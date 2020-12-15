package org.open.medgen.dart.core.service.rdbms.dao;

import org.open.medgen.dart.core.model.mongo.aggregations.CoverageResult;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.Variant;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.VariantSample;
import org.open.medgen.dart.core.model.rdbms.entity.log.Job;
import org.open.medgen.dart.core.model.rdbms.entity.log.JobMessage;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.report.*;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@Stateless
public class ReportDAO extends DAO {

    @Inject AnnotationDAO annotationDAO;

    @Transactional
    public Report retrieveReport(Integer reportId) {
        
        return this.em.find(Report.class, reportId);
    }

    @Transactional
    public Report retrieveLockedReport(Integer reportId) {

        return this.em.find(Report.class, reportId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Transactional
    public void persistReportVariant(ReportVariant reportVariant) {
        em.persist(reportVariant);
        em.flush();
        
    }

    @Transactional
    public void persistReport(Report report) {
        em.persist(report);
        em.flush();
    }

    @Transactional
    public void persistRelatedSample(RelatedSample relatedSample) {
        em.persist(relatedSample);
        em.flush();
    }

    @Transactional
    public void startLoadingVariants(Integer reportId) {
        
            Report report = this.retrieveLockedReport(reportId);

            Job newJob = this.startJob(report.getVariantsJob(), report.getUserFrom());
            
            report.setVariantsJob(newJob);


            em.flush();
    }

    @Transactional
    public void finishLoadingVariants(Integer reportId, Long loadedVariants) {
        
        Report report = this.retrieveLockedReport(reportId);
        
        this.endJob(report.getVariantsJob());

        report.setVariantsLoaded(loadedVariants);

        em.flush();
    }

    @Transactional
    public void finishLoadingVariantsExceptionally(Integer reportId, Throwable ex) {

        Report report = this.retrieveReport(reportId);
        
        this.endJobExceptionally(report.getVariantsJob(), ex);

        em.flush();
    }

    @Transactional
    public void startLoadingCoverage(Integer reportId) {

        Report report = this.retrieveLockedReport(reportId);
        
        Job newJob = this.startJob(report.getCoverageJob(), report.getUserFrom());

        report.setCoverageJob(newJob);

        em.flush();
    }

    @Transactional
    public void finishLoadingCoverage(Integer reportId, Long loadedEntries) {

        Report report = this.retrieveLockedReport(reportId);
        
        this.endJob(report.getCoverageJob());

        report.setCoverageLoaded(loadedEntries);
        
        em.flush();
    }

    @Transactional
    public void finishLoadingCoverageExceptionally(Integer reportId, Throwable ex) {
        Report report = this.retrieveReport(reportId);
        
        this.endJobExceptionally(report.getCoverageJob(), ex);
    }

    @Transactional
    public ReportVariant addVariantToReport(VariantModel variant, Integer reportId) throws RDBMSServiceException {
        Report report = this.retrieveReport(reportId);

        VariantSample variantSample = this.retrieveOrSaveVariantSample(variant, report.getSample());

        ReportVariantPK reportVariantPK = new ReportVariantPK(report, variantSample);

        ReportVariant reportVariant = new ReportVariant();
        reportVariant.setReporVariantPK(reportVariantPK);
        reportVariant.setVariantData(variant);

        em.persist(reportVariant);
        em.flush();

        return reportVariant;

    }

    @Transactional
    public Long getTotalVariantsForReport(Integer reportId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> resultCriteria = cb.createQuery(Long.class);
        
        Root<ReportVariant> queryRoot = resultCriteria.from(ReportVariant.class);
        Join<ReportVariant,ReportVariantPK> reportVariantPk = queryRoot.join(ReportVariant_.reporVariantPK);
        Join<ReportVariantPK,Report> report = reportVariantPk.join(ReportVariantPK_.report);

        resultCriteria.select(cb.count(queryRoot));
        resultCriteria.where(
                cb.equal(
                        report.get(Report_.reportId),reportId
                )
        );

        return em.createQuery(resultCriteria).getSingleResult();
    }

    @Transactional
    public Long getTotalCoverageEntriesForReport(Integer reportId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> resultCriteria = cb.createQuery(Long.class);

        Root<ReportCoverage> queryRoot = resultCriteria.from(ReportCoverage.class);
        Join<ReportCoverage,Report> report = queryRoot.join(ReportCoverage_.report);

        resultCriteria.select(cb.count(queryRoot));
        resultCriteria.where(
                cb.equal(
                        report.get(Report_.reportId),reportId
                )
        );

        return em.createQuery(resultCriteria).getSingleResult();
    }

    @Transactional
    public ReportCoverage addCoverageToReport(CoverageResult entry, Integer reportId) {
        Report report = this.retrieveReport(reportId);
        
        ReportCoverage reportCoverage = new ReportCoverage();

        reportCoverage.setReport(report);
        reportCoverage.setCoverageData(entry);

        em.persist(reportCoverage);
        em.flush();

        return reportCoverage;
    }

    @Transactional
    private Job startJob(Job latestJob, User user) {
        Job newJob = new Job();
        newJob.setCreationDate(new Date());
        newJob.setType(Job.JobType.REPORT_VARIANTS);
        newJob.setUser(user);

        if (latestJob != null) {
            newJob.setPreviousJob(latestJob);
        }

        em.persist(newJob);
        em.flush();

        JobMessage jobMessage = new JobMessage();
        jobMessage.setDateTime(new Date());
        jobMessage.setJob(newJob);
        jobMessage.setMessage("Job Started");
        jobMessage.setMessageType(JobMessage.Type.INFO);

        em.persist(jobMessage);
        em.flush();

        return newJob;
    }

    @Transactional
    private void endJob(Job job) {
        JobMessage jobMessage = new JobMessage();
        jobMessage.setDateTime(new Date());
        jobMessage.setJob(job);
        jobMessage.setMessage("Job Finished");
        jobMessage.setMessageType(JobMessage.Type.INFO);

        em.persist(jobMessage);
        em.flush();
    }

    @Transactional
    private void endJobExceptionally(Job job, Throwable ex) {
        JobMessage jobMessage = new JobMessage();
        jobMessage.setDateTime(new Date());
        jobMessage.setJob(job);
        jobMessage.setMessage(String.format("%s: %s", ex.getClass().getCanonicalName(), ex.getMessage()));
        jobMessage.setMessageType(JobMessage.Type.ERROR);

        em.persist(jobMessage);
        em.flush();
    }



    @Transactional
    private VariantSample retrieveOrSaveVariantSample(
            VariantModel variantModel,
            VCFSample sample) throws RDBMSServiceException {

        Variant variant = annotationDAO.retrieveOrSaveVariant(
                sample.getVcf().getRefGenome(),
                variantModel.getAttributeAsString(VariantModel.GENOMIC_CHANGE_FIELD),
                variantModel.getAttributeAsString(VariantModel.CODING_CHANGE_FIELD),
                variantModel.getAttributeAsString(VariantModel.GENE_FIELD));

        VariantSample variantSample = annotationDAO.retrieveOrPersistVariantSample(variant, sample);
        annotationDAO.getEm().flush();

        return variantSample;

    }


    public List<Report> retrieveAllReports(UserGroup userGroup) {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Report> resultCriteria = cb.createQuery(Report.class);

        Root<Report> queryRoot = resultCriteria.from(Report.class);
        resultCriteria.where(
                cb.equal(
                        queryRoot.get(Report_.group),
                        userGroup
                )
        );
        
        resultCriteria.orderBy(cb.desc(queryRoot.get(Report_.dateFrom)));

        return em.createQuery(resultCriteria).getResultList();
        
    }

    public List<ReportCoverage> getCoverageEntriesForReport(Report report, String geneFilter, String statusFilter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<ReportCoverage> resultCriteria = cb.createQuery(ReportCoverage.class);

        Root<ReportCoverage> queryRoot = resultCriteria.from(ReportCoverage.class);
        resultCriteria.where(
                cb.equal(queryRoot.get(ReportCoverage_.report),report)
        );
        
        return em.createQuery(resultCriteria).getResultList();
    }

    public List<ReportVariant> getVariantsForReport(Report report) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<ReportVariant> resultCriteria = cb.createQuery(ReportVariant.class);
        
        Root<ReportVariant> reportVariant = resultCriteria.from(ReportVariant.class);
        Join<ReportVariant, ReportVariantPK> reportVariantPK = reportVariant.join(ReportVariant_.reporVariantPK);
                
        resultCriteria.where(
                cb.equal(reportVariantPK.get(ReportVariantPK_.report),report)
        );

        return em.createQuery(resultCriteria).getResultList();
    }
}
