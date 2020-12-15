/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.dto.vcf.field;

/**
 *
 * @author dbarreca
 */
public class UnsupportedFieldException extends Exception {

    public UnsupportedFieldException() {
    }

    public UnsupportedFieldException(String message) {
        super(message);
    }

    public UnsupportedFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedFieldException(Throwable cause) {
        super(cause);
    }
    
}
