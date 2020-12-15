package org.open.medgen.dart.core.service.cache;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.NumericField;
import org.open.medgen.dart.core.model.cache.UserInfo;
import org.open.medgen.dart.core.model.query.FullQuery;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CachedQueryKey implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @IndexedEmbedded
    protected FullQuery theQuery;
    
    @IndexedEmbedded
    protected UserInfo userInfo;
    
    protected CachedQueryKey() {}
    
    public CachedQueryKey(FullQuery theQuery, UserInfo userInfo) {
        this.theQuery = theQuery;
        this.userInfo = userInfo;
    }

    public FullQuery getTheQuery() {
        return theQuery;
    }

    public void setTheQuery(FullQuery theQuery) {
        this.theQuery = theQuery;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CachedQueryKey that = (CachedQueryKey) o;
        return Objects.equals(theQuery, that.theQuery) &&
                Objects.equals(userInfo, that.userInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theQuery, userInfo);
    }

    @Field(name = "hashCode")
    @NumericField
    public Integer getHashCode(){
        return this.hashCode();
    }
    
    public void setHashCode(Integer hashCode){
        return;    
    }
}
