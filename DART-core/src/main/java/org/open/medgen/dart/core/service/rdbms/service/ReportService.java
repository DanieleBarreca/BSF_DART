package org.open.medgen.dart.core.service.rdbms.service;

import org.open.medgen.dart.core.controller.annotation.AnnotatorFactory;
import org.open.medgen.dart.core.controller.annotation.RelatedSamplesAnnotator;
import org.open.medgen.dart.core.model.mongo.aggregations.CoverageResult;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.SampleCondition;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.QueryPreset;
import org.open.medgen.dart.core.model.rdbms.entity.report.*;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.rdbms.dao.AnnotationDAO;
import org.open.medgen.dart.core.service.rdbms.dao.ReportDAO;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ReportService {
    
    @Inject
    ReportDAO reportDAO;
    
    @Inject
    AnnotationDAO annotationDAO;
    
    @Transactional
    public Report createReportForSample(
            QueryPreset queryPreset, 
            PanelPreset panelPreset, 
            VCFSample patientSample,
            User user, 
            UserGroup userGroup) {
        
        Report report = new Report();
        
        report.setSample(patientSample);
        report.setGroup(userGroup);
        report.setUserFrom(user);
        report.setDateFrom(new Date());
        
        report.setHasCoverage(patientSample.getCoverageMongoId()!=null);
        report.setQuery(queryPreset);
        report.setPanel(panelPreset);
        
        reportDAO.persistReport(report);
        
        return report;
        
    }

    @Transactional
    public Report addRelatedSampleToReport(
            Report report,
            VCFSample sample,
            RelatedSamplesAnnotator.RelatedSampleSex sex,
            boolean affected) {

        RelatedSamplePk pk = new RelatedSamplePk(report, sample);
        RelatedSample relatedSample = new RelatedSample();
        relatedSample.setId(pk);
        relatedSample.setAffected(affected);
        relatedSample.setSex(sex);
        
        reportDAO.persistRelatedSample(relatedSample);

        return report;

    }
    
    public List<Report> retrieveReports(UserGroup userGroup) {
        return  reportDAO.retrieveAllReports(userGroup);
    }

    public Report retrieveReport(Integer reportId) {
        return reportDAO.retrieveReport(reportId);
    }

    public Collection<SampleCondition> getConditionsForReport(Report report) {
        return annotationDAO.retrieveConditionsForSample(report.getSample(), report.getGroup(), report.getDateFrom());
    }

    public List<CoverageResult> getCoverageEntriesForReport(Report report, String geneFilter, String statusFilter) {
        List<ReportCoverage> queryResult = reportDAO.getCoverageEntriesForReport(report, geneFilter, statusFilter);
        return queryResult.stream().map(ReportCoverage::getCoverageData).filter(
                    reportCoverage -> {
                        boolean genePass = true;
                        boolean statusPass = true;
                        if (geneFilter!=null){
                            genePass = reportCoverage.getGenes().stream().anyMatch(
                                    gene -> gene.matches(String.format("(?i).*%s.*",geneFilter))
                            );
                        }
                        if (statusFilter!=null){
                            statusPass = reportCoverage.getMappingStatus().toString().matches(String.format("(?i).*%s.*",statusFilter));
                        }
                        
                        return genePass && statusPass;
                    }
                    
            ).collect(Collectors.toList());
    }

    public List<VariantModel> getVariantsForReport(Report report) {
        return reportDAO.getVariantsForReport(report).stream().map(
                ReportVariant::getVariantData
        ).collect(Collectors.toList());
    }
}
