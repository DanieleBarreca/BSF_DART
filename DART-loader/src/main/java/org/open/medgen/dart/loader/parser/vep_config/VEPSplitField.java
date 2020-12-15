/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.parser.vep_config;

import java.util.List;

/**
 *
 * @author dbarreca
 */
public class VEPSplitField {
    private final String fieldName;
    private final List<String> subFieldsNames;
    private final String separator;

    public VEPSplitField(String fieldName, List<String> subFieldsNames, String separator) {
        this.fieldName = fieldName;
        this.subFieldsNames = subFieldsNames;
        this.separator = separator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public List<String> getSubFieldsNames() {
        return subFieldsNames;
    }

    public String getSeparator() {
        return separator;
    }
    
    
}
