/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "user_roles")
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    @ManyToOne(optional = false)
    private User user;
    
    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    @ManyToOne(optional = false)
    private UserGroup group;
    
    @Column(name = "canQuery")
    @NotNull
    private Boolean canQuery =false; 
    
    @Column(name = "canSaveQueryPreset")
    @NotNull
    private Boolean canSaveQueryPreset = false;
    
    @Column(name = "canSaveQueryPanel")
    @NotNull
    private Boolean canSaveQueryPanel = false;

    @Column(name = "canAnnotatePathogenicity")
    @NotNull
    private Boolean canAnnotatePathogenicity = false;

    @Column(name = "canValidateVariants")
    @NotNull
    private Boolean canValidateVariants = false;
    
    @Column(name = "isAdmin")
    @NotNull
    private Boolean isAdmin = false;
    
    @Column(name = "canUploadVCF")
    @NotNull
    private Boolean canUploadVCF = false;

    @Column(name = "canSaveReport")
    @NotNull
    private Boolean canSaveReport = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public UserGroup getGroup() {
        return group;
    }

   

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getCanQuery() {
        return canQuery;
    }

    public void setCanQuery(Boolean canQuery) {
        this.canQuery = canQuery;
    }

    public Boolean getCanSaveQueryPreset() {
        return canSaveQueryPreset;
    }

    public void setCanSaveQueryPreset(Boolean canSaveQueryPreset) {
        this.canSaveQueryPreset = canSaveQueryPreset;
    }

    public Boolean getCanSaveQueryPanel() {
        return canSaveQueryPanel;
    }

    public void setCanSaveQueryPanel(Boolean canSaveQueryPanel) {
        this.canSaveQueryPanel = canSaveQueryPanel;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean getCanUploadVCF() {
        return canUploadVCF;
    }

    public void setCanUploadVCF(Boolean canUploadVCF) {
        this.canUploadVCF = canUploadVCF;
    }

    public Boolean getCanAnnotatePathogenicity() {
        return canAnnotatePathogenicity;
    }

    public void setCanAnnotatePathogenicity(Boolean canAnnotatePathogenicity) {
        this.canAnnotatePathogenicity = canAnnotatePathogenicity;
    }

    public Boolean getCanValidateVariants() {
        return canValidateVariants;
    }

    public void setCanValidateVariants(Boolean canValidateVariants) {
        this.canValidateVariants = canValidateVariants;
    }

    public Boolean getCanSaveReport() {
        return canSaveReport;
    }

    public void setCanSaveReport(Boolean canSaveReport) {
        this.canSaveReport = canSaveReport;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", user=" + user +
                ", group=" + group +
                ", canQuery=" + canQuery +
                ", canSaveQueryPreset=" + canSaveQueryPreset +
                ", canSaveQueryPanel=" + canSaveQueryPanel +
                ", isAdmin=" + isAdmin +
                ", canUploadVCF=" + canUploadVCF +
                '}';
    }
}
