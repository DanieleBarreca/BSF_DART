package org.open.medgen.dart.core.model.rdbms.dto.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.AnnotationDictionary;

public class AnnotationTermDTO {

    public final static String REF_ID = "REF_ID";
    public final static String LABEL = "LABEL";
    public final static String TYPE = "TYPE";
    public final static String DESCRIPTION = "DESCRIPTION";
    public final static String RANK = "RANK";
    
    @JsonProperty(REF_ID)
    private Integer dbId;

    @JsonProperty(LABEL)
    private String label;

    @JsonProperty(DESCRIPTION)
    private String description;
    
    @JsonProperty(RANK)
    private Integer rank;

    @JsonProperty(TYPE)
    private String type;

    public AnnotationTermDTO() {
    }

    public AnnotationTermDTO(AnnotationDictionary annotationDictionary) {
        this.dbId = annotationDictionary.getDbId();
        this.label = annotationDictionary.getAnnotationLabel();
        this.description = annotationDictionary.getAnnotationDescription();
        this.rank = annotationDictionary.getRank();
        this.type = annotationDictionary.getAnnotationType();
                
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
