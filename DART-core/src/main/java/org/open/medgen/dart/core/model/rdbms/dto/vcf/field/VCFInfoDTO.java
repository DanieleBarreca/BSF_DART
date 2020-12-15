/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.dto.vcf.field;

import org.open.medgen.dart.core.controller.utils.VCFFieldsUtils;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author dbarreca
 */
public class VCFInfoDTO implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private final static String ID = "ID";
    private final static String DESCRIPTION = "DESCRIPTION";
    private final static String NUMBER = "VCF_NUMBER";    
    private final static String VCF_TYPE = "VCF_TYPE";
    private final static String DISPLAY_NAME = "DISPLAY_NAME";
    private final static String FIELD_PATH = "FIELD_PATH";
    private final static String TYPE = "TYPE";
    private final static String VALUES = "POSSIBLE_VALUES";
    private final static String QUERYABLE = "QUERYABLE";
    
    private Integer id;
    private String description;
    private String vcfFieldName;
    private VCFAttributeNumber vcfNumber;
    private VCFHeaderLineType vcfType;
    private String displayName;
    private String fieldPath;
    private FieldType type;
    private List<String> possibleValues = new LinkedList<>();
    private boolean queryable = true;

    
    public VCFInfoDTO(){}
    
    public VCFInfoDTO(VCFField vcfFieldEntity, Set<String> possibleValues) {
        this.id = vcfFieldEntity.getFieldId();
        this.vcfFieldName = vcfFieldEntity.getFieldName();
        this.description = vcfFieldEntity.getDescription();
        this.vcfNumber = vcfFieldEntity.getVcfNumber();
        this.vcfType = vcfFieldEntity.getVcfFieldType();
        this.displayName = vcfFieldEntity.getDisplayName();
        this.fieldPath = vcfFieldEntity.getFieldPath();
        this.type = vcfFieldEntity.getFieldType();
        this.possibleValues.addAll(possibleValues);
        this.queryable = vcfFieldEntity.getQueryable();
    }

    public VCFInfoDTO(String fieldName, String description, FieldLocation location, VCFAttributeNumber vcfNumber, VCFHeaderLineType vcfType, boolean queryable) throws UnsupportedFieldException{
        init(fieldName,description,location, vcfNumber,vcfType, null, queryable);

    }

    public VCFInfoDTO(String fieldName, String description, FieldLocation location, VCFAttributeNumber vcfNumber, VCFHeaderLineType vcfType,Collection<String> possibleValues, boolean queryable) throws UnsupportedFieldException{
        init(fieldName,description,location, vcfNumber,vcfType, possibleValues, queryable);

    }

    public VCFInfoDTO(String fieldName, String description, FieldLocation location, VCFAttributeNumber vcfNumber, VCFHeaderLineType vcfType, Collection<String> possibleValues) throws UnsupportedFieldException{
        init(fieldName,description,location, vcfNumber,vcfType, possibleValues, null);
    } 
    
    public VCFInfoDTO(String fieldName, String description, FieldLocation location, VCFAttributeNumber vcfNumber, VCFHeaderLineType vcfType) throws UnsupportedFieldException {
        init(fieldName,description,location, vcfNumber,vcfType, null, null);
    }

    @JsonProperty(ID)
    public Integer getId() {
        return id;
    }
    
    @JsonProperty(DESCRIPTION)
    public String getDescription() {
        return description;
    }
    

    @JsonProperty(NUMBER)
    public VCFAttributeNumber getVcfNumber() {
        return vcfNumber;
    }
    
    @JsonProperty(VCF_TYPE)
    public VCFHeaderLineType getVcfType() {
        return vcfType;
    }
    
    @JsonProperty(DISPLAY_NAME)
    public String getDisplayName() {
        return displayName;
    }
    
    @JsonProperty(FIELD_PATH)
    public String getFieldPath() {
        return fieldPath;
    }
    
    @JsonProperty(TYPE)
    public FieldType getType() {
        return type;
    }

    @JsonProperty(VALUES)
    public List<String> getPossibleValues() {
        return possibleValues;
    }

    @JsonProperty(QUERYABLE)
    public Boolean getQueryable() {
        return queryable;
    }
    
    @JsonIgnore
    public String getVcfFieldName() {
        return vcfFieldName;
    }
    
    
    public VCFField toEntity() {
        
        VCFField entity = new VCFField();
        entity.setFieldPath(fieldPath);
        entity.setFieldName(vcfFieldName);
        entity.setDisplayName(displayName);
        entity.setDescription(description);
        entity.setVcfNumber(vcfNumber);
        entity.setVcfFieldType(vcfType);
        entity.setFieldType(type);     
        entity.setQueryable(queryable);
        if (!this.fieldPath.equals(VariantModel.CHROM) && !this.fieldPath.equals(VariantModel.FILTER)){
            entity.setPossibleValues(VCFFieldsUtils.setToString(this.possibleValues));
        }
        
        return entity;
       
    }
    
    public void addPossibleValue(String value) {
        possibleValues.add(value);
    }
    
   
    
    
    private void init(String vcfFieldName, String description, FieldLocation location, VCFAttributeNumber vcfNumber, VCFHeaderLineType vcfType, Collection<String> possibleValues, Boolean queryable) 
            throws UnsupportedFieldException{
        this.vcfFieldName = vcfFieldName;
        this.description = description;
        this.vcfNumber = vcfNumber;
        this.vcfType = vcfType;
               
        switch (location) {
            case VARIANT_INFO:
            case SAMPLE_FORMAT: 
            case SAMPLE_GENOTYPE:
            case VARIANT:
            case SAMPLE:
            case TRANSCRIPT:
            case ANNOTATIONS:
                this.displayName = vcfFieldName;
                setType(vcfNumber,vcfType);
                break;
            case REF_ALLELE:
                if (vcfNumber.equals(VCFAttributeNumber.R)) {
                    this.displayName = "REF:" + vcfFieldName;
                    setType(VCFAttributeNumber.VALUE, vcfType);
                }else{
                    throw new UnsupportedFieldException("Attribute Number "+vcfNumber+" non supported for REF allele fields");
                }
                break;
            case ALLELE:
                if (vcfNumber.equals(VCFAttributeNumber.R)
                        || vcfNumber.equals(VCFAttributeNumber.A)) {
                    this.displayName = "ALT:" + vcfFieldName;
                    setType(VCFAttributeNumber.VALUE, vcfType);
                }else{
                    throw new UnsupportedFieldException("Attribute Number "+vcfNumber+" non supported for ALT allele fields");
                }
                break;
            default:
                throw new UnsupportedFieldException("Location "+location+" non supported");
        }
        
        this.fieldPath = location.getPrefix()+vcfFieldName;
        
        if (possibleValues!=null) this.possibleValues.addAll(possibleValues);
        if (queryable!=null) this.queryable = queryable;
    }
    
     private void setType(VCFAttributeNumber fieldNumber, VCFHeaderLineType fieldType) throws UnsupportedFieldException{
        switch (fieldNumber) {
            case VALUE:
                switch (fieldType) {
                    case Integer:
                        type = FieldType.INTEGER;
                        break;
                    case Float:
                        type = FieldType.DOUBLE;
                        break;
                    case Character:
                    case String:
                        type = FieldType.STRING;
                        break;
                    case Flag:
                        type = FieldType.FLAG;
                        break;
                    default:
                        throw new UnsupportedFieldException("Type "+fieldType+" non supported for VALUE fields");    
                }
                break;
            case ARRAY:
                switch (fieldType) {
                    case Integer:
                        type = FieldType.ARRAY_INT;
                        break;
                    case Float:
                        type = FieldType.ARRAY_DOUBLE;
                        break;
                    case Character:
                    case String:
                        type = FieldType.ARRAY_STR;
                        break;
                    default:
                        throw new UnsupportedFieldException("Type "+fieldType+" non supported for ARRAY fields"); 
                }
                break;
            default:
                throw new UnsupportedFieldException("Number "+fieldNumber+" non supported to assign type"); 
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VCFInfoDTO that = (VCFInfoDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
