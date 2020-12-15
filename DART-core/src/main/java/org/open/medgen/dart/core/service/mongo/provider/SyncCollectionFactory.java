/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.provider;

import com.mongodb.async.SingleResultCallback;
import org.open.medgen.dart.core.model.mongo.MongoModel;
import org.open.medgen.dart.core.service.mongo.service.MongoUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 *
 * @author dbarreca
 */
@Singleton
@Startup
public class SyncCollectionFactory extends CollectionFactory {
    
    private static final Logger LOG = Logger.getLogger(SyncCollectionFactory.class.getName());

    @Inject
    @SyncClient
    MongoDatabase mongoDB;
        
    protected Map<Class, MongoCollection> collections = new HashMap();


    public SyncCollectionFactory() {
    }

    public SyncCollectionFactory(MongoDatabase mongoDB) {
        this.mongoDB = mongoDB;
        this.init();
    }
    
     public SyncCollectionFactory(MongoDatabase mongoDB, boolean isTest) {
        this.mongoDB = mongoDB;
        this.isTest = isTest;
        
        this.init();
        
    }
    
    @PostConstruct
    private void init() {
       for (MongoModel mongoModel: mongoModels ){
            Class clazz = mongoModel.getClass();
            
            String collectionName=mongoModel.getCollectionName();

           boolean collectionExists=false;
           for (String collection: mongoDB.listCollectionNames()){
               if (collection.equals(collectionName)){
                   collectionExists=true;
                   break;
               }
           }

          
           if (!collectionExists){
               LOG.log(Level.INFO, "CREATING COLLECTION {0}", collectionName);
               mongoDB.createCollection(collectionName);
           }

           MongoCollection collection = mongoDB.getCollection(collectionName, clazz).withCodecRegistry(mongoModel.getRegistry());
           
           if (!collectionExists && !mongoModel.getIndexes().isEmpty() ){
               LOG.log(Level.INFO, "CREATING INDEXES FOR COLLECTION {0}", collection.getNamespace().getCollectionName());
               collection.createIndexes(mongoModel.getIndexes());
           }
            
            collections.put(clazz,collection );
            
            
        }
    }   
       
    
    public <T> MongoCollection<T> getCollection(Class<T> clazz){
        MongoCollection<T> theCollection =  collections.get(clazz);
        if (theCollection == null) throw new IllegalStateException("No collection found for class "+clazz.getCanonicalName());
        
        return theCollection;
    }

    @Override
    public String getCollectionName(Class clazz) {
        return  getCollection(clazz).getNamespace().getCollectionName();
    }

    
}
