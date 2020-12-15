package org.open.medgen.dart.core.model.rdbms.dto.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.rdbms.entity.enums.ValidationStatus;

public class AnnotatedVariantSampleDTO extends AnnotatedVariantDTO{
    
    public final static String VALIDATION_STATUS = "VALIDATION_STATUS";
    
    
    @JsonProperty(VALIDATION_STATUS)
    private ValidationStatus validationStatus;
    

    public AnnotatedVariantSampleDTO() {
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }
}
