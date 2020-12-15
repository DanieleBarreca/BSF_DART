/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.NumericField;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author dbarreca
 */
@Embeddable
public class FullQuery implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final static String VCF_REF_ID = "VCF_REF_ID";
    private final static String SAMPLE_REF_ID = "SAMPLE_REF_ID";
    private final static String VCF_NAME = "VCF_NAME";
    private final static String SAMPLE_NAME = "SAMPLE_NAME";
    private final static String PANEL = "PANEL";
    private final static String RELATED_SAMPLES = "RELATED_SAMPLES";
    private final static String FILTER = "FILTER";
    
    @Field
    @NumericField
    @JsonProperty(VCF_REF_ID)
    private Integer vcfFileId;
    
    @Transient
    @JsonProperty(VCF_NAME)
    private String vcfFileName;

    @Transient
    @JsonProperty(SAMPLE_NAME)
    private String sample;

    @Transient
    @JsonProperty(SAMPLE_REF_ID)
    private Integer sampleRefId;

    @Transient
    @JsonProperty(PANEL)
    private QueryPanel panel = new QueryPanel();

    @Transient
    @JsonProperty(RELATED_SAMPLES)
    private List<RelatedSampleInfo> relatedSamples = new LinkedList<>();
   
    @Transient
    @JsonProperty(FILTER)
    private QueryFilter queryFilter = null;

    public FullQuery() {
    }

    public Integer getVcfFileId() {
        return vcfFileId;
    }

    public String getSample() {
        return sample;
    }

    public String getVcfFileName() {
        return vcfFileName;
    }

    public QueryPanel getPanel() {
        return panel;
    }

    public List<RelatedSampleInfo> getRelatedSamples() {
        if (relatedSamples==null) {
            return new LinkedList<>();
        }
        return relatedSamples;
    }

    public QueryFilter getQueryFilter() {
        return queryFilter;
    }

    public void setVcfFileId(Integer vcfFileId) {
        this.vcfFileId = vcfFileId;
    }

    public void setVcfFileName(String vcfFileName) {
        this.vcfFileName = vcfFileName;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public void setPanel(QueryPanel panel) {
        this.panel = panel;
    }

    public void setRelatedSamples(List<RelatedSampleInfo> relatedSamples) {
        this.relatedSamples = relatedSamples;
    }

    public void setQueryFilter(QueryFilter queryFilter) {
        this.queryFilter = queryFilter;
    }

    public Integer getSampleRefId() {
        return sampleRefId;
    }

    public void setSampleRefId(Integer sampleRefId) {
        this.sampleRefId = sampleRefId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.vcfFileId);
        hash = 13 * hash + Objects.hashCode(this.sample);
        hash = 13 * hash + Objects.hashCode(this.panel);
        hash = 13 * hash + Objects.hashCode(this.queryFilter);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FullQuery other = (FullQuery) obj;
        if (!Objects.equals(this.vcfFileId, other.vcfFileId)) {
            return false;
        }
        if (!Objects.equals(this.sample, other.sample)) {
            return false;
        }
        if (!Objects.equals(this.panel, other.panel)) {
            return false;
        }
        if (!Objects.equals(this.queryFilter, other.queryFilter)) {
            return false;
        }
        
        return true;
    }

    
 
}
