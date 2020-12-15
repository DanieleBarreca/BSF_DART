/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.query.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author dbarreca
 */
public class QueryErrorResponse {
    
    private final String message;
    private final String stackTrace;

    public QueryErrorResponse(Throwable t) {
        this.message = t.toString();
        
        StringWriter errors = new StringWriter();        
        t.printStackTrace(new PrintWriter(errors));
        stackTrace = errors.toString();
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("stack")
    public String getStackTrace() {
        return stackTrace;
    }
                 
}
