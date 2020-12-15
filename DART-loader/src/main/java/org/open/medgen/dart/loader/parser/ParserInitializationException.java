/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.parser;

/**
 *
 * @author dbarreca
 */
public class ParserInitializationException extends Exception {

    public ParserInitializationException() {
    }

    public ParserInitializationException(String message) {
        super(message);
    }

    public ParserInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserInitializationException(Throwable cause) {
        super(cause);
    }

    public ParserInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
