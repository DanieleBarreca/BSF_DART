/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.parser;

import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFHeaderLineType;




/**
 *
 * @author dbarreca
 */
public class Utils {
    
    public static VCFHeaderLineType convertType(htsjdk.variant.vcf.VCFHeaderLineType type){
        return VCFHeaderLineType.valueOf(type.name());
    }
}
