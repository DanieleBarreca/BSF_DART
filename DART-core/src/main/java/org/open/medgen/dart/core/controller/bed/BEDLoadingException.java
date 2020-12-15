/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.bed;

import org.open.medgen.dart.core.controller.ControllerException;

/**
 *
 * @author dbarreca
 */
public class BEDLoadingException extends ControllerException{

    public BEDLoadingException() {
    }

    public BEDLoadingException(String message) {
        super(message);
    }

    public BEDLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BEDLoadingException(Throwable cause) {
        super(cause);
    }

    public BEDLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
