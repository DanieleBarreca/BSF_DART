package org.open.medgen.dart.core.service.rdbms.service;

import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.model.cache.CachedQuery;
import org.open.medgen.dart.core.model.mongo.aggregations.CoverageResult;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.service.cache.CacheService;
import org.open.medgen.dart.core.service.mongo.service.SyncCoverageService;
import org.open.medgen.dart.core.service.mongo.service.SyncVariantService;
import org.open.medgen.dart.core.service.rdbms.dao.AnnotationDAO;
import org.open.medgen.dart.core.service.rdbms.dao.DAO;
import org.open.medgen.dart.core.service.rdbms.dao.ReportDAO;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;



@Stateless
public class AsynchronousReportService extends DAO {

    private static final Logger LOG = Logger.getLogger(AsynchronousReportService.class.getName());
    
    @Inject
    ReportDAO reportDAO;

    @Inject
    private SyncVariantService syncVariantService;

    @Inject
    private SyncCoverageService syncCoverageService;
    
    @Inject
    private CacheService cacheService;

    @Asynchronous
    @Transactional(value= Transactional.TxType.NOT_SUPPORTED)
    public void loadReportVariantsWithQuery(String theQueryId, Integer reportId) throws ControllerException {

        CachedQuery cachedQuery = cacheService.retrieveQueryWithId(theQueryId);
        if (cachedQuery == null) throw new ControllerException("Cached query not found");
        
        reportDAO.startLoadingVariants(reportId);
        

        try {


            Iterator<VariantModel> variants = syncVariantService.getVariantsIterator(cachedQuery.getResults(), null);

            int i = 0;
            while (variants.hasNext()) {
                VariantModel variant = variants.next();
                reportDAO.addVariantToReport(variant, reportId);
                if (i % 100 == 0){
                    LOG.log(Level.INFO,"Loaded "+i+" variants for report "+reportId);
                }
                i++;
            }

            reportDAO.finishLoadingVariants(reportId, reportDAO.getTotalVariantsForReport(reportId));

        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, "Error while loading report with id " + reportId, ex);
            reportDAO.finishLoadingVariantsExceptionally(reportId, ex);
        }

    }

  
    @Asynchronous
    @Transactional(value= Transactional.TxType.NOT_SUPPORTED)
    public void loadReportCoverageWithQuery(String theQueryId, Integer reportId) throws  ControllerException {

        CachedQuery cachedQuery = cacheService.retrieveQueryWithId(theQueryId);
        if (cachedQuery == null) throw new ControllerException("Cached query not found");

        reportDAO.startLoadingCoverage(reportId);

        int i = 0;
        
        try {


            Iterator<CoverageResult> coverageEntries = syncCoverageService.getCoverageIterator(cachedQuery.getCoverageResults(),null,null);

            while (coverageEntries.hasNext()) {
                CoverageResult entry = coverageEntries.next();
                reportDAO.addCoverageToReport(entry, reportId);
                if (i % 100 == 0){
                    LOG.log(Level.INFO,"Loaded "+i+" coverage entries for report "+reportId);
                }
                i++;
            }


            reportDAO.finishLoadingCoverage(reportId, reportDAO.getTotalCoverageEntriesForReport(reportId));

        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, "Error while loading report with id " + reportId, ex);
            reportDAO.finishLoadingCoverageExceptionally(reportId, ex);
        }

    }
    
  




}