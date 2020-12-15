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
public enum VCFAttributeNumber {
    A,
    R,
    G,
    VALUE,
    ARRAY;
    
    public static VCFAttributeNumber decode(String countType, Integer countNumber) {
        switch(countType){
            case "INTEGER":
                if (countNumber<=1){
                    return VALUE;
                }else{
                    return ARRAY;
                }
            case "UNBOUNDED":
                return ARRAY;
            case "A":
                return A;
            case "R":
                return R;
            case "G":
                return G;
        }
        
        return null;
    }
    
    public static VCFAttributeNumber decode(String attributeNumber) {
        switch(attributeNumber){           
            case ".":
                return ARRAY;
            case "A":
                return A;
            case "R":
                return R;
            case "G":
                return G;
            default:
                try{
                    Integer number = Integer.parseInt(attributeNumber);
                    if (number <=1) {
                        return VALUE;
                    }else{
                        return ARRAY;
                    }
                }catch (NumberFormatException e){
                    return null;
                }
        }
    }
}
