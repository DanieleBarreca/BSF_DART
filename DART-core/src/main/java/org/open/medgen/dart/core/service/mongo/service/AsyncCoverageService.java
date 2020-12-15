/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.service;

import com.mongodb.async.client.AggregateIterable;
import com.mongodb.async.client.MongoCollection;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.open.medgen.dart.core.model.mongo.aggregations.CoverageResult;
import org.open.medgen.dart.core.model.mongo.bed.BedEntry;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;
import org.open.medgen.dart.core.service.cache.CacheConsumer;
import org.open.medgen.dart.core.service.cache.CacheRunnable;
import org.open.medgen.dart.core.service.cache.EntityNotFoundException;
import org.open.medgen.dart.core.service.mongo.exception.MongoServiceException;
import org.open.medgen.dart.core.service.mongo.provider.AsyncCollectionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static org.open.medgen.dart.core.service.mongo.service.MongoUtils.*;

/**
 *
 * @author dbarreca
 */
@Stateless
public class AsyncCoverageService {
    private final static Logger LOGGER = Logger.getLogger(AsyncCoverageService.class.getName());

    @Inject
    AsyncCollectionFactory collectionFactory;

    MongoCollection<CoverageEntry> coverageCollection;

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing AsyncCoverageService...");
        coverageCollection = collectionFactory.getCollection(CoverageEntry.class);
    }
    
    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down AsyncCoverageService...");

    }
    
    public void executeCoverageQuery(
            String coverageFileId,
            String bedFileId,
            Set<String> additionalGenes,
            Set<String> additionalTranscripts,
            CacheConsumer<CoverageResult> coverageConsumer,
            CacheConsumer<Throwable> onError,
            CacheRunnable onComplete
            
    ) throws MongoServiceException {

        List<Bson> aggregationSteps = getAggregationSteps(coverageFileId, bedFileId, additionalGenes, additionalTranscripts);

        LOGGER.info("Executing coverage aggregation query ...");
        AggregateIterable<CoverageResult> iterable = coverageCollection.aggregate(aggregationSteps, CoverageResult.class);

        LOGGER.info("Iterate over coverage results ...");
        iterable.forEach(
                (CoverageResult coverageResult) -> {
                    try{
                        coverageConsumer.consume(coverageResult);
                    }catch (EntityNotFoundException e){
                        throw new VariantIterationError(e);
                    }
                },
                (Void result, Throwable t) -> {
                    try{
                        if (t != null) {
                            if (t.getCause() instanceof EntityNotFoundException){
                                LOGGER.log(Level.SEVERE,"The count query was cancelled since no cached query was found",t);
                            }else{
                                LOGGER.log(Level.SEVERE, "Error while executing coverage query", t);
                                onError.consume(t);
                            }
                        } else {
                            LOGGER.info("Coverage query execution completed.");
                            onComplete.run();
                        }
                    }catch(EntityNotFoundException e){
                        LOGGER.info("The coverage query was already cancelled. Nothing to do");
                        //DO NOTHING
                    }
                }
        );
        LOGGER.info("Prepared collection of coverage results from query!");
    }
    
    private List<Bson> getAggregationSteps(
            String coverageFileId,
            String bedFileId,
            Set<String> geneNames,
            Set<String> transcriptNames
        ) {

        boolean hasGenes = geneNames!=null && !geneNames.isEmpty();
        boolean hasTranscripts = transcriptNames!=null && !transcriptNames.isEmpty();

        List<Bson> aggregations = new LinkedList();

        Bson theQuery = eq(CoverageEntry.COVERAGE_FILE, new ObjectId(coverageFileId));
        aggregations.add(match(theQuery));

        if (bedFileId!=null) {
            aggregations.addAll(getBedJoinPipeline(bedFileId,geneNames,transcriptNames));
        }else if (hasGenes && !hasTranscripts) {
            aggregations.add(match(in(CoverageEntry.GENE_NAMES, geneNames)));
        }else if (!hasGenes && hasTranscripts){
            aggregations.add(match(in(CoverageEntry.TRANSCRIPT_IDS, transcriptNames)));
        }else if (hasGenes && hasTranscripts){
            aggregations.add(match(or(
                    in(CoverageEntry.TRANSCRIPT_IDS, transcriptNames),
                    in(CoverageEntry.GENE_NAMES, geneNames)
            )));
        }

        aggregations.add(project(fields(include(CoverageEntry.CHROM, CoverageEntry.START, CoverageEntry.END, CoverageEntry.GENE_NAMES, CoverageEntry.MAPPING_STATUS))));

        return aggregations;
    }

    private List<Bson> getBedJoinPipeline(String bedFileId,
                                          Set<String> geneNames,
                                          Set<String> transcriptNames) {

        boolean hasGenes = geneNames!=null && !geneNames.isEmpty();
        boolean hasTranscripts = transcriptNames!=null && !transcriptNames.isEmpty();

        String joinFieldName = "joinKey";
        String bedRegionsArrayName = "regions";
        String tempFieldName = "temp";
       
        List<Bson> aggregations = new LinkedList();
        
        //Calculate the join Key - 'Bukcetize' position in 1mio sized buckets
        Bson joinKey = concat(bedFileId, "-", valueOf(CoverageEntry.BUCKET));
        aggregations.add(project(fields(include(CoverageEntry.CHROM, CoverageEntry.START, CoverageEntry.END, CoverageEntry.GENE_NAMES, CoverageEntry.TRANSCRIPT_IDS, CoverageEntry.MAPPING_STATUS),computed(joinFieldName, joinKey))));

        //Perform Join
        aggregations.add(lookup(
                collectionFactory.getCollectionName(BedEntry.class),
                joinFieldName, 
                BedEntry.LOOKUP_KEY, 
                bedRegionsArrayName
        ));

        //Filter only buckets which contains the position
        Bson filter = genericExpr(
                MongoUtils.OR,
                genericExpr(MongoUtils.AND,
                        genericExpr(MongoUtils.GTE, toVar(tempFieldName+"."+BedEntry.START), valueOf(CoverageEntry.START)),
                        genericExpr(MongoUtils.LTE, toVar(tempFieldName+"."+BedEntry.START), valueOf(CoverageEntry.END))
                ),
                genericExpr(MongoUtils.AND,
                        genericExpr(MongoUtils.GTE, toVar(tempFieldName+"."+BedEntry.END), valueOf(CoverageEntry.START)),
                        genericExpr(MongoUtils.LTE, toVar(tempFieldName+"."+BedEntry.END), valueOf(CoverageEntry.END))
                ),
                genericExpr(MongoUtils.AND,
                        genericExpr(MongoUtils.LTE, toVar(tempFieldName+"."+BedEntry.START), valueOf(CoverageEntry.START)),
                        genericExpr(MongoUtils.GTE, toVar(tempFieldName+"."+BedEntry.END), valueOf(CoverageEntry.END))
                )
        );
        
        aggregations.add(project(fields(
                include(CoverageEntry.CHROM, CoverageEntry.START, CoverageEntry.END, CoverageEntry.GENE_NAMES, CoverageEntry.TRANSCRIPT_IDS, CoverageEntry.MAPPING_STATUS),
                computed(bedRegionsArrayName, filter(valueOf(bedRegionsArrayName),tempFieldName, filter))
        )));

        //Select only variants in relevant regions
        Bson sizeConstraint = not(size(bedRegionsArrayName, 0));
        if (!hasGenes && !hasTranscripts){
            aggregations.add(match(sizeConstraint));
        }else if (hasGenes && !hasTranscripts){
            aggregations.add(
                    match(or(
                            sizeConstraint,
                            in(CoverageEntry.GENE_NAMES, geneNames)
                    ))
            );
        }else if (!hasGenes) {
            aggregations.add(
                    match(or(
                            sizeConstraint,
                            in(CoverageEntry.TRANSCRIPT_IDS, transcriptNames)
                    ))
            );
        }else {
            aggregations.add(
                    match(or(
                            sizeConstraint,
                            in(CoverageEntry.GENE_NAMES, geneNames),
                            in(CoverageEntry.TRANSCRIPT_IDS, transcriptNames)
                    ))
            );
        }


        return aggregations;
    }
    
}
