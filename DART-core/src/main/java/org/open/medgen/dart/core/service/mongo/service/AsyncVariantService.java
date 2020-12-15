/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.service;

import org.open.medgen.dart.core.controller.bed.BEDParser;
import org.open.medgen.dart.core.model.query.QueryRule;
import org.open.medgen.dart.core.model.mongo.aggregations.Count;
import org.open.medgen.dart.core.model.mongo.aggregations.VariantResult;
import org.open.medgen.dart.core.model.mongo.bed.BedEntry;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.service.cache.CacheConsumer;
import org.open.medgen.dart.core.service.cache.CacheRunnable;
import org.open.medgen.dart.core.service.cache.EntityNotFoundException;
import org.open.medgen.dart.core.service.mongo.exception.MongoServiceException;
import org.open.medgen.dart.core.service.mongo.parser.BsonQueryParser;
import org.open.medgen.dart.core.service.mongo.provider.AsyncCollectionFactory;
import org.open.medgen.dart.core.service.mongo.provider.QueryLimit;
import com.mongodb.async.client.MongoCollection;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Filters.*;
import static org.open.medgen.dart.core.service.mongo.service.MongoUtils.*;
import com.mongodb.async.client.AggregateIterable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Sorts.*;
import com.mongodb.client.result.DeleteResult;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import org.bson.types.ObjectId;

/**
 *
 * @author dbarreca
 */
@Stateless
public class AsyncVariantService {
    private final static Logger LOGGER = Logger.getLogger(AsyncVariantService.class.getName());

    @Inject
    AsyncCollectionFactory collectionFactory;
    
    @Inject
    @QueryLimit
    private int queryLimit;

    MongoCollection<VariantModel> variantCollection;

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing AsyncVariantService...");
        variantCollection = collectionFactory.getCollection(VariantModel.class);
    }
    
    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down AsyncVariantService...");

    }
    
     public void executeCountQuery(
            ObjectId vcfFileId,
            String sample,
            String bedFileId,
            Set<String> additionalGenes,
            QueryRule filterRules,
            CacheConsumer<Long> countConsumer,
            CacheConsumer<Throwable> onError,
            CacheRunnable onTimeout
    ) throws MongoServiceException {
        List<Bson> aggregationSteps = getAggregationSteps(vcfFileId, sample, bedFileId, additionalGenes, filterRules);
        aggregationSteps.add(count());
        variantCollection.aggregate(aggregationSteps, Count.class).maxTime(2, java.util.concurrent.TimeUnit.MINUTES).first((Count result, Throwable t) -> {
            LOGGER.info("Count query - process result ...");
            try{
                if (t!=null){
                    if (t.getMessage().contains("ExceededTimeLimit")){
                        LOGGER.log(Level.WARNING, "Count execution failed: " + t.getMessage(), t);
                        onTimeout.run();
                    }else{
                        LOGGER.log(Level.WARNING, "Count execution failed: " + t.getMessage(), t);
                        onError.consume(t);
                    }
                }else{
                    countConsumer.consume(result!=null ?  result.getCount(): 0L);
                }
            }catch(EntityNotFoundException e){
                LOGGER.log(Level.SEVERE,"The count query was cancelled since no cached query was found",e);
            }
            LOGGER.info("Count execution completed.");
        });
    }
    
    public void executeQuery(
            ObjectId vcfFileId,
            String sample,
            String bedFileId,
            Set<String> additionalGenes,
            QueryRule filterRules,
            CacheConsumer<VariantResult> variantConsumer,
            CacheConsumer<Throwable> onError,
            CacheRunnable onComplete
            
    ) throws MongoServiceException {
           
        
        List<Bson> aggregationSteps = getAggregationSteps(vcfFileId, sample, bedFileId,additionalGenes, filterRules);
        aggregationSteps.add(limit(queryLimit));

        LOGGER.info("Executing aggregation query ...");
        AggregateIterable<VariantResult> iterable = variantCollection.aggregate(aggregationSteps, VariantResult.class);

        LOGGER.info("Iterate over results ...");
        iterable.forEach(
                (VariantResult variant) -> {
                    try{
                        variantConsumer.consume(variant);
                    }catch (EntityNotFoundException e){
                        throw new VariantIterationError(e);
                    }
                },
                (Void result, Throwable t) -> {
                    try{
                        if (t != null) {
                            if (t.getCause() instanceof EntityNotFoundException){
                                LOGGER.log(Level.SEVERE,"The count query was cancelled since no cached query was found", t);
                            }else{
                                LOGGER.log(Level.SEVERE, "Error while executing query", t);
                                onError.consume(t);
                            }
                        } else {
                            LOGGER.info("Query execution completed.");
                            onComplete.run();
                        }
                    }catch(EntityNotFoundException e){
                        LOGGER.info("The query was already cancelled. Nothing to do");
                        //DO NOTHING
                    }
                }
        );
        LOGGER.info("Prepared collection of results from query!");
    }
    
    private List<Bson> getAggregationSteps(ObjectId vcfFileId,
            String sample,
            String bedFileId,
            Set<String> additionalGenes,
            QueryRule filterRules) throws MongoServiceException{
        
        List<Bson> aggregations = new LinkedList();            
        
        Bson theQuery;
        if (filterRules!=null){
            theQuery = and(
                    eq(VariantModel.SAMPLE_NAME, sample), 
                    eq(VariantModel.VCF_ID, vcfFileId),                    
                    BsonQueryParser.getCriteria(filterRules));
            
        }else{
            theQuery = and(                    
                    eq(VariantModel.SAMPLE_NAME, sample),
                    eq(VariantModel.VCF_ID, vcfFileId)
            );
        }
        
        aggregations.add(match(theQuery));
        
        if (bedFileId != null) {
            aggregations.addAll(getBedJoinPipeline(bedFileId, additionalGenes));
        }else if (additionalGenes!=null && !additionalGenes.isEmpty()){
            aggregations.add(match(in(VariantModel.GENE_FIELD, additionalGenes)));
        }
        
        aggregations.add(sort(ascending(VariantModel.CHROM, VariantModel.POS)));
        
        aggregations.add(project(fields(include(VariantModel.VARIANT_ID))));
        
        return aggregations;
    }
    
    private List<Bson> getBedJoinPipeline(String bedFileId, Set<String> additionalGenes) {
        String joinFieldName = "joinKey";
        String bedRegionsArrayName = "regions";
        String tempFieldName = "temp";
       
        List<Bson> aggregations = new LinkedList();
        
        //Calculate the join Key - 'Bukcetize' position in 1mio sized buckets
        Bson bucket = substr(trunc(divide(valueOf(VariantModel.POS), BEDParser.BUCKET_SIZE)),0,-1);
        Bson joinKey = concat(bedFileId, "-", valueOf(VariantModel.CHROM), "-",bucket);
        aggregations.add(project(fields(include(VariantModel.CHROM, VariantModel.POS, VariantModel.VARIANT_ID, VariantModel.GENE_FIELD), computed(joinFieldName, joinKey))));

        //Perform Join
        aggregations.add(lookup(
                collectionFactory.getCollectionName(BedEntry.class),
                joinFieldName, 
                BedEntry.LOOKUP_KEY, 
                bedRegionsArrayName
        ));

        //Filter only buckets which contains the position
        Bson filter = genericExpr(
                MongoUtils.AND, 
                genericExpr(MongoUtils.LTE, toVar(tempFieldName+"."+BedEntry.START), valueOf(VariantModel.POS)), 
                genericExpr(MongoUtils.GTE, toVar(tempFieldName+"."+BedEntry.END), valueOf(VariantModel.POS))
        );
        
        aggregations.add(project(fields(
                include(VariantModel.CHROM, VariantModel.POS, VariantModel.VARIANT_ID, VariantModel.GENE_FIELD), 
                computed(bedRegionsArrayName, filter(valueOf(bedRegionsArrayName),tempFieldName, filter))
        )));

        //Select only variants in relevant regions
        Bson sizeConstraint = not(size(bedRegionsArrayName, 0));
        if (additionalGenes == null || additionalGenes.isEmpty()){
            aggregations.add(match(sizeConstraint));
        }else{
            aggregations.add(match(or(sizeConstraint,in(VariantModel.GENE_FIELD, additionalGenes))));
        }
        
        return aggregations;
    }
    
    public void removeVCFVariants(ObjectId vcfId, Runnable onComplete, Consumer<Throwable> onError){
        
        variantCollection.deleteMany(eq(VariantModel.VCF_ID, vcfId), (DeleteResult result, Throwable t) -> {
            if (t!=null ) {
                onError.accept(t);
            }else{
                LOGGER.info("VCF ID: "+vcfId.toHexString()+". Deleted "+result.getDeletedCount()+ " variants ");
                onComplete.run();
            }
            
        });
    }

    public CompletableFuture<Void> saveVariants(List<VariantModel> variants) {
         CompletableFuture theResult = new CompletableFuture();
        
        variantCollection.insertMany(variants, (Void result, Throwable t) -> {
            if (t!=null ) {
                theResult.completeExceptionally(t);
            }else{
                theResult.complete(result);
            }
            
        });
        
        return theResult;
    }
    
}
