/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.service;

import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BEDFilePermission;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.log.Job;
import org.open.medgen.dart.core.model.rdbms.entity.log.JobMessage;
import org.open.medgen.dart.core.service.rdbms.dao.BedDAO;
import org.open.medgen.dart.core.service.rdbms.dao.JobDAO;
import org.open.medgen.dart.core.service.rdbms.dao.UserDAO;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.bson.types.ObjectId;

/**
 *
 * @author dbarreca
 */
@Stateless
public class DbBEDService {

    @Inject
    BedDAO bedDAO;
    
    @Inject
    JobDAO jobDAO;
    
    @Inject
    UserDAO userDAO;

    public DbBEDService() {
    }

    public DbBEDService(BedDAO bedDAO) {
        this.bedDAO = bedDAO;
    }

    public BedFile retrieveBEDOrNull(String bedMD5) {
        return bedDAO.getFileByMD5OrNull(bedMD5);

    }
    
    public BedFile retrieveBEDOrNull(Integer bedFileId) {
        return bedDAO.getFileById(bedFileId);
    }

    public BedFile registerBED(String fileName, String md5, BedFile theFile, ObjectId mongoID, String userName, String groupName, String genome){
        User user = userDAO.getUserOrNull(userName);
        UserGroup userGroup = userDAO.getUserGroupOrNull(groupName);
        
        Job job = new Job(Job.JobType.BED_UPLOAD, user);
        jobDAO.persistJob(job);
        
        boolean insert = false;
        
        if (theFile==null){
            insert = true;
            theFile = new BedFile();
        }else{
            job.setPreviousJob(theFile.getLatestJob());
        }
                    
        theFile.setFileName(fileName);
        theFile.setMongoId(mongoID.toHexString());
        theFile.setStatus(MongoFileStatus.LOADING);
        theFile.setMd5(md5);
        theFile.setLatestJob(job); 
        theFile.setGenome(genome);
       
        if (insert){        
            bedDAO.save(theFile);
        }
        
        addUserGroupToBED(theFile, userGroup, true);

        return theFile;
    }
    
    public void addUserGroupToBED(BedFile theFile, UserGroup userGroup, Boolean isOwner){
        
        boolean found = false;
        for (BEDFilePermission permission: theFile.getPermissions()){
            if (permission.getKey().getGroup().equals(userGroup)){
                found = true;
                permission.setIsOwner(isOwner);
            }else if(isOwner){
                permission.setIsOwner(false);
            }
        }
        
        if (!found){
            BEDFilePermission permission = bedDAO.getOrCreatePermission(theFile, userGroup);
            permission.setIsOwner(isOwner);
        }
    }
    
    public void startDeleteJob(BedFile theBed, String login) {
        User user = userDAO.getUserOrNull(login);
        Job job = new Job(Job.JobType.BED_DELETE, user);
        job.setPreviousJob(theBed.getLatestJob());
        jobDAO.persistJob(job);
        theBed.setLatestJob(job);
        this.changeBEDStatus(theBed, MongoFileStatus.DELETING);
    }

    public void changeBEDStatus(BedFile theBed, MongoFileStatus bedStatus) {
        theBed.setStatus(bedStatus);
        if (MongoFileStatus.NOT_AVAILABLE.equals(bedStatus)) {
            theBed.setMongoId("");
        }
        bedDAO.persist(theBed);
    }

    public void changeBEDStatus(Integer theBedId, MongoFileStatus bedStatus) {
        BedFile theBED = this.retrieveBEDOrNull(theBedId);
        this.changeBEDStatus(theBED , bedStatus);
    }

    public void addMessageToBED(BedFile theBed, String message, JobMessage.Type type) {
        JobMessage theMessage = new JobMessage(type, message);
        theMessage.setJob(theBed.getLatestJob());

        jobDAO.persistMessage(theMessage);
    }

}
