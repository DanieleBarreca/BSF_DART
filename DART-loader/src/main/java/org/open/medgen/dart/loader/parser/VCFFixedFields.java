/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.parser;

import org.open.medgen.dart.core.model.mongo.variant.Zygosity;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFAttributeNumber;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.FieldLocation;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.UnsupportedFieldException;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import htsjdk.variant.vcf.VCFHeaderLineType;

import java.util.*;

/**
 *
 * @author dbarreca
 */
public class VCFFixedFields {

    public static List<VCFInfoDTO> getFixedFields(Set<String> contigs, Set<String> filters, VCFType type) {
        List<VCFInfoDTO> result = new LinkedList();

        addFixedField(
                result,
                "CHROM",
                "Indication of the chromosome (or, in general, a contig) carrying the variation",
                FieldLocation.VARIANT,
                VCFAttributeNumber.VALUE,
                VCFHeaderLineType.String,
                contigs
        );

        addFixedField(
                result,
                "POS",
                "The reference position, with the 1st base having position 1",
                FieldLocation.VARIANT,
                VCFAttributeNumber.VALUE,
                VCFHeaderLineType.Integer
        );
        addFixedField(
                result,
                "IDS",
                "List of unique identifiers where available. If this is a dbSNP variant the rs number(s) is used.",
                FieldLocation.VARIANT,
                VCFAttributeNumber.ARRAY,
                VCFHeaderLineType.String
        );
        addFixedField(
                result,
                "QUAL",
                "Phred-scaled quality score for the assertion made in ALT",
                FieldLocation.VARIANT,
                VCFAttributeNumber.VALUE,
                VCFHeaderLineType.Float
        );
        addFixedField(
                result,
                "FILTER",
                "Filter status: is null if this position has passed all filters, i.e. a call is made at this position. Otherwise if the site has not passed all filters, a slist of codes for filters that fail",
                FieldLocation.VARIANT,
                VCFAttributeNumber.ARRAY,
                VCFHeaderLineType.String,
                filters
        );
        addFixedField(
                result,
                "SAMPLE_NAME",
                "Label used to identify the sample",
                FieldLocation.SAMPLE,
                VCFAttributeNumber.VALUE,
                VCFHeaderLineType.String
        );
        addFixedField(
                result,
                "GT",
                "Genotype",
                FieldLocation.SAMPLE_GENOTYPE,
                VCFAttributeNumber.ARRAY,
                VCFHeaderLineType.Integer);
        addFixedField(
                result,
                "ZYGOSITY",
                "Zygosity",
                FieldLocation.SAMPLE_GENOTYPE,
                VCFAttributeNumber.VALUE,
                VCFHeaderLineType.String,
                Zygosity.getValues());

        addFixedField(
                result,
                "ALLELE",
                "Reference allele in CHROM at POS",
                FieldLocation.REF_ALLELE,
                VCFAttributeNumber.R,
                VCFHeaderLineType.String
        );
        addFixedField(
                result,
                "ALLELE",
                "Alternate allele in CHROM at POS",
                FieldLocation.ALLELE,
                VCFAttributeNumber.R,
                VCFHeaderLineType.String
        );
        addFixedField(
                result,
                "INDEX",
                "Allele index in the VCF file",
                FieldLocation.ALLELE,
                VCFAttributeNumber.A,
                VCFHeaderLineType.Integer
        );
        
         addFixedField(
                result,
                "OTHER_SAMPLES",
                "Other samples with the same allele",
                FieldLocation.VARIANT,
                VCFAttributeNumber.ARRAY,
                VCFHeaderLineType.String
        );
         
        return result;
    }

    private static void addFixedField(List<VCFInfoDTO> fields, String id, String description, FieldLocation location, VCFAttributeNumber number, VCFHeaderLineType type, Collection<String> possibleValues) {
        try {
            fields.add(new VCFInfoDTO(id, description, location, number, Utils.convertType(type), possibleValues));
        } catch (UnsupportedFieldException e) {
            System.out.println("WARN: " + e.getMessage());
        }
    }

    private static void addFixedField(List<VCFInfoDTO> fields, String id, String description, FieldLocation location, VCFAttributeNumber number, VCFHeaderLineType type) {
        try {
            org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFHeaderLineType myType = org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFHeaderLineType.valueOf(type.name());
            fields.add(new VCFInfoDTO(id, description, location, number, Utils.convertType(type)));
        } catch (UnsupportedFieldException e) {
            System.out.println("WARN: " + e.getMessage());
        }
    }
}
