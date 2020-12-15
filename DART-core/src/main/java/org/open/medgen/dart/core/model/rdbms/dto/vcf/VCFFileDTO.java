/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.dto.vcf;


import org.open.medgen.dart.core.controller.utils.VCFFieldsUtils;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.log.JobMessage;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFile;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.bson.types.ObjectId;


/**
 *
 * @author dbarreca
 */
@JsonInclude(Include.NON_NULL)
public class VCFFileDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    
    public final static String STATUS = "STATUS";
    public final static String USER_GROUPS = "GROUPS";
    public final static String DATE = "CREATION_DATE";
    public final static String REF_ID = "REF_ID";
    public final static String FILE_NAME = "VCF_FILE";
    //public final static String TYPE = "TYPE";
    public final static String SAMPLES = "SAMPLES";
    public final static String INFO_FIELDS = "FIELDS";
    public final static String PIPELINE = "PIPELINE";
    public final static String DB_ID = "DB_ID";
    public final static String MD5 = "MD5";    
    
    public final static String PIPELINE_DESCRIPTION = "PIPELINE_DESCRIPTION";
    public final static String VCF_TYPE = "VCF_TYPE";
    public final static String REF_GENOME = "REF_GENOME";
    public final static String MESSAGES = "MESSAGES";
     
    private ObjectId mongoId = null;    

    @JsonProperty(DB_ID)
    private Integer sqlId;
    
    @JsonProperty(FILE_NAME)
    private String vcfFileName;
    
    @JsonProperty(DATE)
    private Date creationDate;
    
    @JsonProperty(STATUS)
    private MongoFileStatus status;   
    
    @JsonProperty(MD5)
    private String md5;
    
    @JsonProperty(VCF_TYPE)
    private VCFType type;
    
    @JsonProperty(PIPELINE_DESCRIPTION)
    private String pipelineDetails;
    
    @JsonProperty(REF_GENOME)
    private String refGenome;    
    
    @JsonProperty(SAMPLES)
    private List<SampleDTO> samples = new LinkedList() ;      
    
    @JsonProperty(INFO_FIELDS)
    private List<VCFInfoDTO> vcfFields = new LinkedList();
    
    @JsonProperty(MESSAGES)
    private List<String> messages = null;
    

    public VCFFileDTO(String vcfFileName, String pipelineDetails, String refGenome, VCFType type) {
        this.vcfFileName = vcfFileName;
        this.pipelineDetails = pipelineDetails;
        this.refGenome = refGenome;
        this.type = type;
    }
    
    public VCFFileDTO(VCFFile entity, boolean includeMessages) {
        this.sqlId = entity.getVcfId();
        if (entity.getMongoId()!=null && !entity.getMongoId().isEmpty()){
            this.mongoId = new ObjectId(entity.getMongoId());
        }
        this.vcfFileName = entity.getFileName();
        this.status = entity.getStatus();
        this.md5 = entity.getMd5();
        this.creationDate = entity.getCreationDate();
        
        for (VCFSample sampleEntity: entity.getSampleList()){
            this.addSample(new SampleDTO(sampleEntity));
        }
        this.vcfFields =VCFFieldsUtils.getInfoFields(entity.getFieldList(), entity.getContigs(), entity.getFilter());
        
        this.type = entity.getType();
        this.refGenome = entity.getRefGenome();
        this.pipelineDetails = entity.getPipelineDetails();
        if (includeMessages){
            messages = new LinkedList();
            for (JobMessage message: entity.getLatestJob().getMessages()){
                messages.add(message.getMessageType().toString() + " - "+message.getMessage());
            }
        }
    }
    
    
    
    public VCFFile toEntity(){
        VCFFile theEntity = new VCFFile();
        theEntity.setFileName(vcfFileName);
        theEntity.setStatus(MongoFileStatus.LOADING);
        theEntity.setMongoId(mongoId);
        theEntity.setCreationDate(this.creationDate);
        theEntity.setMd5(this.md5);
        theEntity.setPipelineDetails(this.pipelineDetails);
        theEntity.setRefGenome(this.refGenome);
        theEntity.setType(this.type);
        
        for (VCFInfoDTO infoField: this.vcfFields){
            if (infoField.getFieldPath().equals(VariantModel.CHROM)){
                theEntity.setContigs(VCFFieldsUtils.setToString(infoField.getPossibleValues()));
            } else if (infoField.getFieldPath().equals(VariantModel.FILTER)){
                theEntity.setFilter(VCFFieldsUtils.setToString(infoField.getPossibleValues()));
            }
        }
        
        return theEntity;
    }
    
    @JsonProperty(REF_ID)
    public String  getMongoIdString(){
        if (mongoId!=null){
            return mongoId.toHexString();
        }else{
            return "";
        }
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @JsonIgnore
    public ObjectId getMongoId() {
        return mongoId;
    }

    public void setMongoId(ObjectId mongoId) {
        this.mongoId = mongoId;
    }

    public MongoFileStatus getStatus() {
        return status;
    }

    public void setStatus(MongoFileStatus status) {
        this.status = status;
    }

    public String getVcfFileName() {
        return vcfFileName;
    }

    public void setVcfFileName(String vcfFileName) {
        this.vcfFileName = vcfFileName;
    }


    public List<VCFInfoDTO> getVcfFields() {
        return vcfFields;
    }
    
    public void addField(VCFInfoDTO field){
        this.vcfFields.add(field);
    }
    
    public void addFields(List<VCFInfoDTO> field){
        this.vcfFields.addAll(field);
    }
        

    public List<SampleDTO> getSamples() {
        return samples;
    }
    
    public void addSample(SampleDTO sample){
        this.samples.add(sample);
    }
    
    public Integer getSqlId() {
        return sqlId;
    }

    public void setSqlId(Integer sqlId) {
        this.sqlId = sqlId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }    

    public VCFType getType() {
        return type;
    }

    public void setType(VCFType type) {
        this.type = type;
    }

    public String getRefGenome() {
        return refGenome;
    }
}

