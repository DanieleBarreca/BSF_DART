/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.bed;

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
@Table(name = "bed_permissions")
public class BEDFilePermission implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private BEDFilePermissionPK key;
    
    @Column(name="owner")
    private Boolean isOwner = false;

    public BEDFilePermission() {
    }

    public BEDFilePermission(BEDFilePermissionPK key) {
        this.key = key;
    }
    
    public BEDFilePermission(BedFile file, UserGroup group) {
        this.key = new BEDFilePermissionPK(file,group);
    }

    
    public BEDFilePermissionPK getKey() {
        return key;
    }

    public void setKey(BEDFilePermissionPK key) {
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
        final BEDFilePermission other = (BEDFilePermission) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }
    
    
    
}
