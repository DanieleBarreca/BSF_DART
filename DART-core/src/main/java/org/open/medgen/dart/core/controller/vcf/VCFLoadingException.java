/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.vcf;

import org.open.medgen.dart.core.controller.ControllerException;

/**
 *
 * @author dbarreca
 */
public class VCFLoadingException extends ControllerException{

    public VCFLoadingException() {
    }

    public VCFLoadingException(String message) {
        super(message);
    }

    public VCFLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public VCFLoadingException(Throwable cause) {
        super(cause);
    }

    public VCFLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
