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
public class QueryParsingException extends MongoServiceException{

    public QueryParsingException(String message) {
        super(message);
    }

    public QueryParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryParsingException(Throwable cause) {
        super(cause);
    }

    public QueryParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
