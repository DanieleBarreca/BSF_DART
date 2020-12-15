/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.vcf;

import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFAttributeNumber;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.FieldType;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFHeaderLineType;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "vcf_field")
public class VCFField implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    @Column(name="field_id")
    private Integer fieldId;
    
    @Column(name="field_path")
    @Size(max=255)
    @NotNull
    private String fieldPath;

    @Column(name="field_name")
    @Size(max=45)
    private String fieldName;   
    
    @Column(name="display_name")
    @Size(max=45)
    private String displayName;
    
    @Column(name="description")
    @Size(max=5000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vcf_number")
    @NotNull
    private VCFAttributeNumber vcfNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vcf_type")
    @NotNull
    private VCFHeaderLineType vcfFieldType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "internal_type")
    @NotNull
    private FieldType fieldType;

    @Column(name = "queryable")
    @NotNull
    private Boolean queryable;
    
    @Lob
    @Size(max = 65535)
    @Column(name = "possible_values")
    private String possibleValues;

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer id) {
        this.fieldId = id;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VCFAttributeNumber getVcfNumber() {
        return vcfNumber;
    }

    public void setVcfNumber(VCFAttributeNumber vcfNumber) {
        this.vcfNumber = vcfNumber;
    }

    public VCFHeaderLineType getVcfFieldType() {
        return vcfFieldType;
    }

    public void setVcfFieldType(VCFHeaderLineType vcfFieldType) {
        this.vcfFieldType = vcfFieldType;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getPossibleValues() {
        return possibleValues;
    }
    
    public void setPossibleValues(String possibleValues) {
        this.possibleValues = possibleValues;
    }


    public Boolean getQueryable() {
        return queryable;
    }

    public void setQueryable(Boolean queryable) {
        this.queryable = queryable;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.fieldId);
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
        final VCFField other = (VCFField) obj;
        if (!Objects.equals(this.fieldId, other.fieldId)) {
            return false;
        }
        return true;
    }
  
    
}
