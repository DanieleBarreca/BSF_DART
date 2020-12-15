/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.presets;

import org.open.medgen.dart.core.model.query.QueryRule;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFField;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "query_presets")
public class QueryPreset implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)  
    @Basic(optional = false)
    @Column(name="query_id")
    private Integer queryId;
    
    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    @ManyToOne(optional = false)
    private UserGroup userGroup;

    @Column(name="query_mnemonic")
    @Size(max=500)
    private String mnemonic;
    
    @NotNull
    @Column(name="query_hash")
    private Integer queryHash;
    
    @Lob
    @Column(name="rule",length=16777216)
    private QueryRule rule;
    
    @JoinTable(name = "query_fields", 
            joinColumns = {@JoinColumn(name = "query_id", referencedColumnName = "query_id")}, 
            inverseJoinColumns = {@JoinColumn(name = "field_id", referencedColumnName = "field_id")}
    )
    @ManyToMany
    @OrderBy("id ASC")
    private List<VCFField> fieldList = new LinkedList();

    @JoinColumn(name = "user_from", referencedColumnName = "userId")
    @ManyToOne(optional = false)
    private User userFrom;

    @JoinColumn(name = "user_to", referencedColumnName = "userId")
    @ManyToOne()
    private User userTo;

    @Basic(optional = false)
    @Column(name = "date_from")
    private Date dateFrom = new Date();

    @Basic(optional = true)
    @Column(name = "date_to")
    private Date dateTo;

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public Integer getQueryHash() {
        return queryHash;
    }

    public void setQueryHash(Integer queryHash) {
        this.queryHash = queryHash;
    }

    public QueryRule getRule() {
        return rule;
    }

    public void setRule(QueryRule rule) {
        this.rule = rule;
    }
    
    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public List<VCFField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<VCFField> fieldList) {
        this.fieldList = fieldList;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.queryId);
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
        final QueryPreset other = (QueryPreset) obj;
        if (!Objects.equals(this.queryId, other.queryId)) {
            return false;
        }
        return true;
    }

    
}
