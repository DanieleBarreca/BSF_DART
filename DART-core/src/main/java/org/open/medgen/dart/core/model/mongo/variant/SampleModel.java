/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.variant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Map;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 *
 * @author dbarreca
 */
public class SampleModel implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public final static String SAMPLE_NAME = "SAMPLE_NAME";
    public final static String SAMPLE_GENOTYPE = "GENOTYPE";
    public final static String FORMAT = "FORMAT";

    
    @BsonProperty(SAMPLE_NAME)
    private String name = "UNKNOWN";
    
    private final GenotypeModel sampleGenotype;
    
    private final AttributeMap format;
    
    public SampleModel(String name) {
        this.name = name;
        this.sampleGenotype = new GenotypeModel();
        this.format = new AttributeMap();
    }

    public SampleModel() {
        this.sampleGenotype = new GenotypeModel();
        this.format = new AttributeMap();
    }

    @BsonCreator
    public SampleModel(@BsonProperty(SAMPLE_GENOTYPE) Map<String, Object> sampleGenotype, @BsonProperty(FORMAT) Map<String, Object> format) {
        this.sampleGenotype = new GenotypeModel(sampleGenotype);
        this.format = new AttributeMap(format);
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(SAMPLE_NAME)
    public String getName() {
        return name;
    }
    
    @BsonProperty(SAMPLE_GENOTYPE)
    @JsonProperty(SAMPLE_GENOTYPE)
    public Map<String,Object> getSampleGenotypeAsMap() {
        return sampleGenotype;
    }

    @JsonIgnore
    @BsonIgnore
    public GenotypeModel getSampleGenotype() {
        return sampleGenotype;
    }

    @BsonProperty(FORMAT)
    @JsonProperty(FORMAT)
    public Map<String,Object> getFormatAsMap() {
        return format;
    }
 
    @JsonIgnore
    @BsonIgnore
    public AttributeMap getFormat() {
        return format;
    }
    
    public Object getAttribute(String attributePath){
        String[] splittedPath = attributePath.split("\\.");
        String firstElement = splittedPath[0];
        String otherElements = (attributePath.contains(".")) ? attributePath.substring(attributePath.indexOf(".")+1) : "";

        switch(firstElement) {
            case SAMPLE_NAME:
                return name;
            case SAMPLE_GENOTYPE:
                return sampleGenotype.getAttribute(otherElements);
            case FORMAT:
                return format.get(otherElements);    
            default:
                return null;
        }
    }
    
    
}
