/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.service;

/**
 *
 * @author dbarreca
 */
public class VariantIterationError extends Error {

    public VariantIterationError() {
    }

    public VariantIterationError(String message) {
        super(message);
    }

    public VariantIterationError(String message, Throwable cause) {
        super(message, cause);
    }

    public VariantIterationError(Throwable cause) {
        super(cause);
    }

    public VariantIterationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
