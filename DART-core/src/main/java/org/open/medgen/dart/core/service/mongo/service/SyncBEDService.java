/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.service;

import org.open.medgen.dart.core.model.mongo.bed.BedEntry;
import org.open.medgen.dart.core.service.mongo.provider.SyncCollectionFactory;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.bson.types.ObjectId;

/**
 *
 * @author dbarreca
 */
@Stateless
public class SyncBEDService { 
    
    private final static Logger LOGGER = Logger.getLogger(SyncBEDService.class.getName());
    
    @Inject
    SyncCollectionFactory collectionFactory;
    
    MongoCollection<BedEntry> bedCollection;

     
    @PostConstruct
    public void init() {
        LOGGER.info("Initializing SyncBEDServices...");
        bedCollection = collectionFactory.getCollection(BedEntry.class);
    }
    

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down AsyncVCFService...");

    }
   
    public void removeBED(ObjectId id) {
        bedCollection.deleteMany(eq(BedEntry.BED_FILE, id));
    }
   
}
