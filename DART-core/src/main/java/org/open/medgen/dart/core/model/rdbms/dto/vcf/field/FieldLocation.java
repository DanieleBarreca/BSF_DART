/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.dto.vcf.field;

/**
 *
 * @author dbarreca
 */
public enum FieldLocation {
    VARIANT(""),
    VARIANT_INFO("INFO."),
    SAMPLE("SAMPLE."),
    SAMPLE_FORMAT("SAMPLE.FORMAT."),
    SAMPLE_GENOTYPE("SAMPLE.GENOTYPE."),
    REF_ALLELE("REF_ALLELE."),
    ALLELE("ALLELE."),
    TRANSCRIPT("TRANSCRIPT."),
    ANNOTATIONS("ANNOTATIONS."),;
    
    private final String prefix;

    FieldLocation(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix ;
    }
     }
