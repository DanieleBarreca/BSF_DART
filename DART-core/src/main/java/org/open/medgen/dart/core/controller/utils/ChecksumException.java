/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.utils;

/**
 *
 * @author dbarreca
 */
public class ChecksumException extends Exception {

    public ChecksumException() {
    }

    public ChecksumException(String message) {
        super(message);
    }

    public ChecksumException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChecksumException(Throwable cause) {
        super(cause);
    }

    public ChecksumException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
