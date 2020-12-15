/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.cache;

import org.infinispan.query.dsl.*;
import org.open.medgen.dart.core.model.query.FullQuery;
import org.open.medgen.dart.core.model.cache.CachedQuery;
import org.open.medgen.dart.core.model.cache.QueryStatus;
import org.open.medgen.dart.core.model.cache.UserInfo;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.query.Search;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.ConditionTermDTO;

/**
 *
 * @author dbarreca
 */
@Stateless
public class CacheService {
       
    //@Resource(name = "java:comp/env/cache/localCache")
    @Inject @QueryCache
    private Cache<String, CachedQuery> cache;
    
    private QueryFactory qf;

    @PostConstruct
    public void start() {
        qf= Search.getQueryFactory(cache);
    } 
    
    public CachingResult addQuery(FullQuery theQuery, String user, String userGroup, boolean hasCoverage, String userToken){
        
        CachedQuery toCache = new CachedQuery(theQuery, hasCoverage, new UserInfo(user,userGroup,userToken));
        CachedQuery existingQuery = retrieveQueryWithKey(toCache.getQueryKey());
        
        boolean toRun = true;
        
        if (existingQuery!=null){
            if(!QueryStatus.ERROR.equals(existingQuery.getResultStatus())){
                toRun = false;
            }else{
                existingQuery.reRun();
            }
            existingQuery.merge(toCache);
            cache.replace(existingQuery.getQueryUUID(), existingQuery);
        }else{
            existingQuery = toCache;
            if (userToken!=null && !userToken.isEmpty()){
                cache.put(existingQuery.getQueryUUID(), existingQuery, 1, TimeUnit.DAYS);
            }else {
                cache.put(existingQuery.getQueryUUID(), existingQuery);
            }
        }
  
        return new CachingResult(cache.get(existingQuery.getQueryUUID()), toRun);
    }
    
    public void completeQueryResults(CachedQuery cachedQuery) throws EntityNotFoundException{
        
        synchronized (cachedQuery) {

            cachedQuery.complete();

            this.replaceQuery(cachedQuery);
        }
        
    }
    
    public void completeQueryResultsWithError(CachedQuery cachedQuery, Throwable error) throws EntityNotFoundException{

        synchronized (cachedQuery) {
            cachedQuery.completeExceptionally(error);

            this.replaceQuery(cachedQuery);
        }
    }

    public void completeCoverageResultsWithError(CachedQuery cachedQuery, Throwable error) throws EntityNotFoundException{
        
        synchronized (cachedQuery) {
            cachedQuery.completeCoverageExceptionally(error);

            this.replaceQuery(cachedQuery);
        }
    }


    public void completeCoverageResults(CachedQuery cachedQuery) throws EntityNotFoundException{
        
        synchronized (cachedQuery) {
            cachedQuery.completeCoverage();

            this.replaceQuery(cachedQuery);
        }

    }

    public void completeCountResult(CachedQuery cachedQuery, long result) throws EntityNotFoundException{

        synchronized (cachedQuery) {
            cachedQuery.setTotalCount(result);

            this.replaceQuery(cachedQuery);
        }
       
    }
    
    public void completeCountResultsWithError(CachedQuery cachedQuery, Throwable t) throws EntityNotFoundException{

        synchronized (cachedQuery) {
            cachedQuery.setTotalCountError(t);

            this.replaceQuery(cachedQuery);
        }
    }
    
    public void completeCountResultsWithTimeout(CachedQuery cachedQuery) throws EntityNotFoundException {

        synchronized (cachedQuery) {
            cachedQuery.setTotalCountTimeout();

            this.replaceQuery(cachedQuery);
        }
    }

    private void  replaceQuery(CachedQuery theQuery) throws EntityNotFoundException{
        
        CachedQuery result = cache.replace(theQuery.getQueryUUID(), theQuery);
        if (result==null){
            throw new EntityNotFoundException("Cached query with UUID "+theQuery.getQueryUUID()+" was not found");
        }
    }

   
    public List<CachedQuery> retrieveUserQueries(String userName, String userGroup, String userToken) {
        QueryBuilder qb = qf
                .from(CachedQuery.class)
                .having("queryKey.userInfo.userName").eq(userName)
                .and()
                .having("queryKey.userInfo.userGroup").eq(userGroup);
        
        if (userToken!=null && !userToken.trim().isEmpty()){
            qb = ((FilterConditionContext) qb).and().having("queryKey.userInfo.userToken").eq(userToken);
        }
                
        Query query = qb.build();

        return query.list();
    }
     
    public List<CachedQuery> retrieveAllQueries() {
         Query query = qf
                .from(CachedQuery.class)
                .build();

        return query.list();
    }

    public CachedQuery retrieveQueryWithKey(CachedQueryKey theKey) {
        QueryBuilder query = qf.from(CachedQuery.class).having("queryKey.hashCode").eq(theKey.hashCode());

        List<CachedQuery> results =  query.build().<CachedQuery>list();
        for (CachedQuery result: results){
            if (result.getQueryKey().equals(theKey)){
                return result;
            }
        }

        return null;
    }


    public CachedQuery retrieveQueryForUserWithId(String userName, String id) {
        CachedQuery query = retrieveQueryWithId(id);
        
        if (query!=null && query.getUserInfo().getUserName().equals(userName)){
            return query;
        }
        
        return null;
    }

    public CachedQuery retrieveQueryWithId(String id) {
        CachedQuery query = cache.get(id);
        return query;
    }
    
    public CachedQuery retrieveQueryForUserGroupWithId(String userName, String groupName, String id) {
        CachedQuery query = retrieveQueryForUserWithId(userName, id);
        
        if (query!= null && query.getUserInfo().getUserGroup().equals(groupName)){
            return query;
        }
        
        return null;
    }
    
    public void evictVCF(Integer vcfFileId){
        QueryBuilder query = qf.from(CachedQuery.class)
                .having("queryKey.theQuery.vcfFileId").eq(vcfFileId);
        Iterator<CachedQuery> iter = query.build().<CachedQuery>list().iterator();
        while(iter.hasNext()){
            this.removeQuery(iter.next());
        }
        
    }
        
    public void removeQuery(CachedQuery theQuery){
        cache.remove(theQuery.getQueryUUID());
    }

    public void removeUser(String userName, String queryId, String userGroup) throws EntityNotFoundException {
        CachedQuery theQuery = this.retrieveQueryForUserGroupWithId(userName,userGroup, queryId);
        if (theQuery == null) throw new EntityNotFoundException("Query with ID "+queryId+" not found");

        removeQuery(theQuery);
    }
    
}
