package org.open.medgen.dart.core.controller.report;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.model.cache.CachedQuery;
import org.open.medgen.dart.core.model.query.RelatedSampleInfo;
import org.open.medgen.dart.core.model.rdbms.dto.PermissionsDTO;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.QueryPreset;
import org.open.medgen.dart.core.model.rdbms.entity.report.Report;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.cache.CacheService;
import org.open.medgen.dart.core.service.rdbms.service.DbVCFService;
import org.open.medgen.dart.core.service.rdbms.service.PresetService;
import org.open.medgen.dart.core.service.rdbms.service.ReportService;
import org.open.medgen.dart.core.service.rdbms.service.UserService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Stateless
public class TransactionalReportController {

    @Inject
    PermissionController permissionController;

    @Inject
    UserService userService;

    @Inject
    PresetService presetService;

    @Inject
    CacheService cacheService;

    @Inject
    DbVCFService vcfService;

    @Inject
    ReportService reportService;


    @Transactional
    public Integer persistReport(String userName, String userGroup, String queryId) throws AuthorizationException, ControllerException {
        PermissionsDTO permissions = permissionController.getPermissionsForUser(userName, userGroup);
        if (permissions == null) {
            throw new AuthorizationException();
        }
        
        if (!permissions.isCanSaveReport()) throw new AuthorizationException();
        
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();

        CachedQuery cachedQuery = cacheService.retrieveQueryForUserGroupWithId(userName, userGroup, queryId);
        if (cachedQuery == null) throw new ControllerException("Cached query not found");
        if (!cachedQuery.getUserInfo().getUserName().equals(userName)) throw new AuthorizationException();

        QueryPreset queryPreset = presetService.getPreset(userGroupEntity, cachedQuery.getTheQuery().getQueryFilter().getFilter());
        if (queryPreset == null) throw new ControllerException("Query preset not found");

        PanelPreset panelPreset= presetService.getPanelPreset(userGroupEntity,cachedQuery.getTheQuery().getPanel());
        if (panelPreset == null) throw new ControllerException("Panel preset not found");

        VCFSample sample = vcfService.retrieveVCFSampleEntity(cachedQuery.getTheQuery().getSampleRefId());
        Report report =  reportService.createReportForSample(queryPreset,panelPreset, sample, user, userGroupEntity);
        
        for (RelatedSampleInfo relatedSampleInfo :cachedQuery.getTheQuery().getRelatedSamples()) {
            
            if (relatedSampleInfo.getSample()!=null && !relatedSampleInfo.getSample().trim().isEmpty()) {

                VCFSample relatedSample = sample.getVcf().getSampleList().stream().filter(
                        (VCFSample otherSample) -> otherSample.getSampleName().equals(relatedSampleInfo.getSample().trim())
                ).findFirst().get();

                reportService.addRelatedSampleToReport(report, relatedSample, relatedSampleInfo.getSex(), relatedSampleInfo.isAffected());
            }
        }
        
        return  report.getReportId();
        
    }
}
