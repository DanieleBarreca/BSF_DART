package org.open.medgen.dart.core.model.rdbms.dto.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.VariantAnnotation;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.VariantSampleAnnotation;
import org.open.medgen.dart.core.model.rdbms.entity.enums.ValidationStatus;

import java.util.Date;

public class VariantSampleAnnotationDTO {

    private final static String REF_ID="REF_ID";
    private final static String VALIDATION_STATUS="VALIDATION_STATUS";
    
    private final static String ANNOTATION_USER="ANNOTATION_USER";
    private final static String ANNOTATION_DATE="ANNOTATION_DATE";

    private final static String VALID="VALID";

    @JsonProperty(REF_ID)
    private final Integer id;
    
    @JsonProperty(VALIDATION_STATUS)
    private final ValidationStatus validationStatus;

    @JsonProperty(ANNOTATION_USER)
    private final String annotationUser;

    @JsonProperty(ANNOTATION_DATE)
    private final Date annotationDate;

    @JsonProperty(VALID)
    private final boolean valid;
    
    public VariantSampleAnnotationDTO(VariantSampleAnnotation variantSampleAnnotation){
        this.id= variantSampleAnnotation.getDbId();
        this.validationStatus = variantSampleAnnotation.getValidationStatus();
        this.annotationUser = variantSampleAnnotation.getUserFrom().getLogin();
        
        this.annotationDate = variantSampleAnnotation.getDateFrom();

        this.valid = variantSampleAnnotation.getUserTo() == null;
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public String getAnnotationUser() {
        return annotationUser;
    }

    public Date getAnnotationDate() {
        return annotationDate;
    }
}
