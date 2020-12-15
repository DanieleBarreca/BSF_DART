/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.variant;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.annotation.Annotator;
import org.open.medgen.dart.core.controller.annotation.AnnotatorFactory;
import org.open.medgen.dart.core.controller.bed.BedGenomeVersion;
import org.open.medgen.dart.core.model.mongo.aggregations.CoverageResult;
import org.open.medgen.dart.core.model.query.FullQuery;
import org.open.medgen.dart.core.model.query.QueryFilter;
import org.open.medgen.dart.core.model.query.QueryPanel;
import org.open.medgen.dart.core.model.query.result.CountResultResponse;
import org.open.medgen.dart.core.model.query.result.CoverageResultResponse;
import org.open.medgen.dart.core.model.query.result.FilterResultResponse;
import org.open.medgen.dart.core.model.query.result.VariantResultResponse;
import org.open.medgen.dart.core.model.cache.CachedQuery;
import org.open.medgen.dart.core.model.mongo.aggregations.VariantResult;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.PermissionsDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.SampleDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.QueryPreset;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFile;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.cache.CacheService;
import org.open.medgen.dart.core.service.cache.CachingResult;
import org.open.medgen.dart.core.service.cache.EntityNotFoundException;
import org.open.medgen.dart.core.service.mongo.exception.MongoServiceException;
import org.open.medgen.dart.core.service.mongo.exception.QueryParsingException;
import org.open.medgen.dart.core.service.mongo.parser.QueryValidator;
import org.open.medgen.dart.core.service.mongo.service.AsyncCoverageService;
import org.open.medgen.dart.core.service.mongo.service.AsyncVariantService;
import org.open.medgen.dart.core.service.mongo.service.SyncCoverageService;
import org.open.medgen.dart.core.service.mongo.service.SyncVariantService;
import org.open.medgen.dart.core.controller.permission.PermissionController;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.bson.types.ObjectId;
import org.open.medgen.dart.core.service.rdbms.service.DbVCFService;
import org.open.medgen.dart.core.service.rdbms.service.PresetService;

/**
 *
 * @author dbarreca
 */

@Stateless
@Default
public class VariantController {
    private final static Logger LOGGER = Logger.getLogger(VariantController.class.getName());
    
    @Inject private CacheService cacheService;
    
    @Inject private PermissionController permissionService;
    
    @Inject private AsyncVariantService asyncVariantService;

    @Inject private AsyncCoverageService asyncCoverageService;

    @Inject private SyncVariantService syncVariantService;

    @Inject private SyncCoverageService syncCoverageService;

    @Inject private DbVCFService vcfService;
    
    @Inject private AnnotatorFactory annotatorFactory;

    @Inject private PresetService presetService;
    
    @Transactional
    public String submitQuery(FullQuery query, String user, String userGroup, String userToken) throws AuthorizationException, ControllerException {
        
        LOGGER.info("Checking permissions ...");
        PermissionsDTO permissions = permissionService.getPermissionsForUser(user, userGroup);
        if (permissions==null || (permissions.isPublicUser() && (userToken== null || userToken.trim().isEmpty()))){
            throw new AuthorizationException();
        }
        
        if (!permissions.isCanSavePanel()){
            if (query.getPanel().getRefId()==null){
                throw new AuthorizationException("User cannot run a query without a preset panel!");
            }else{
                PanelPreset panel = presetService.getPanelPreset(query.getPanel().getRefId());
                if (panel == null || !userGroup.equals(panel.getUserGroup().getGroup())){
                    throw new AuthorizationException("User is not authorized for the selected panel!");
                }
                query.setPanel(new QueryPanel(panel));
            }
        }

        if (!permissions.isCanSavePreset()) {
            if (query.getQueryFilter().getRefId() == null) {
                throw new AuthorizationException("User cannot run a query without a preset query!");
            } else {
                QueryPreset preset = presetService.getPreset(query.getQueryFilter().getRefId());
                if (preset == null || !userGroup.equals(preset.getUserGroup().getGroup())) {
                    throw new AuthorizationException("User is not authorized for the selected query!");
                }
                query.setQueryFilter(new QueryFilter(preset));
            }
        }
        
        
        try {
            QueryValidator.validate(query);
        } catch (QueryParsingException ex) {
            throw new MalformedQueryException(ex);
        }
       
        
        VCFFileDTO theVCF;
        try {
            theVCF = permissionService.getVcfForQuery(query.getVcfFileId(), user,userGroup);
            if (theVCF == null) throw new AuthorizationException();
        } catch (org.open.medgen.dart.core.controller.permission.EntityNotFoundException ex) {
            throw new ControllerException(ex);
        }
        query.setVcfFileName(theVCF.getVcfFileName());

        SampleDTO sampleToQuery = null;
        for (SampleDTO sample: theVCF.getSamples()){
            if (sample.getDbId().equals(query.getSampleRefId())){
                sampleToQuery = sample;
                break;
            }
        }
        if (sampleToQuery == null) throw new ControllerException("Sample not found in the VCF file");
        query.setSample(sampleToQuery.getSampleName());

        if (!MongoFileStatus.AVAILABLE.equals(theVCF.getStatus())) throw new ControllerException("The VCF is not loaded in MongoDB");
        
        ObjectId vcfId = theVCF.getMongoId();
        if (vcfId == null) throw new ControllerException("The VCF is not loaded in MongoDB");
        
        String bedId = null;
        if (query.getPanel().getBedFileId()!=null){
            BedFile theBed;
            try {
                theBed = permissionService.getBedForQuery(query.getPanel().getBedFileId(), user, userGroup);
            } catch (org.open.medgen.dart.core.controller.permission.EntityNotFoundException ex) {
                throw new ControllerException(ex);
            }
            
            if (!MongoFileStatus.AVAILABLE.equals(theBed.getStatus())) throw new ControllerException("The BED file is not available");
            
            bedId = theBed.getMongoId();
            if (bedId == null || bedId.isEmpty()) throw new ControllerException("The BED file is not loaded in MongoDB");
            
            String vcfGenome=BedGenomeVersion.getCorrectedVersion(theVCF.getRefGenome()).toLowerCase();
            String bedGenome=theBed.getGenome().toLowerCase();
            
            if (!vcfGenome.equals(bedGenome)){
                throw new ControllerException(String.format(
                        "The BED genome version %s does not match the VCF genome version %s",
                        bedGenome,
                        vcfGenome
                ));
            }
        }

        LOGGER.info("Checking cache ...");
        final CachingResult cached = cacheService.addQuery(query, user,userGroup, sampleToQuery.getCoverageMongoId()!=null, userToken);

        if (cached.toRun()) {

            LOGGER.info("Prepare count query execution ...");
            try{
                asyncVariantService.executeCountQuery(
                        vcfId,
                        sampleToQuery.getSampleName(),
                        bedId,
                        query.getPanel().getGeneList(),
                        query.getQueryFilter().getFilter(),
                        (Long result) -> cacheService.completeCountResult(cached.getQuery(), result),
                        (Throwable t) -> cacheService.completeCountResultsWithError(cached.getQuery(), t),
                        () -> cacheService.completeCountResultsWithTimeout(cached.getQuery())
                );
            }catch(MongoServiceException e){
                try{
                    LOGGER.log(Level.WARNING, "problem with count query", e);
                    cacheService.completeQueryResultsWithError(cached.getQuery(), e);
                }catch(EntityNotFoundException e2){}
            }

            LOGGER.info("Prepare query execution ...");
            try{
                asyncVariantService.executeQuery(
                        vcfId,
                        sampleToQuery.getSampleName(),
                        bedId,
                        query.getPanel().getGeneList(),
                        query.getQueryFilter().getFilter(),
                        (VariantResult result) -> cached.getQuery().addResult(result.getId()),
                        (Throwable t) -> cacheService.completeQueryResultsWithError(cached.getQuery(), t),
                        () -> cacheService.completeQueryResults(cached.getQuery())
                );
            }catch(MongoServiceException e){
                try{
                    LOGGER.log(Level.WARNING, "problem with data query", e);
                    cacheService.completeQueryResultsWithError(cached.getQuery(), e);
                }catch(EntityNotFoundException e2){}
            }

            if (cached.getQuery().isHasCoverage()) {
                LOGGER.info("Prepare coverage query execution ...");
                try {
                    asyncCoverageService.executeCoverageQuery(
                            sampleToQuery.getCoverageMongoId(),
                            bedId,
                            query.getPanel().getGeneList(),
                            null,
                            (CoverageResult result) -> cached.getQuery().addCoverageResult(result.getId()),
                            (Throwable t) -> cacheService.completeCoverageResultsWithError(cached.getQuery(), t),
                            () -> cacheService.completeCoverageResults(cached.getQuery())
                    );
                } catch (MongoServiceException e) {
                    try {
                        LOGGER.log(Level.WARNING, "problem with coverage query", e);
                        cacheService.completeCoverageResultsWithError(cached.getQuery(), e);
                    } catch (EntityNotFoundException e2) {
                    }
                }
            }
        }
        LOGGER.info("Done submitting queries!");
        return cached.getQuery().getQueryUUID();        
    }
    
    @Transactional
    public FilterResultResponse getResults(String queryUUID, String user, String userGroup, int first, Integer pageSize) throws AuthorizationException, ControllerException {
        CachedQuery queryResults = cacheService.retrieveQueryForUserWithId(user, queryUUID);
        if (queryResults == null) {
            throw new AuthorizationException();
        }
        if (permissionService.getPermissionsForUser(user, userGroup) == null) {
            throw new AuthorizationException();
        }
        
        
        VCFFile vcfFile= vcfService.retrieveVCFEntity(queryResults.getTheQuery().getVcfFileId());
        String refGenome = vcfFile.getRefGenome();

        try {
            return new FilterResultResponse(
                    this.getCountResult(queryResults),
                    this.getVariantResults(queryResults, first, pageSize, userGroup, refGenome),
                    this.getCoverageResult(queryResults,0,0,null, null ));
        }catch (EntityNotFoundException e){
            throw new ControllerException(e);
        }

    }

    @Transactional
    public CoverageResultResponse getCoverageResults(String queryUUID, String user, int first, int pageSize, String geneFilter,String statusFilter) throws AuthorizationException, ControllerException {
        CachedQuery queryResults = cacheService.retrieveQueryForUserWithId(user, queryUUID);
        if (queryResults == null) {
            throw new AuthorizationException();
        }

        return this.getCoverageResult(queryResults,first, pageSize,geneFilter,statusFilter);
    }

    private VariantResultResponse getVariantResults(CachedQuery queryResults, int first, Integer pageSize, String userGroup, String refGenome) throws EntityNotFoundException {
        VariantResultResponse resultResponse;
        switch (queryResults.getResultStatus()) {
            case FINISHED:
                Integer totalCount = queryResults.getResults().size();

                int last;
                if (pageSize != null)
                    last = Math.min(first + pageSize, totalCount) - 1;
                else
                    last = totalCount -1;

                if ( (last < first) || last < 0 || first<0) {
                    resultResponse = new VariantResultResponse(queryResults.getResultStatus(), totalCount, null, null);
                } else {

                    List<VariantModel> results;
                    
                    Annotator annotator = annotatorFactory.getAnnotator(queryResults.getTheQuery(),refGenome, userGroup, queryResults.getTheQuery().getSampleRefId());                     
                     
                    results = new LinkedList<>();
                    syncVariantService.getVariantsIterator(
                            queryResults.getResults().subList(first, last + 1),
                            annotator
                    ).forEachRemaining(results::add);
                    

                    resultResponse = new VariantResultResponse(queryResults.getResultStatus(), totalCount, null, results);

                }
                break;
            case ERROR:
                resultResponse = new VariantResultResponse(queryResults.getResultStatus(), null, queryResults.getResultError(), null);
                break;
            default:
                resultResponse = new VariantResultResponse(queryResults.getResultStatus(), null, null, null);
                break;
        }

        return resultResponse;
    }
    
    private CountResultResponse getCountResult(CachedQuery queryResults) {
        CountResultResponse countResult;
        switch (queryResults.getCountStatus()) {
            case FINISHED:
                countResult = new CountResultResponse(queryResults.getCountStatus(),queryResults.getTotalCount(), null);
                break;
            case ERROR:
                countResult = new CountResultResponse(queryResults.getCountStatus(),null, queryResults.getCountError());
                break;
            default:
                countResult = new CountResultResponse(queryResults.getCountStatus(),null, null);
                break;
        }

        return countResult;
    }

    private CoverageResultResponse getCoverageResult(CachedQuery queryResults, int first, int pageSize, String geneFilter, String statusFilter) {
        CoverageResultResponse coverageResult;

        switch (queryResults.getCoverageStatus()) {
            case FINISHED:
                
                
                List<CoverageResult> results = new LinkedList<>();
                Integer filteredCount = queryResults.getCoverageResults().size();
                
                if (pageSize!=0) {
                    if (geneFilter!=null || statusFilter!=null) {
                        syncCoverageService.getCoverageIterator(queryResults.getCoverageResults(), geneFilter, statusFilter).forEachRemaining(
                                results::add
                        );

                        filteredCount = results.size();
                        int last = Math.min(first + pageSize, filteredCount);

                        if (last > first && last > 0) {
                            results = results.subList(first, last);
                        } else {
                            results = new LinkedList<>();
                        }
                    }else {
                        int last = Math.min(first + pageSize, queryResults.getCoverageResults().size());
                        if (last > first && last > 0) {
                            syncCoverageService.getCoverageIterator(queryResults.getCoverageResults().subList(first, last), null,  null).forEachRemaining(
                                    results::add
                            );
                        } 
                    }
                }

                coverageResult = new CoverageResultResponse(queryResults.getCoverageStatus(),queryResults.getCoverageResults().size(),filteredCount, null, results);
                break;
            case ERROR:
                coverageResult = new CoverageResultResponse(queryResults.getCoverageStatus(),null, queryResults.getCoverageResultError(), null);
                break;
            default: //RUNNING, NOT PRESENT
                coverageResult = new CoverageResultResponse(queryResults.getCoverageStatus(),null, null, null);
                break;
        }

        return coverageResult;
    }
   
}
