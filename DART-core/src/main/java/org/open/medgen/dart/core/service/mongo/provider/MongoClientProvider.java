/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.provider;



import com.mongodb.ConnectionString;

import com.mongodb.connection.AsynchronousSocketChannelStreamFactoryFactory;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.Produces;

/**
 *
 * @author dbarreca
 */
@Singleton
public class MongoClientProvider {    
    private final static Logger LOGGER = Logger.getLogger(MongoClientProvider.class.getName());
    
    @Resource
    ManagedExecutorService executorService = null;
    
    @Resource(name="mongoUrl")
    private String mongoUrl;
  
    @Resource(name="queryLimit")
    private Integer queryLimit;

    
    private com.mongodb.async.client.MongoDatabase mongoAsyncDatabase;    
    private com.mongodb.async.client.MongoClient mongoAsyncClient;
    
    private com.mongodb.client.MongoDatabase mongoSyncDatabase;    
    private com.mongodb.client.MongoClient mongoSyncClient;

    public MongoClientProvider() {
    }

    public MongoClientProvider(String mongoUrl) {
        this.mongoUrl = mongoUrl;
        init();
    }
    
    
    @PostConstruct
    private void init(){
        
        LOGGER.info("Initializing Persistence configuration...");
        LOGGER.log(Level.INFO, "Connecting to MongoDB @ {0}", mongoUrl);
        LOGGER.log(Level.INFO, "Setting Query Limit to "+queryLimit);

        ConnectionString mongoURI = new ConnectionString(mongoUrl);       
        initSync(mongoURI);
        initAsync(mongoURI);

    }
    
    private void initSync(ConnectionString mongoURI){
        com.mongodb.MongoClientSettings.Builder clientSettingsBuilder = com.mongodb.MongoClientSettings.builder();
        clientSettingsBuilder.applyConnectionString(mongoURI);
        
        mongoSyncClient = com.mongodb.client.MongoClients.create(clientSettingsBuilder.build());
        mongoSyncDatabase = mongoSyncClient.getDatabase(mongoURI.getDatabase());
    }
    
    private void initAsync(ConnectionString mongoURI){
        com.mongodb.async.client.MongoClientSettings.Builder clientSettingsBuilder = com.mongodb.async.client.MongoClientSettings.builder();
        clientSettingsBuilder.applyConnectionString(mongoURI);
        if (executorService!=null){
            Logger.getLogger(MongoClientProvider.class.getName()).log(Level.INFO, "Use executor Service for creation " + this.executorService);
            try {
                clientSettingsBuilder.streamFactoryFactory(
                        AsynchronousSocketChannelStreamFactoryFactory.builder()
                                .group(AsynchronousChannelGroup.withThreadPool(executorService)).build());
                
            } catch (IOException ex) {
                Logger.getLogger(MongoClientProvider.class.getName()).log(Level.SEVERE, null, ex);
                throw new IllegalStateException(ex);
            }
        }
        
        mongoAsyncClient = com.mongodb.async.client.MongoClients.create(clientSettingsBuilder.build());
        mongoAsyncDatabase = mongoAsyncClient.getDatabase(mongoURI.getDatabase());
    }
    
    
    @PreDestroy
    public void shutdown() {
        if (mongoSyncClient != null) {
            LOGGER.info("Closing up mongo connection...");
            mongoAsyncClient.close();
        }
        
         if (mongoSyncClient != null) {
            LOGGER.info("Closing up mongo connection...");
            mongoAsyncClient.close();
        }

    }
    
    @Produces @QueryLimit
    public int getLimit(){
        return queryLimit;
    }
    
    @Produces @SyncClient
    public com.mongodb.client.MongoDatabase getSyncDatabase(){
        return mongoSyncDatabase;
    }
    
    @Produces @AsyncClient
    public com.mongodb.async.client.MongoDatabase getAsyncDatabase(){
        return mongoAsyncDatabase;
    }
  
}
