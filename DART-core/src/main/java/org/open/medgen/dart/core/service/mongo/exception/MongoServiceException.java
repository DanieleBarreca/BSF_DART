/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.exception;

/**
 *
 * @author dbarreca
 */
public class MongoServiceException extends Exception {

    public MongoServiceException() {
    }

    public MongoServiceException(String message) {
        super(message);
    }

    public MongoServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoServiceException(Throwable cause) {
        super(cause);
    }

    public MongoServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
