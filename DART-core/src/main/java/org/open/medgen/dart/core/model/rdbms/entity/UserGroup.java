/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity;

import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.QueryPreset;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFile;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "user_group")
public class UserGroup implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "group_id")
    private Integer groupId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "group_name")
    private String group;
    
    @JoinTable(name = "vcf_permissions", joinColumns = {
        @JoinColumn(name = "group_id", referencedColumnName = "group_id")}, inverseJoinColumns = {
        @JoinColumn(name = "vcf_id", referencedColumnName = "vcf_id")})
    @ManyToMany
    private List<VCFFile> vcfList = new LinkedList<>();
    
    @JoinTable(name = "bed_permissions", joinColumns = {
        @JoinColumn(name = "group_id", referencedColumnName = "group_id")}, inverseJoinColumns = {
        @JoinColumn(name = "bed_id", referencedColumnName = "bed_id")})
    @ManyToMany
    private List<BedFile> bedFileList = new LinkedList<>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userGroup")
    private List<QueryPreset> queries = new LinkedList<>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userGroup")
    private List<PanelPreset> panels = new LinkedList<>();
    
    @JoinTable(name = "user_roles", joinColumns = {
        @JoinColumn(name = "group_id", referencedColumnName = "group_id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "userId")})
    @ManyToMany
    private Set<User> users = new HashSet<>();
    
    
    
    public UserGroup() {
    }

    public UserGroup(Integer groupId) {
        this.groupId = groupId;
    }

    public UserGroup(Integer groupId, String group) {
        this.groupId = groupId;
        this.group = group;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<VCFFile> getVcfList() {
        return vcfList;
    }

    public void setVcfList(List<VCFFile> vcfList) {
        this.vcfList = vcfList;
    }

    public List<BedFile> getBedFileList() {
        return bedFileList;
    }

    public void setBedFileList(List<BedFile> bedFileList) {
        this.bedFileList = bedFileList;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }


    public List<QueryPreset> getQueries() {
        return queries;
    }

    public void setQueries(List<QueryPreset> queries) {
        this.queries = queries;
    }

    public List<PanelPreset> getPanels() {
        return panels;
    }

    public void setPanels(List<PanelPreset> panels) {
        this.panels = panels;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupId != null ? groupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserGroup)) {
            return false;
        }
        UserGroup other = (UserGroup) object;
        if ((this.groupId == null && other.groupId != null) || (this.groupId != null && !this.groupId.equals(other.groupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.open.medgen.vcfcore.model.v1.annotation.entity.Group1[ groupId=" + groupId + " ]";
    }

    public void addVcf(VCFFile theVCF) {
        if (!this.vcfList.contains(theVCF)) this.vcfList.add(theVCF);
    }

    public void addBed(BedFile theBED) {
        if (!this.bedFileList.contains(theBED)) this.bedFileList.add(theBED);
    }
    
}
