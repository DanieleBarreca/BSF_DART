/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.model;

/**
 *
 * @author dbarreca
 */
public class AppResponse {
     
    private final AppResponseStatus status;
    private final String message;
    private final Object payload;

    public AppResponse(AppResponseStatus status, String message, Object payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public AppResponseStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getPayload() {
        return payload;
    }
    
    
}
