/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.cache;

import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.open.medgen.dart.core.model.cache.CachedQuery;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Init;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 *
 * @author dbarreca
 */
@Singleton
public class CacheProvider {
    
    private EmbeddedCacheManager manager = null;
    private Cache<String, CachedQuery> queryCache;
    
    private static final Logger LOGGER = Logger.getLogger(CacheProvider.class.getName());

    @PostConstruct
    public void init(){
        try {
            manager = new DefaultCacheManager("infinispan-config.xml");
            
            queryCache = manager.getCache("queries");

            LOGGER.info("Starting indexing the cache...");
            SearchManager searchManager = Search.getSearchManager(queryCache);
            searchManager.getMassIndexer().start();
            LOGGER.info("Cache indexed!");
            
        } catch (IOException ex) {
            Logger.getLogger(CacheProvider.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }
    
    @PreDestroy
    public void cleanUp(){
        if (manager!=null) try {
            manager.close();
        } catch (IOException ex) {
            Logger.getLogger(CacheProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Produces @QueryCache
    public Cache<String, CachedQuery> getQueryCache() {
        return queryCache;
    }
    
    
}
