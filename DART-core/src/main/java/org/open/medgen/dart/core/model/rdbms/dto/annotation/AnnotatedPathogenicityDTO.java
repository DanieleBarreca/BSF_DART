package org.open.medgen.dart.core.model.rdbms.dto.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.SampleDTO;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.VariantAnnotation;

public class AnnotatedPathogenicityDTO extends AnnotatedVariantDTO{
    
    public final static String CONDITION = "CONDITION";
    public final static String INHERITANCE = "INHERITANCE";
    public final static String SAMPLE = "SAMPLE";
    

    @JsonProperty(CONDITION)
    private ConditionTermDTO condition;

    @JsonProperty(INHERITANCE)
    private InheritanceTermDTO inheritance;
    
    @JsonProperty(ANNOTATION)
    private AnnotationTermDTO annotationTerm;
    

    public AnnotatedPathogenicityDTO() {
    }


    public ConditionTermDTO getCondition() {
        return condition;
    }

    public void setCondition(ConditionTermDTO condition) {
        this.condition = condition;
    }

    public InheritanceTermDTO getInheritance() {
        return inheritance;
    }

    public void setInheritance(InheritanceTermDTO inheritance) {
        this.inheritance = inheritance;
    }

    public AnnotationTermDTO getAnnotationTerm() {
        return annotationTerm;
    }

    public void setAnnotationTerm(AnnotationTermDTO annotationTerm) {
        this.annotationTerm = annotationTerm;
    }
}
