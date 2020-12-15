/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.cache;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author dbarreca
 */

@Embeddable
public class UserInfo implements Serializable {
    
    @Field(analyze = Analyze.NO)
    private String userName;

    @Field(analyze = Analyze.NO)
    private String userGroup;
    
    @Field(indexNullAs = "", analyze = Analyze.NO)
    private String userToken;

    @Field(analyze = Analyze.NO)
    private Date accessDate =new Date();
    
    protected UserInfo(){
        
    }
    
    public UserInfo(String userName, String userGroup) {
        this.userName = userName;
        this.userGroup = userGroup;
        this.userToken = null;
    }

    public UserInfo(String userName, String userGroup, String userToken) {
        this.userName = userName;
        this.userGroup = userGroup;
        this.userToken = userToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this.accessDate = accessDate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.userName);
        hash = 97 * hash + Objects.hashCode(this.userGroup);
        hash = 97 * hash + Objects.hashCode(this.userToken);
        
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserInfo other = (UserInfo) obj;
        if (!Objects.equals(this.userName, other.userName)) {
            return false;
        }
        if (!Objects.equals(this.userGroup, other.userGroup)) {
            return false;
        }
        if (!Objects.equals(this.userToken, other.userToken)) {
            return false;
        }
        return true;
    }

    
    
    
    
}
