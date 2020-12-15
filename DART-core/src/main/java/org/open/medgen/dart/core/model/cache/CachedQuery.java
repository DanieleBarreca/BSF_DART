/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.cache;

import org.bson.types.ObjectId;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.open.medgen.dart.core.model.query.FullQuery;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;

import org.apache.commons.codec.binary.Hex;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.ConditionTermDTO;
import org.open.medgen.dart.core.service.cache.CachedQueryKey;

import javax.persistence.*;

/**
 *
 * @author dbarreca
 */

@Entity
@Indexed
public class CachedQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    protected String queryUUID;
    
    @IndexedEmbedded
    protected CachedQueryKey queryKey;
    

    @Transient
    protected Date created = new Date();

    @Transient
    protected CountStatus countStatus = CountStatus.RUNNING;
    
    @Transient
    private Long totalCount;
    
    @Transient
    protected Throwable countError = null;

    @Transient
    protected QueryStatus resultStatus = QueryStatus.RUNNING;
    
    @Transient
    private List<String> results = new LinkedList<>();

    @Transient
    protected Throwable resultError = null;

    @Transient
    private boolean hasCoverage;

    @Transient
    protected CoverageStatus coverageStatus = CoverageStatus.NOT_PRESENT;

    @Transient
    private LinkedList<String> coverageResults = new LinkedList<>();
    
    @Transient
    protected Throwable coverageResultError = null;
    
    protected CachedQuery() {}
    
    public CachedQuery(FullQuery theQuery, boolean hasCoverage, UserInfo userInfo) {
        this.queryKey = new CachedQueryKey(theQuery, userInfo);

        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        
        UUID uuid = UUID.randomUUID();
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        queryUUID = Hex.encodeHexString(bb.array());
        this.hasCoverage = hasCoverage;
        if (this.hasCoverage) {
            this.coverageStatus = CoverageStatus.RUNNING;
        }
    }
    
    public CachedQueryKey getQueryKey() {
        return queryKey;
    }

    public FullQuery getTheQuery() {
        return queryKey.getTheQuery();
    }

    public UserInfo getUserInfo() {
        return queryKey.getUserInfo();
    }

    public String getQueryUUID() {
        return queryUUID;
    }

    public QueryStatus getResultStatus() {
        return resultStatus;
    }
     
    public CountStatus getCountStatus() {
        return countStatus;
    }

    public CoverageStatus getCoverageStatus() {
        return coverageStatus;
    }
    
    public void setTotalCount(long value){
        this.totalCount = value;
        this.countStatus = CountStatus.FINISHED;
        this.countError = null;
    }
    
    public void setTotalCountError(Throwable t){
        this.totalCount = null;
        this.countError = t;
        this.countStatus = CountStatus.ERROR;
    }
    
     public void setTotalCountTimeout(){
        this.totalCount = null;
        this.countStatus = CountStatus.TIMEOUT;
    }
    
    public void addResult(ObjectId result){
        this.results.add(result.toHexString());
    }
    
    public void complete(){
        this.resultError = null;
        this.resultStatus = QueryStatus.FINISHED;
    }
    
    public void completeExceptionally(Throwable t){
        this.resultError = t;
        this.results.clear();
        this.resultStatus = QueryStatus.ERROR;
    }

    public void addCoverageResult(ObjectId result){
        this.coverageResults.add(result.toHexString());
    }

    public void completeCoverage(){
        this.coverageResultError = null;
        this.coverageStatus = CoverageStatus.FINISHED;
    }

    public void completeCoverageExceptionally(Throwable t){
        this.coverageResultError = t;
        this.coverageResults.clear();
        this.coverageStatus = CoverageStatus.ERROR;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public Throwable getCountError() {
        return countError;
    }

    public List<String> getResults() {
        return results;
    }

    public Throwable getResultError() {
        return resultError;
    }

    public List<String> getCoverageResults() {
        return coverageResults;
    }

    public Throwable getCoverageResultError() {
        return coverageResultError;
    }

    public CachedQuery merge(CachedQuery other) {
        this.queryKey.setUserInfo(other.queryKey.getUserInfo());
        this.queryKey.getTheQuery().setRelatedSamples(other.getTheQuery().getRelatedSamples());
        return this;
    }

    public boolean isHasCoverage() {
        return hasCoverage;
    }

    public void reRun() {
        this.resultStatus = QueryStatus.RUNNING;
        this.countStatus = CountStatus.RUNNING;
        if (this.hasCoverage){
            this.coverageStatus = CoverageStatus.RUNNING;
        }
        this.countError = null;
        this.resultError = null;
        this.results.clear();
        this.totalCount = null;
    }
}
