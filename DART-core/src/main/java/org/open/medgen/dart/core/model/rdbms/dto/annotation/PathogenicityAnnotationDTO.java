package org.open.medgen.dart.core.model.rdbms.dto.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.VariantAnnotation;

import java.util.Date;

public class PathogenicityAnnotationDTO {

    private final static String REF_ID="REF_ID";
    private final static String ANNOTATION="ANNOTATION";
    private final static String CONDITION="CONDITION";
    private final static String INHERITANCE="INHERITANCE";
    
    private final static String ANNOTATION_USER="ANNOTATION_USER";
    private final static String ANNOTATION_DATE="ANNOTATION_DATE";

    private final static String VALID="VALID";

    @JsonProperty(REF_ID)
    private final Integer id;
    
    @JsonProperty(ANNOTATION)
    private final AnnotationTermDTO annotation;

    @JsonProperty(CONDITION)
    private final ConditionTermDTO condition;

    @JsonProperty(INHERITANCE)
    private final InheritanceTermDTO inheritanceTerm;

    @JsonProperty(ANNOTATION_USER)
    private final String annotationUser;

    @JsonProperty(ANNOTATION_DATE)
    private final Date annotationDate;

    @JsonProperty(VALID)
    private final boolean valid;
    
    public PathogenicityAnnotationDTO(VariantAnnotation annotatedVariant){
        this.id= annotatedVariant.getDbId();
        this.annotation = new AnnotationTermDTO(annotatedVariant.getAnnotation());
        this.condition = new ConditionTermDTO(annotatedVariant.getCondition());
        this.inheritanceTerm = new InheritanceTermDTO(annotatedVariant.getInheritance());
        this.annotationUser = annotatedVariant.getUserFrom().getLogin();
        
        this.annotationDate = annotatedVariant.getDateFrom();
        this.valid = annotatedVariant.getUserTo() == null;
    }

    public AnnotationTermDTO getAnnotation() {
        return annotation;
    }

    public String getAnnotationUser() {
        return annotationUser;
    }

    public Date getAnnotationDate() {
        return annotationDate;
    }

    public boolean isValid() {
        return valid;
    }
}
