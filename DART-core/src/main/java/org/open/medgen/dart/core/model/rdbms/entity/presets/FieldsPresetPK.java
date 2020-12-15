/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.presets;

import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFField;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import static javax.persistence.EnumType.STRING;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author dbarreca
 */
@Embeddable
public class FieldsPresetPK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    @ManyToOne(optional = false)
    private User user;
    
    @JoinColumn(name = "field_id", referencedColumnName = "field_id")
    @ManyToOne(optional = false)
    private VCFField field;
    
    @Column(name="vcf_type")
    @Enumerated(STRING)
    private VCFType type;

    public FieldsPresetPK(User user, VCFField field,VCFType type) {
        this.user = user;
        this.field = field;
        this.type = type;
    }

    public FieldsPresetPK() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VCFField getField() {
        return field;
    }

    public void setField(VCFField field) {
        this.field = field;
    }

    public VCFType getType() {
        return type;
    }

    public void setType(VCFType type) {
        this.type = type;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.user);
        hash = 67 * hash + Objects.hashCode(this.field);
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
        final FieldsPresetPK other = (FieldsPresetPK) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.field, other.field)) {
            return false;
        }
        return true;
    }
    
    

    
}
