/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.variant;

import org.open.medgen.dart.core.controller.ControllerException;


/**
 *
 * @author dbarreca
 */
public class MalformedQueryException extends ControllerException{

    
 
    public MalformedQueryException(String message) {
        super(message);
    }

    public MalformedQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedQueryException(Throwable cause) {
        super(cause);
    }

    public MalformedQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    
}
