/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.variant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bson.codecs.pojo.annotations.BsonIgnore;

/**
 *
 * @author dbarreca
 */

public class GenotypeModel extends AttributeMap implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    public final static String GT = "GT";
    public final static String ZYGOSITY = "ZYGOSITY";
    public final static String PHASED = "PHASED";

    public GenotypeModel() {
        put(ZYGOSITY, Zygosity.UNKNOWN.name());
    }
    
    public GenotypeModel(List<Integer>  alleles, boolean phased) {
        setGenotype(alleles,phased);
    }

    public GenotypeModel(Map<? extends String, ? extends Object> m) {
        super(m);
    }
    
    public GenotypeModel(String genotype) {
        setGenotype(genotype);
    }
    
    @JsonIgnore
    @BsonIgnore
    public List<Integer> getGenotype() {
        return getAttributeAsIntArray(GT);
    }
    
    @JsonIgnore
    @BsonIgnore
    public String getGenotypeString() {
        String separator = isPhased() ? "|" : "/";
        Iterator<Integer> gt = getGenotype().iterator();
        
        if (!gt.hasNext()) return "";
        
        StringBuilder sb = new StringBuilder();
        sb.append(gt.next());
        while (gt.hasNext()){
            sb.append(separator).append(gt.next());
        }
        
        return sb.toString();
        
    }
    
    @JsonIgnore
    @BsonIgnore
    public Zygosity getZygosity() {
        return Zygosity.decode(getAttributeAsString(ZYGOSITY));
    }
        
    @JsonIgnore
    @BsonIgnore
    public Boolean isPhased() {
        return getAttributeAsFlag(PHASED);
    }

    @BsonIgnore
    public void setGenotype(List<Integer> gt, boolean phased) {
        put(GT,gt);
        put(ZYGOSITY,Zygosity.decode(gt).name());
        put(PHASED, phased);
    }

    @BsonIgnore
    public void setGenotype(String gt) {
        String separator = "/";
        boolean phased = false;
        
        if (gt.contains("|")){
            separator = "|";
            phased = true;
        }
        
        List<Integer> result = new LinkedList<>();
        for (String allele : gt.split(separator)) {
            if (".".equalsIgnoreCase(allele)){
                result.add(-1);
            }else{
                result.add(Integer.parseInt(allele));
            }
        }
        
        setGenotype(result,phased);
    }
   
    public Object getAttribute(String attributePath){
        
        switch(attributePath) {
            case "GT":
                return getGenotypeString();    
            default:
                return get(attributePath);
        }
    }
}
