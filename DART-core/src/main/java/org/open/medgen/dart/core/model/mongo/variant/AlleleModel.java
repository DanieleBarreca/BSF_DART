/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.variant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Map;
import org.bson.codecs.pojo.annotations.BsonIgnore;

/**
 *
 * @author dbarreca
 */
public class AlleleModel extends AttributeMap implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public final static String ALLELE = "ALLELE";
    public final static String INDEX  = "INDEX";
    
    public AlleleModel() {
        super();
        put(ALLELE,".");
        put(INDEX, -1);
    }

    public AlleleModel(Map<? extends String, ? extends Object> m) {
        super(m);
    }
    
    public AlleleModel(String allele, Integer index) {
        put(ALLELE,allele);
        put(INDEX, index);
    }
    
    @JsonIgnore
    @BsonIgnore
    public String getAllele() {
        return getAttributeAsString(ALLELE);
    }
    
    @JsonIgnore
    @BsonIgnore
    public Integer getAlleleIndex() {
        return getAttributeAsInt(INDEX);
    }
   
    
}
