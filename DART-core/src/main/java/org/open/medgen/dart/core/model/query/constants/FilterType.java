/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.query.constants;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 * @author dbarreca
 */
public enum FilterType {
    STRING,
    INTEGER,
    DOUBLE,
    BOOLEAN;

    
    @JsonValue
    public String getAsJSON(){
        return this.name().toLowerCase();
    }
}
