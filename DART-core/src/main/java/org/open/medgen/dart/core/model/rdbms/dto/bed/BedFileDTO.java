/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.dto.bed;

import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.log.Job;
import org.open.medgen.dart.core.model.rdbms.entity.log.JobMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dbarreca
 */
public class BedFileDTO {
    
    public final static String STATUS = "STATUS";
    public final static String FILE_NAME = "FILE_NAME";
    public final static String DB_ID = "DB_ID";
    public final static String MD5 = "MD5";    
    public final static String MESSAGES = "MESSAGES";
    public final static String DATE = "DATE";
    public final static String GENOME = "GENOME";
    
    @JsonProperty(DB_ID)
    private Integer sqlId;
    
    @JsonProperty(FILE_NAME)
    private String fileName;
    
    @JsonProperty(DATE)
    private Date uploadDate;
    
    @JsonProperty(STATUS)
    private MongoFileStatus status;

    @JsonProperty(GENOME)
    private String genome;
    
    @JsonProperty(MD5)
    private String md5;
    
    @JsonProperty(MESSAGES)
    private List<String> messages = null;

    public BedFileDTO() {
    }

    public BedFileDTO(BedFile entity, boolean includeMessages) {
        this.sqlId = entity.getBedId();
        this.fileName = entity.getFileName();
        this.status = entity.getStatus();
        this.md5 = entity.getMd5();
        this.genome=entity.getGenome();
        
        Job job = entity.getLatestJob();
        while(job!=null && job.getType()!=Job.JobType.BED_UPLOAD){
            job = job.getPreviousJob();
        }
        this.uploadDate = job.getCreationDate();
       
        if (includeMessages){
            messages = new LinkedList();
            for (JobMessage message: entity.getLatestJob().getMessages()){
                messages.add(message.getMessageType().toString() + " - "+message.getMessage());
            }
        }
    }

    public Integer getSqlId() {
        return sqlId;
    }

    public void setSqlId(Integer sqlId) {
        this.sqlId = sqlId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MongoFileStatus getStatus() {
        return status;
    }

    public void setStatus(MongoFileStatus status) {
        this.status = status;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public String getGenome() {
        return genome;
    }

    public void setGenome(String genome) {
        this.genome = genome;
    }
}
