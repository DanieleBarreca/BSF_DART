/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.dto.vcf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFile;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author dbarreca
 */
public class SampleDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    public final static String REF_ID = "REF_ID";
    public final static String SAMPLE_NAME = "SAMPLE_NAME";
    public final static String ALIGNMENT_URL = "ALIGNMENT_URL";
    public final static String VARIANT_URL = "VARIANT_URL";
    public final static String COVERAGE_TRACK_URL = "COVERAGE_TRACK_URL";
    public final static String COVERAGE_MONGO_ID = "COVERAGE_MONGO_ID";
    
    @JsonProperty(REF_ID)
    private Integer dbId;
    
    @JsonProperty(SAMPLE_NAME)
    private String sampleName;
    
    @JsonProperty(ALIGNMENT_URL)
    private String alignmentUrl;
    
    @JsonProperty(VARIANT_URL)
    private String varianttUrl;

    @JsonProperty(COVERAGE_TRACK_URL)
    private String coverageTrackUrl;

    @JsonProperty(COVERAGE_MONGO_ID)
    private String coverageMongoId;
    
    @JsonIgnore
    private String alignmentUrlMD5;

    @JsonIgnore
    private String coverageTrackUrlMD5;

    @JsonIgnore
    private String varianttUrlMD5;

    public SampleDTO() {};
    
    public SampleDTO(VCFSample entity) {
        this.dbId = entity.getSampleId();
        this.sampleName = entity.getSampleName();
        this.alignmentUrl = entity.getBamUrl();
        this.varianttUrl = entity.getSampleVcfUrl();
        this.coverageTrackUrl = entity.getCoverageTrackUrl();
        this.coverageMongoId = entity.getCoverageMongoId();
    };

    public SampleDTO(String sampleName, String alignmentUrl, String alignmentUrlMD5, String varianttUrl, String varianttUrlMD5, String coverageTrackUrl, String coverageTrackUrlMD5, String coverageMongoId) {
        this.sampleName = sampleName;
        
        this.alignmentUrl = alignmentUrl;
        this.alignmentUrlMD5 = alignmentUrlMD5;
        
        this.varianttUrl = varianttUrl;
        this.varianttUrlMD5 = varianttUrlMD5;
        
        this.coverageTrackUrl = coverageTrackUrl;
        this.coverageTrackUrlMD5 = coverageTrackUrlMD5;
        
        this.coverageMongoId = coverageMongoId;
    }
    
    public VCFSample toEntity(String sampleName, VCFFile vcf) throws URISyntaxException {
        VCFSample theSampleEntity = new VCFSample();
        theSampleEntity.setVcf(vcf);
        theSampleEntity.setSampleName(sampleName);
        theSampleEntity.setSampleVcfUrl(this.varianttUrl!= null? new URI(this.varianttUrl).normalize().toString() : null);
        theSampleEntity.setBamUrl(this.alignmentUrl!=null? new URI(this.alignmentUrl).normalize().toString(): null);
        theSampleEntity.setCoverageTrackUrl(this.coverageTrackUrl!=null? new URI(this.coverageTrackUrl).normalize().toString(): null);
        theSampleEntity.setCoverageMongoId(this.coverageMongoId);
        
        return theSampleEntity;
    }
    

    public String getSampleName() {
        return sampleName;
    }
        
    public String getAlignmentUrl() {
        return alignmentUrl;
    }
   
    public String getVarianttUrl() {
        return varianttUrl;
    }

    public String getCoverageTrackUrl() {
        return coverageTrackUrl;
    }

    public String getCoverageMongoId() {
        return coverageMongoId;
    }

    public Integer getDbId() {
        return dbId;
    }

    public String getAlignmentUrlMD5() {
        return alignmentUrlMD5;
    }

    public String getCoverageTrackUrlMD5() {
        return coverageTrackUrlMD5;
    }

    public String getVarianttUrlMD5() {
        return varianttUrlMD5;
    }
}
