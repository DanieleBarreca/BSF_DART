/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.bed;


import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author dbarreca
 */
@Embeddable
public class BEDFilePermissionPK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @JoinColumn(name = "bed_id", referencedColumnName = "bed_id")
    @ManyToOne(optional = false)
    private BedFile file;
    
    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    @ManyToOne(optional = false)
    private UserGroup group;

    public BEDFilePermissionPK() {
    }

    
    public BEDFilePermissionPK(BedFile file, UserGroup group) {
        this.file = file;
        this.group = group;
    }

    public BedFile getFile() {
        return file;
    }

    public void setFile(BedFile file) {
        this.file = file;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.file);
        hash = 31 * hash + Objects.hashCode(this.group);
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
        final BEDFilePermissionPK other = (BEDFilePermissionPK) obj;
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        if (!Objects.equals(this.group, other.group)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
