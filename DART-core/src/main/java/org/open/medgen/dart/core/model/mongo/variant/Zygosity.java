/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.variant;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author dbarreca
 */
public  enum Zygosity {
    HOMOZYGOUS_ALT,
    HETEROZYGOUS_ALT,
    HETEROZYGOUS_REF,
    UNKNOWN,
    HOMOZYGOUS_REF;
    
    
    public static Zygosity decode(String zygosity){
        for (Zygosity genotypeCat: Zygosity.values()){
            if (genotypeCat.toString().equalsIgnoreCase(zygosity)){
                return genotypeCat;
            }
        }
        
        return null;
    }
    
    public static List<String> getValues(){
        List<String> result = new LinkedList<>();
        
        for (Zygosity label: Zygosity.values()){
            result.add(label.toString());
        }
        
        return result;
    }
    
    
    public static Zygosity decode(List<Integer> genotype){
        if (genotype.contains(null) || genotype.contains(-1)){
            return UNKNOWN;
        }
        
        Integer ploidy = genotype.size();
        
        if (ploidy == 1) {
            Integer allele = genotype.get(0);
            if (allele == 0){
                return HOMOZYGOUS_REF;
            }else{
                return HOMOZYGOUS_ALT;
            }
        }else{
            Set<Integer> genotypeSet = new HashSet<>(genotype);
            boolean containsRef = genotypeSet.contains(0);
            boolean hasDifferentElements = genotypeSet.size() !=1;
            if (hasDifferentElements && containsRef) {
                return HETEROZYGOUS_REF;
            }else if (hasDifferentElements && !containsRef){
                return HETEROZYGOUS_ALT;
            }else if (!hasDifferentElements && containsRef){
                return HOMOZYGOUS_REF;
            }else{
                return HOMOZYGOUS_ALT;
            }
            
        }
    }
}
