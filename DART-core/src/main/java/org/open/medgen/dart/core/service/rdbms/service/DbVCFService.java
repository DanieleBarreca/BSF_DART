
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.service;

import org.open.medgen.dart.core.controller.vcf.VCFLoadingException;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.SampleDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFile;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.log.Job;
import org.open.medgen.dart.core.model.rdbms.entity.log.JobMessage;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFField;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFilePermission;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.rdbms.dao.JobDAO;
import org.open.medgen.dart.core.service.rdbms.dao.UserDAO;
import org.open.medgen.dart.core.service.rdbms.dao.VcfDAO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.bson.types.ObjectId;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;

/**
 *
 * @author dbarreca
 */
@Stateless
public class DbVCFService {
    @Inject
    VcfDAO vcfDAO;

    @Inject
    UserDAO userDAO;
    
    @Inject
    JobDAO jobDAO;
    
    private final static String[] INDEX_FILES_SUFFIXES={"tbi","bai"};

    public DbVCFService() {
    }

    public DbVCFService(VcfDAO vcfDAO) {
        this.vcfDAO = vcfDAO;
    }

    public VCFFileDTO retrieveVCFOrNull(String md5) {
        VCFFile fileEntity = vcfDAO.getFileByMD5OrNull(md5);
        if (fileEntity == null) {
            return null;
        }

        return new VCFFileDTO(fileEntity,false);
    }
    
    
    public VCFFileDTO retrieveVCFOrNull(Integer fileId) {
        VCFFile fileEntity = retrieveVCFEntity(fileId);
        if (fileEntity == null) {
            return null;
        }

        return new VCFFileDTO(fileEntity,false);
    }
    
    public VCFFile retrieveVCFEntity(Integer fileId) {
        return vcfDAO.getFileById(fileId);
        
    }
    
    public VCFFile retrieveVCFEntity(String md5) {
        return vcfDAO.getFileByMD5OrNull(md5);
    }
    
    public VCFFile retrieveVCFEntity(ObjectId objectId) {
        return vcfDAO.getFileByMongoId(objectId);
    }

  
    public Integer registerVCF(VCFFileDTO toInsert ,VCFFile existing, User user, UserGroup userGroup) throws VCFLoadingException{

        
        Job job = new Job(Job.JobType.VCF_UPLOAD, user);
        jobDAO.persistJob(job);
        
        if (existing == null) {
            
            List<VCFField> fields = new LinkedList();
            for (VCFInfoDTO field : toInsert.getVcfFields()) {
                VCFField fieldEntity = field.toEntity();

                fields.add(vcfDAO.saveOrGetField(fieldEntity));
            }

            existing = toInsert.toEntity();
            existing.setLatestJob(job);
            existing.setFieldList(fields);
            
            vcfDAO.save(existing);
                       
            for (SampleDTO sample : toInsert.getSamples()) {
                try {
                    VCFSample vcfSampleEntity = sample.toEntity(sample.getSampleName(), existing);
                    vcfDAO.saveVCFSample(vcfSampleEntity);
                }catch (URISyntaxException e){
                    throw new VCFLoadingException(String.format("Malformed URI for sample %s",sample.getSampleName()),e);
                }
            }
            
        } else {
            existing.setStatus(MongoFileStatus.LOADING);            
            existing.setMongoId(toInsert.getMongoId());
            existing.setType(toInsert.getType());
            job.setPreviousJob(existing.getLatestJob());
            existing.setLatestJob(job);

        }
        
        addUserGroupToVCF(existing, userGroup, true);
        
        return existing.getVcfId();
    }
    
    public void addUserGroupToVCF(VCFFile theFile, UserGroup userGroup, Boolean isOwner){
        
        boolean found = false;
        for (VCFFilePermission permission: theFile.getPermissions()){
            if (permission.getKey().getGroup().equals(userGroup)){
                found = true;
                permission.setIsOwner(isOwner);
            }else if(isOwner){
                permission.setIsOwner(false);
            }
        }
        
        if (!found){
            VCFFilePermission permission = vcfDAO.getOrCreatePermission(theFile, userGroup);
            permission.setIsOwner(isOwner);
        }
    }
    
    public void startDeleteJob(VCFFile theVcf, String login){
        User user = userDAO.getUserOrNull(login);
        Job job = new Job(Job.JobType.VCF_DELETE, user);
        job.setPreviousJob(theVcf.getLatestJob());
        jobDAO.persistJob(job);
        theVcf.setLatestJob(job);
        this.changeVCFStatus(theVcf, MongoFileStatus.DELETING);
    }
    
    public void changeVCFStatus(VCFFile theVCF, MongoFileStatus vcfStatus) {        
        theVCF.setStatus(vcfStatus);
        if (MongoFileStatus.NOT_AVAILABLE.equals(vcfStatus)) {
            theVCF.setMongoId("");
        }
        vcfDAO.persist(theVCF);
        
    }
    
    public void changeVCFStatus(Integer theVcfId, MongoFileStatus vcfStatus) {        
        VCFFile theVCF = this.retrieveVCFEntity(theVcfId);
        this.changeVCFStatus(theVCF, vcfStatus);        
    }
    
    public void addMessageToVCF(VCFFile theVcf, String message, JobMessage.Type type){
        JobMessage theMessage = new JobMessage(type,message);
        theMessage.setJob(theVcf.getLatestJob());
        
        jobDAO.persistMessage(theMessage);
    }

    public VCFSample retrieveVCFSampleEntity(Integer dbId) {
        return vcfDAO.getVCFSampleById(dbId);
    }


    public List<VCFSample> getSamplesForFileURI(String fileURI) throws RDBMSServiceException{
        try {
            
            for (String suffix: INDEX_FILES_SUFFIXES){
                if (fileURI.endsWith(suffix)){
                    fileURI=fileURI.replace(String.format(".%s",suffix),"");
                }
            }
            
            while(fileURI.startsWith("/")){
                fileURI=fileURI.substring(1);
            }
            
            fileURI=new URI(String.format("local://%s",fileURI)).normalize().toString();
            return vcfDAO.getVCFSampleByFileURI(fileURI);
            
        } catch (URISyntaxException e){
            throw new RDBMSServiceException(String.format("Malformed URI: %s",fileURI),e);
        }
    }
    

}
