/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.parser.vep_config;

import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFAttributeNumber;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.FieldLocation;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.UnsupportedFieldException;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFHeaderLineType;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author dbarreca
 */
public class VEPPreferences {
    private final static String DEFAULT_CSQ_FIELD_KEY = "CSQ";
    private final static String DEFAULT_CSQ_FIELD_PREFIX = DEFAULT_CSQ_FIELD_KEY + ":";
    
    public final static String DEFAULT_DESCRIPTION_PREFIX = "Consequence annotations from Ensembl VEP. Format: ";
    public final static String SEPARATOR = "\\|";
    public final static String ARRAY_SEPARTOR = "&";
    
    private final Map<String, VCFInfoDTO> vepFields = new ConcurrentHashMap<>(); 
    private final Map<String, VEPSplitField> splitFields = new ConcurrentHashMap<>();
    private final Map<String, Set<OntologyTerm>> vepEnums = new ConcurrentHashMap<>();

    private String csqFieldKey;
    private String csqFieldPrefix;
    private String descriptionPrefix;
    
    public static class Builder {
        private InputStream enumStream;
        private InputStream vepFieldsStream;
        private File enumFile;
        private File vepFieldsFile;
        private String csqFieldKey;
        private String csqFieldPrefix;
        private String descriptionPrefix;

        public Builder() {
            this.csqFieldKey = DEFAULT_CSQ_FIELD_KEY;
            this.csqFieldPrefix = DEFAULT_CSQ_FIELD_PREFIX;
            this.descriptionPrefix = DEFAULT_DESCRIPTION_PREFIX;
        }

        public Builder setCsqFieldPrefix(String csqFieldPrefix) {
            this.csqFieldPrefix = csqFieldPrefix;
            return(this);
        }
        public Builder setDescriptionPrefix(String descriptionPrefix) {
            this.descriptionPrefix = descriptionPrefix;
            return(this);
        }

        public Builder setEnumStream(InputStream enumStream) {
            this.enumStream = enumStream;
            return(this);
        }


        public Builder setVepFieldsStream(InputStream vepFieldsStream) {
            this.vepFieldsStream = vepFieldsStream;
            return(this);
        }

        public Builder setEnumFile(File enumFile) {
            this.enumFile = enumFile;
            return(this);
        }

        public Builder setVepFieldsFile(File vepFieldsFile) {
            this.vepFieldsFile = vepFieldsFile;
            return(this);
        }
        public VEPPreferences build(){
            VEPPreferences pref = new VEPPreferences();
            pref.csqFieldPrefix = this.csqFieldPrefix;
            pref.descriptionPrefix = this.descriptionPrefix;
            pref.csqFieldKey = this.csqFieldKey;
            pref.init_split_fields();
            VEPPreferences.loadResources(this.enumFile, this.enumStream, "VEPEnums.tsv", stream -> pref.loadEnums(stream));
            VEPPreferences.loadResources(this.vepFieldsFile, this.vepFieldsStream, "VEPOutputFields.tsv", stream -> pref.loadVepOutputFields(stream));
            return(pref);
        }
    }

    private VEPPreferences() {
        
    }
    
    private void init_split_fields() {
        splitFields.put("exon", new VEPSplitField("EXON", Arrays.asList("EXON_AFFECTED", "EXON_TOTAL"), "/"));
        splitFields.put("intron",new VEPSplitField("INTRON", Arrays.asList("INTRON_AFFECTED", "INTRON_TOTAL"), "/"));
        splitFields.put("cdna_position",new VEPSplitField("cDNA_position", Arrays.asList("cDNA_position_start", "cDNA_position_end"), "-"));
        splitFields.put("cds_position",new VEPSplitField("CDS_position", Arrays.asList("CDS_position_start", "CDS_position_end"), "-"));
        splitFields.put("cds_position",new VEPSplitField("CDS_position", Arrays.asList("CDS_position_start", "CDS_position_end"), "-"));
        splitFields.put("protein_position",new VEPSplitField("Protein_position", Arrays.asList("Protein_position_start", "Protein_position_end"), "-"));
        splitFields.put("polyphen",new VEPSplitField("PolyPhen", Arrays.asList("PolyPhen_prediction", "PolyPhen_score"), "\\("));
        splitFields.put("sift",new VEPSplitField("SIFT", Arrays.asList("SIFT_prediction", "SIFT_score"), "\\("));
    }
    
    private static void loadResources(File file, InputStream inputStream, String defaultResource, Consumer<InputStream> process) {
        try {
            if (Objects.nonNull(file)) {
                try (InputStream stream = new FileInputStream(file)) {
                    process.accept(stream);
                }
            }
            if (Objects.nonNull(inputStream)) {
                process.accept(inputStream);
            }
            try (InputStream stream = VEPPreferences.class.getClassLoader().getResourceAsStream(defaultResource)) {
                process.accept(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void loadEnums(InputStream stream) throws RuntimeException {
        try {
            Iterable<CSVRecord> records = CSVFormat.RFC4180
                    .withDelimiter('\t')
                    .withHeader(VEPEnumsHeader.class)
                    .withSkipHeaderRecord()
                    .parse(new InputStreamReader(stream));

            for (CSVRecord record : records) {
                OntologyTerm term = new OntologyTerm(
                        record.get(VEPEnumsHeader.TERM),
                        record.get(VEPEnumsHeader.DESCRIPTION),
                        record.get(VEPEnumsHeader.ACCESSION)
                );

                Set<OntologyTerm> terms = vepEnums.get(record.get(VEPEnumsHeader.FIELD));
                if (terms == null) {
                    terms = Collections.synchronizedSet(new LinkedHashSet<>());
                    vepEnums.put(record.get(VEPEnumsHeader.FIELD), terms);
                }
                // check for Term name only!!!
                if (! terms.stream().anyMatch(t -> t.getTerm().equals(term.getTerm()))) {
                    terms.add(term);
                }
                
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void loadVepOutputFields(InputStream stream) {
        try {
            Iterable<CSVRecord> records = CSVFormat.RFC4180
                    .withDelimiter('\t')
                    .withHeader(VEPPreferencesHeader.class)
                    .withSkipHeaderRecord()
                    .parse(new InputStreamReader(stream));
    
            for (CSVRecord record:records){
                try{
                    VCFInfoDTO field = new VCFInfoDTO(
                            this.csqFieldPrefix+record.get(VEPPreferencesHeader.FIELD_NAME),
                            record.get(VEPPreferencesHeader.FIELD_DESCRPPTION),
                            FieldLocation.TRANSCRIPT,
                            VCFAttributeNumber.decode(record.get(VEPPreferencesHeader.FIELD_NUMBER)),
                            VCFHeaderLineType.valueOf(record.get(VEPPreferencesHeader.FIELD_TYPE))
                    );
                    if (vepEnums.containsKey(field.getVcfFieldName())){
                        for (OntologyTerm term: this.getVepEnums().get(field.getVcfFieldName())){
                            field.addPossibleValue(term.getTerm());
                        }
                    }
                    String key = record.get(VEPPreferencesHeader.FIELD_NAME).trim().toLowerCase();
                    if (!vepFields.containsKey(key)) { // Only add if not exist
                        vepFields.put(key, field);
                    }
                }catch(UnsupportedFieldException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, VCFInfoDTO> getVepFields() {
        return vepFields;
    }

    public Map<String, VEPSplitField> getSplitFields() {
        return splitFields;
    }

    public Map<String, Set<OntologyTerm>> getVepEnums() {
        return vepEnums;
    }

    public String getCsqFieldPrefix() {
        return csqFieldPrefix;
    }

    public String getDescriptionPrefix() {
        return descriptionPrefix;
    }

    public String getCsqFieldKey() {
        return csqFieldKey;
    }
}
