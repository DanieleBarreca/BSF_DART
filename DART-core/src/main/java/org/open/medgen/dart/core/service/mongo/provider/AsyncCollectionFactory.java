/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.provider;

import org.open.medgen.dart.core.model.mongo.MongoModel;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 *
 * @author dbarreca
 */
@Singleton
public class AsyncCollectionFactory extends CollectionFactory {

    private static final Logger LOG = Logger.getLogger(AsyncCollectionFactory.class.getName());
    
    
    @Inject
    @AsyncClient
    MongoDatabase mongoDB;
            
    public Map<Class, MongoCollection> collections = new HashMap();

    public AsyncCollectionFactory() {
    }

    public AsyncCollectionFactory(MongoDatabase mongoDB) {
        this.mongoDB = mongoDB;
        this.init();
    }
    
     public AsyncCollectionFactory(MongoDatabase mongoDB, boolean isTest) {
        this.mongoDB = mongoDB;
        this.isTest = isTest;
        
        this.init();
        
    }
    
    @PostConstruct
    private void init() {
        for (MongoModel mongoModel: mongoModels ){
            Class clazz = mongoModel.getClass();
            collections.put(clazz, mongoDB.getCollection(getCollectionName(mongoModel), clazz).withCodecRegistry(mongoModel.getRegistry()));
        }
    }     
    
        
    public <T> MongoCollection<T> getCollection(Class<T> clazz){
        MongoCollection<T> theCollection =  collections.get(clazz);
        if (theCollection == null) throw new IllegalStateException("No collection found for class "+clazz.getCanonicalName());
        
        return theCollection;
    }   
    
    public void indexCollections() {
        for (MongoModel mongoModel : mongoModels) {
            
            MongoCollection collection = getCollection(mongoModel.getClass());
            LOG.log(Level.INFO, "STARTED INDEXING COLLECTION {0}", collection.getNamespace().getCollectionName());
            
            if (!mongoModel.getIndexes().isEmpty()){
                collection.createIndexes(mongoModel.getIndexes(), new SingleResultCallback<List<String>> () {
                    @Override
                    public void onResult(List<String> result, Throwable t) {
                        if (t!=null){
                            LOG.log(Level.SEVERE,"ERROR while Indexing", t);
                        }else{
                            LOG.log(Level.INFO, "COMPLETED INDEXING MONGODB {0}", result.toString());
                        }
                    }
                });
            }
        }
    }

    @Override
    public String getCollectionName(Class clazz) {
        return  getCollection(clazz).getNamespace().getCollectionName();
    }
    
    
}
