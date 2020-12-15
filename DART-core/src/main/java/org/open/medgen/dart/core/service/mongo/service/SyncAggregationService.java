/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.service;


import org.open.medgen.dart.core.model.mongo.aggregations.TranscriptMutationCount;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.service.mongo.provider.SyncCollectionFactory;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.bson.BsonType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;


/**
 *
 * @author dbarreca
 */
@Stateless
public class SyncAggregationService {
    
    @Inject
    private SyncCollectionFactory collectionFactory;
    
    private MongoCollection<VariantModel> variantCollection;
    
   private final static Logger LOGGER = Logger.getLogger(SyncAggregationService.class.getName());
    @PostConstruct
    private void startup() {
        variantCollection = collectionFactory.getCollection(VariantModel.class);
        
        LOGGER.info("Initializing Aggregation Service...");
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down Aggregation Service...");

    }
  
    public List<TranscriptMutationCount> getMutationsInTranscript(ObjectId vcfId, String transcript, String sampleName) {
        List<Bson> conditions = new LinkedList<>();
        
        conditions.add(eq(VariantModel.VCF_ID,vcfId));
        conditions.add(eq(VariantModel.TRANSCRIPT_FEATURE,transcript));
        conditions.add(not(type(VariantModel.TRANSCRIPT_PROTEIN_START,BsonType.NULL)));
        if (sampleName!=null && !sampleName.isEmpty()) conditions.add(eq(VariantModel.SAMPLE_NAME,sampleName));
        
        List<Bson> aggregations = new LinkedList();
        aggregations.add(match(and(conditions)));
        aggregations.add(group(
            new Document(TranscriptMutationCount.POS,MongoUtils.valueOf(VariantModel.TRANSCRIPT_PROTEIN_START))
                .append(TranscriptMutationCount.IMPACT, MongoUtils.valueOf(VariantModel.TRANSCRIPT_IMPACT)),
                sum(TranscriptMutationCount.COUNT, 1)
        ));
        
        
        List<TranscriptMutationCount> resultList = new LinkedList();
        
        variantCollection.aggregate(aggregations, TranscriptMutationCount.class).into(resultList);
        
        return resultList;
    }

    
    
}
