/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.service.exception;

/**
 *
 * @author dbarreca
 */
public class RDBMSServiceException extends Exception{

    public RDBMSServiceException() {
    }

    public RDBMSServiceException(String message) {
        super(message);
    }

    public RDBMSServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RDBMSServiceException(Throwable cause) {
        super(cause);
    }

    public RDBMSServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
