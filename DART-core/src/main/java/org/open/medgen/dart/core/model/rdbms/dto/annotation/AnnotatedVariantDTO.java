package org.open.medgen.dart.core.model.rdbms.dto.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.SampleDTO;

public abstract class AnnotatedVariantDTO {

    public final static String REF_GENOME = "REF_GENOME";
    public final static String HGVSG = "HGVSg";
    public final static String HGVSC = "HGVSc";
    public final static String GENE = "GENE";
    public final static String ANNOTATION = "ANNOTATION";
    public final static String SAMPLE = "SAMPLE";
    public final static String VARIANT_MODEL_REF = "VARIANT_MODEL_REF";

    @JsonProperty(REF_GENOME)
    private String refGenome;

    @JsonProperty(HGVSG)
    private String hgvsg;

    @JsonProperty(HGVSC)
    private String hgvsc;

    @JsonProperty(GENE)
    private String gene;

    @JsonProperty(SAMPLE)
    private SampleDTO sample;

    @JsonProperty(VARIANT_MODEL_REF)
    private String variantModelRef;

    public AnnotatedVariantDTO() {
    }

    public String getRefGenome() {
        return refGenome;
    }

    public String getHgvsg() {
        return hgvsg;
    }

    public String getHgvsc() {
        return (hgvsc==null || hgvsc.trim().isEmpty()) ?  null : hgvsc;
    }

    public String getGene() {
        return gene;
    }

    public SampleDTO getSample() {
        return sample;
    }

    public void setSample(SampleDTO sample) {
        this.sample = sample;
    }

    public String getVariantModelRef() {
        return variantModelRef;
    }

    public void setVariantModelRef(String variantModelRef) {
        this.variantModelRef = variantModelRef;
    }
}
