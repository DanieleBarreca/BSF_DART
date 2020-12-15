package org.open.medgen.dart.core.controller.report;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.annotation.Annotator;
import org.open.medgen.dart.core.controller.annotation.AnnotatorFactory;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.model.cache.CoverageStatus;
import org.open.medgen.dart.core.model.mongo.aggregations.CoverageResult;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.query.FullQuery;
import org.open.medgen.dart.core.model.query.result.CoverageResultResponse;
import org.open.medgen.dart.core.model.rdbms.dto.PermissionsDTO;
import org.open.medgen.dart.core.model.rdbms.dto.ReportDTO;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.ConditionTermDTO;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.SampleCondition;
import org.open.medgen.dart.core.model.rdbms.entity.report.Report;
import org.open.medgen.dart.core.service.rdbms.service.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ReportController {

    @Inject
    PermissionController permissionService;

    @Inject
    UserService userService;
    
    @Inject
    TransactionalReportController transactionalReportController;
    
    @Inject
    AsynchronousReportService asynchronousReportService;
    
    @Inject
    ReportService reportService;

    @Inject 
    private AnnotatorFactory annotatorFactory;


    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Integer createReportFromQuery(String userName, String userGroup, String queryId) throws AuthorizationException, ControllerException {
        Integer reportId= transactionalReportController.persistReport(userName, userGroup, queryId);

        this.asynchronousReportService.loadReportVariantsWithQuery(queryId, reportId);
        this.asynchronousReportService.loadReportCoverageWithQuery(queryId, reportId);
        
        return reportId;
    }
    
    
    public List<ReportDTO> getAllReportsForUserAndGroup(String userName, String userGroup) throws AuthorizationException {
        PermissionsDTO permissions = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissions==null){
            throw new AuthorizationException();
        }
        
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();
        
        return reportService.retrieveReports(userGroupEntity).stream().map((Report report )-> { 
            ReportDTO result = new ReportDTO(report);

            result.addConditions(
                    reportService.getConditionsForReport(report).stream().map(
                            (SampleCondition sampleCondition) -> new ConditionTermDTO(sampleCondition.getCondition())
                    ).collect(Collectors.toSet()))
            ;
            
            return result;
            }).collect(Collectors.toList());
    }

    public ReportDTO getReport(Integer reportId, String userName, String userGroup) throws AuthorizationException {
        PermissionsDTO permissions = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissions==null){
            throw new AuthorizationException();
        }

        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();

        Report report =  reportService.retrieveReport(reportId);
        if (report == null) return  null;
        
        if (!report.getGroup().equals(userGroupEntity)) throw new AuthorizationException();

        ReportDTO result = new ReportDTO(report);

        result.addConditions(
                reportService.getConditionsForReport(report).stream().map(
                        (SampleCondition sampleCondition) -> new ConditionTermDTO(sampleCondition.getCondition())
                ).collect(Collectors.toSet()));
        
        return result;
    }


    public CoverageResultResponse getCoverageResults(Integer reportId, String userName, String userGroup, Integer first, 
                                                     Integer pageSize, String geneFilter, String statusFilter
    ) throws  AuthorizationException, ControllerException{
        PermissionsDTO permissions = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissions==null){
            throw new AuthorizationException();
        }

        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();

        Report report =  reportService.retrieveReport(reportId);
        if (report == null || !report.getHasCoverage()) return  null;
        if (!report.getGroup().equals(userGroupEntity)) throw new AuthorizationException();
        
        List<CoverageResult> coverageResult = reportService.getCoverageEntriesForReport(report, geneFilter, statusFilter);
        int coverageResultSize = coverageResult.size();
        if (coverageResultSize <= first){
            coverageResult = new LinkedList<>();
        }else {
            coverageResult = coverageResult.subList(first, Math.min(coverageResultSize, first+pageSize ));
        }

        return new CoverageResultResponse(
                CoverageStatus.FINISHED,
                report.getCoverageLoaded().intValue(),
                coverageResultSize, 
                null, 
                coverageResult
        );
    }


    public List<VariantModel> getVariantResults(Integer reportId, String userName, String userGroup) throws  AuthorizationException, ControllerException{
        PermissionsDTO permissions = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissions==null){
            throw new AuthorizationException();
        }

        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();

        Report report =  reportService.retrieveReport(reportId);
        if (report == null || !report.getHasCoverage()) return  null;
        if (!report.getGroup().equals(userGroupEntity)) throw new AuthorizationException();
        ReportDTO reportDTO = new ReportDTO(report);

        Annotator annotator = this.annotatorFactory.getAnnotator(
                reportDTO.getFullQuery(),
                reportDTO.getVcfFileDTO().getRefGenome(),
                userGroup,
                reportDTO.getFullQuery().getSampleRefId(),
                reportDTO.getDate()
        );

        List<VariantModel> variantResult= reportService.getVariantsForReport(report);
        
        variantResult.stream().forEach(annotator::annotate);
        
        return variantResult;
        
    }
}
