/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.open.medgen.dart.core.model.query.FullQuery;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.ConditionTermDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dbarreca
 */
public class CachedQueryInfo {
    
    private final Date created;
    private final FullQuery theQuery;
    private final String uuid;
    private final CountStatus countStatus;
    private final Throwable countError;
    private final QueryStatus resultStatus;
    private final Throwable resultError;
    private final CoverageStatus coverageStatus;
    private final Throwable coverageError;
    private final Date lastAccessed;
    private final List<ConditionTermDTO> conditions;
    
    
     public CachedQueryInfo(CachedQuery cachedQuery, String user, String userGroup, List<ConditionTermDTO> conditions) {
        this.created = cachedQuery.created;
        this.theQuery = cachedQuery.queryKey.getTheQuery();
        this.uuid = cachedQuery.queryUUID;
        this.countError = cachedQuery.countError;
        this.countStatus = cachedQuery.countStatus;
        this.resultStatus = cachedQuery.resultStatus;
        this.resultError = cachedQuery.resultError;
        this.coverageStatus = cachedQuery.coverageStatus;
        this.coverageError = cachedQuery.coverageResultError;
        if (user ==null || user.isEmpty()) throw new AuthorizationError();
        UserInfo foundUserInfo = cachedQuery.queryKey.getUserInfo();
        if (foundUserInfo==null || !foundUserInfo.getUserName().equals(user) || !foundUserInfo.getUserGroup().equals(userGroup)) throw new AuthorizationError();
        lastAccessed = foundUserInfo.getAccessDate();
        this.conditions = conditions;
   
    }
    

    public FullQuery getTheQuery() {
        return theQuery;
    }

    @JsonIgnore
    public String getSampleName() {return theQuery.getSample();}

    public String getUuid() {
        return uuid;
    }

    public CountStatus getCountStatus() {
        return countStatus;
    }

    public Throwable getCountError() {
        return countError;
    }

    public QueryStatus getResultStatus() {
        return resultStatus;
    }

    public Throwable getResultError() {
        return resultError;
    }

    public CoverageStatus getCoverageStatus() {
        return coverageStatus;
    }

    public Throwable getCoverageError() {
        return coverageError;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public Date getCreated() {
        return created;
    }

    public List<ConditionTermDTO> getConditions() {
        return conditions;
    }
}
