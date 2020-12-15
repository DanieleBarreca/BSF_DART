/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.presets;

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
@Table(name = "fields_preset")
public class FieldsPreset implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private FieldsPresetPK key;
    
    @Column(name="rank")
    private Integer rank;

    public FieldsPreset(FieldsPresetPK key, Integer rank) {
        this.key = key;
        this.rank = rank;
    }

    public FieldsPreset(FieldsPresetPK key) {
        this.key = key;
    }

    public FieldsPreset() {
    }

    public FieldsPresetPK getKey() {
        return key;
    }

    public void setKey(FieldsPresetPK key) {
        this.key = key;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.key);
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
        final FieldsPreset other = (FieldsPreset) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }
    
    
}
