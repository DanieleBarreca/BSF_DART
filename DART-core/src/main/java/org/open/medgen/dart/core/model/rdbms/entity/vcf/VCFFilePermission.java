/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.vcf;

import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "vcf_permissions")
public class VCFFilePermission implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private VCFFilePermissionPK key;
    
    @Column(name="owner")
    private Boolean isOwner = false;

    public VCFFilePermission() {
    }

    public VCFFilePermission(VCFFilePermissionPK key) {
        this.key = key;
    }
    
    public VCFFilePermission(VCFFile file, UserGroup group) {
        this.key = new VCFFilePermissionPK(file,group);
    }

    
    public VCFFilePermissionPK getKey() {
        return key;
    }

    public void setKey(VCFFilePermissionPK key) {
        this.key = key;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.key);
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
        final VCFFilePermission other = (VCFFilePermission) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }
    
    
    
}
