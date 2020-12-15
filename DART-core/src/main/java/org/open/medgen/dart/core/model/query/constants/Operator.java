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
public enum Operator {
    EQUAL,
    NOT_EQUAL,
    IN,
    NOT_IN,
    LESS,
    LESS_OR_EQUAL,
    GREATER,
    GREATER_OR_EQUAL,
    BETWEEN,
    NOT_BETWEEN,
    BEGINS_WITH,
    NOT_BEGINS_WITH,
    CONTAINS,
    NOT_CONTAINS,
    ENDS_WITH,
    NOT_ENDS_WITH,
    IS_NULL,
    IS_NOT_NULL,
    ARRAY_SIZE,
    IS_NOT_EMPTY;
    
    @JsonValue
    public String getAsJSON(){
        return this.name().toLowerCase();
    }
}
