package org.open.medgen.dart.core.controller.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.mongo.variant.Zygosity;

public class RelatedSampleAnnotation {
    
    private final static String RELATED_SAMPLE_ZYGOSITY = "RELATED_SAMPLE_ZYGOSITY";
    private final static String RELATED_SAMPLE_AFFECTED = "RELATED_SAMPLE_AFFECTED";
    private final static String RELATED_SAMPLE_SEX = "RELATED_SAMPLE_SEX";
    private final static String INHERITANCE_RANK = "INHERITANCE_RANK";

    @JsonProperty(RELATED_SAMPLE_ZYGOSITY)
    private final String relatedSampleZygosity;

    @JsonProperty(RELATED_SAMPLE_SEX)
    private final String relatedSampleSex;

    @JsonProperty(RELATED_SAMPLE_AFFECTED)
    private final boolean relatedSampleAffected;

    @JsonProperty(INHERITANCE_RANK)
    private final Integer inheritanceRank;

    public RelatedSampleAnnotation(RelatedSamplesAnnotator.RelatedSampleZygosity relatedSampleZygosity,
                                   RelatedSamplesAnnotator.RelatedSampleSex relatedSampleSex,
                                   boolean relatedSampleAffected) {
        
        this.inheritanceRank = relatedSampleZygosity.ordinal();
        
        this.relatedSampleZygosity = relatedSampleZygosity.toString();
        this.relatedSampleAffected = relatedSampleAffected;
        this.relatedSampleSex = relatedSampleSex.toString();
     
    }


    public String getRelatedSampleZygosity() {
        return relatedSampleZygosity;
    }

    public boolean isRelatedSampleAffected() {
        return relatedSampleAffected;
    }

    public Integer getInheritanceRank() {
        return inheritanceRank;
    }

    public String getRelatedSampleSex() {
        return relatedSampleSex;
    }
}
