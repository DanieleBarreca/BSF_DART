/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.parser;

import org.open.medgen.dart.core.model.mongo.variant.AttributeMap;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFAttributeNumber;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import org.open.medgen.dart.loader.parser.vep_config.VEPPreferences;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dbarreca
 */
public class VEPCSQParser {
    
    
    private final List<String> csqSubfields = new ArrayList<>();
    private final Map<String, VCFInfoDTO> csqFields = new LinkedHashMap<>();
    private boolean isValid = true;
    private String geneField;
    private String genomicChangeField;
    private String codingChangeField;
    private VEPPreferences vepPreferences;
    
    public VEPCSQParser(VCFInfoHeaderLine csqInfoFieldHeader, VEPPreferences vepPreferences, String geneField, String genomicChangeField, String codingChangeField) {
        this.vepPreferences = vepPreferences;
        this.geneField = geneField;
        this.genomicChangeField = genomicChangeField;
        this.codingChangeField = codingChangeField;
        
        System.out.println(csqInfoFieldHeader.getDescription());
        for (String field : csqInfoFieldHeader.getDescription().replaceFirst(this.vepPreferences.getDescriptionPrefix(), "").split(VEPPreferences.SEPARATOR)) {
            String normalizedName = field.trim().toLowerCase();
            csqSubfields.add(normalizedName);
            if (this.vepPreferences.getSplitFields().containsKey(normalizedName)) {
                for (String subFieldName : this.vepPreferences.getSplitFields().get(normalizedName).getSubFieldsNames()) {
                    String normalizedSubName = subFieldName.trim().toLowerCase();
                    if (!this.vepPreferences.getVepFields().containsKey(normalizedSubName)){
                        System.out.println("WARN: Field "+normalizedSubName+" not found in configuration");
                        isValid=false;
                    }else{
                        csqFields.put(subFieldName, this.vepPreferences.getVepFields().get(normalizedSubName));
                    }
                }
            } else {
                 if (!this.vepPreferences.getVepFields().containsKey(normalizedName)){
                        System.out.println("WARN: Field "+normalizedName+" not found in configuration");
                        isValid=false;
                    }else{
                        csqFields.put(field, this.vepPreferences.getVepFields().get(normalizedName));
                    }
            }

        }

    }
    
    public boolean hasAlleleIndex(){
        return csqSubfields.contains("allele_num");
    }

    public boolean isIsValid() {
        return isValid;
    }
    
    

    public Map<String, VCFInfoDTO> getCsqFields() {
        return csqFields;
    }
    
    public List<AttributeMap> parseTranscripts(List<String> transcripts){
       List<AttributeMap> result = new ArrayList<>();
       for (String transcript: transcripts){
           if (transcript==null || transcript.isEmpty()){
               continue;
           }
           String[] transcriptFieldValues = transcript.split(VEPPreferences.SEPARATOR);
           AttributeMap transcriptMap = new AttributeMap();
           
           for (int i=0; i<transcriptFieldValues.length; i++){
               String transcriptFieldName = csqSubfields.get(i);
               String transcriptFieldValue = transcriptFieldValues[i];
               
               if (this.vepPreferences.getSplitFields().containsKey(transcriptFieldName)){
                   String[] transcriptSubFieldValues = transcriptFieldValue.split(this.vepPreferences.getSplitFields().get(transcriptFieldName).getSeparator());
                   for (int j=0;j<transcriptSubFieldValues.length;j++){
                       String transcriptSubFieldName = this.vepPreferences.getSplitFields().get(transcriptFieldName).getSubFieldsNames().get(j).trim().toLowerCase();
                       String transcriptSubFielValue = transcriptSubFieldValues[j];
                       if ("\\(".equals(this.vepPreferences.getSplitFields().get(transcriptFieldName).getSeparator())){
                           transcriptSubFielValue = transcriptSubFielValue.replace(")", "");
                       }
                       addAttributeToMap(transcriptSubFieldName,transcriptSubFielValue,transcriptMap);
                    }
                }else{
                   addAttributeToMap(transcriptFieldName,transcriptFieldValue, transcriptMap);          
                   if (this.geneField != null && !this.geneField.trim().isEmpty() && this.geneField.trim().toLowerCase().equals(transcriptFieldName)) {
                       transcriptMap.put(VariantModel.GENE_FIELD_NAME, transcriptFieldValue);
                   }
                   if (this.genomicChangeField != null && !this.genomicChangeField.trim().isEmpty() && this.genomicChangeField.trim().toLowerCase().equals(transcriptFieldName)) {
                       transcriptMap.put(VariantModel.GENOMIC_CHANGE_FIELD_NAME, transcriptFieldValue);
                   }
                   if (this.codingChangeField != null && !this.codingChangeField.trim().isEmpty() && this.codingChangeField.trim().toLowerCase().equals(transcriptFieldName)) {
                       transcriptMap.put(VariantModel.CODING_CHANGE_FIELD_NAME, transcriptFieldValue);
                   }
               }
               
               
               
           }
           
           if (!transcriptMap.isEmpty()){
               result.add(transcriptMap);
           }
       }
       
       return result;
       
    }
    
    private void addAttributeToMap(String attributeName, String attributeValue,AttributeMap map){
        VCFInfoDTO fieldInfo = this.vepPreferences.getVepFields().get(attributeName);

        if (VCFAttributeNumber.ARRAY.equals(fieldInfo.getVcfNumber())){
            map.put(fieldInfo.getVcfFieldName(), 
                    VCFParser.getTypedValue(
                        fieldInfo.getVcfType(), 
                        Arrays.asList(attributeValue.split(VEPPreferences.ARRAY_SEPARTOR)),
                        true));
        }else{
            map.put(fieldInfo.getVcfFieldName(), 
                    VCFParser.getTypedValue(
                            fieldInfo.getVcfType(), 
                            attributeValue.replace(VEPPreferences.ARRAY_SEPARTOR, ","),
                            false));
           
        }
 
    }
    
    public static Map<String, String> getMetadata(VCFHeaderLine vepHeaderLine){
        Map<String, String> result = new HashMap();
        
        Matcher m = Pattern.compile("([^\"]\\S*)=(\".+?\")\\s*").matcher(vepHeaderLine.toString());
        while (m.find()){
            result.put(m.group(1), m.group(2).replace("\"", ""));
        }
        
        return result;
    }
    
    public static String extractVEPVersion(Map<String, String> metadata){
        return "VEP_"+metadata.get("VEP");
    }
    
    public static String extractGenome(Map<String, String> metadata){
        return metadata.get("assembly");
    }
    
    public static Date extractRunDate(Map<String, String> metadata) throws ParserInitializationException{
        //time="2018-08-19 21:26:37"
        
        String time =  metadata.get("time");
        
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(time);
        } catch (ParseException e) {
            throw new ParserInitializationException("Error while parsing VEP time", e);
        }
    }
    
    public static String buildDescription(Map<String, String> metadata){
        StringBuilder sb = new StringBuilder();
        
        boolean first = true;
        for (String prop: metadata.keySet()){
            if (!prop.equalsIgnoreCase("time") && !prop.equalsIgnoreCase("cache")){
                if (!first) 
                    sb.append(" "); 
                else 
                    first=false;
                
                sb.append(prop).append("=\"").append(metadata.get(prop)).append("\"");
                
            }
        }
        
        return sb.toString();
    }

        
}
