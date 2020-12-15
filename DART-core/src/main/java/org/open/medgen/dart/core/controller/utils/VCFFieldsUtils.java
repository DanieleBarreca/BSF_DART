/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.utils;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFField;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author dbarreca
 */
public class VCFFieldsUtils {
    
    private final static String SEPARATOR = ",";
    
    public static Set<String> stringToSet(String possibleValuesString) {
       return stringToSet(possibleValuesString,SEPARATOR);
    }
    
    public static Set<String> stringToSet(String possibleValuesString, String separator) {
        Set<String> result = new LinkedHashSet<>();

        if (possibleValuesString!=null){                    
            for (String value : possibleValuesString.split(separator)) {
                if (!value.trim().isEmpty()) {
                    result.add(value);
                }
            }
        }
        
        return result;
    }
    
    public static String setToString(Collection<String> possibleValuesSet) {
       return setToString(possibleValuesSet,SEPARATOR);
    }
    
    public static String setToString(Collection<String> possibleValuesSet, String separator) {
        boolean first = true;
        if (possibleValuesSet.isEmpty()){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String value: possibleValuesSet){
            if  (first) 
                first = false; 
            else 
                sb.append(separator);
            
            sb.append(value);
            
        }
        
        return sb.toString();
    }
    
    public static List<VCFInfoDTO> getInfoFields(List<VCFField> fieldEntities, String chromosomesValuesString, String filterValuesString){
        Set<String> chromValues = stringToSet(chromosomesValuesString);
        Set<String> filterValues = stringToSet(filterValuesString);
        
        return getInfoFields(fieldEntities, chromValues, filterValues);
    }
    
    public static List<VCFInfoDTO> getInfoFields(List<VCFField> fieldEntities, Set<String> chromosomesValues, Set<String> filterValues){
           return fieldEntities.stream().map(
                (VCFField field) -> {
                    Set<String> possibleValues;
                    if (field.getFieldPath().equals(VariantModel.CHROM)){
                        possibleValues = chromosomesValues;
                    }else if (field.getFieldPath().equals(VariantModel.FILTER)){
                        possibleValues = filterValues;
                    }else{
                        possibleValues = stringToSet(field.getPossibleValues());
                    }
                    
                    return new VCFInfoDTO(field,possibleValues);
                }
        ).collect(Collectors.toList());
    }
}
