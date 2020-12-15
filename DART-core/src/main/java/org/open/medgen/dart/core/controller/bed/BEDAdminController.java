/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.bed;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.permission.EntityNotFoundException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.model.rdbms.dto.bed.BedFileDTO;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.log.JobMessage;
import org.open.medgen.dart.core.service.mongo.service.AsyncBEDService;
import org.open.medgen.dart.core.service.rdbms.service.DbBEDService;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.bson.types.ObjectId;

/**
 *
 * @author dbarreca
 */
@Stateless
@Default
public class BEDAdminController {

    private static final Logger LOG = Logger.getLogger(BEDAdminController.class.getName());
        
    @Inject AsyncBEDService mongoBedServiceAsync;
    @Inject DbBEDService dbBedService;
    @Inject PermissionController permissionController;
    
    @Transactional
    public void saveBEDFile(InputStream is, String bedFileName, String user, String userGroup, String genome) throws BEDLoadingException,AuthorizationException{
        if (!permissionController.isGroupAdmin(user, userGroup)) throw new AuthorizationException();        
        
        ParsedBed parsed = BEDParser.parse(is, bedFileName, genome);
        
        BedFile theFile = permissionController.getBedWithMD5ForInsert(parsed.getMd5());        
        theFile = dbBedService.registerBED(bedFileName, parsed.getMd5(), theFile, parsed.getMongoID(), user, userGroup, BedGenomeVersion.getCorrectedVersion(genome));
        
        Integer fileId = theFile.getBedId();
        mongoBedServiceAsync.saveBEDEntry(parsed.getEntries(),
                () -> this.finishInsertion(fileId),
                (Throwable t) -> this.finishInsertionExceptionally(fileId, t)
        );

    }
    
   
    @Transactional
    public void removeBEDFile(Integer bedFileId, String user, String userGroup) throws BEDLoadingException, AuthorizationException{
        
        
        BedFile theFile;
        try{
            theFile = permissionController.getBedFileForDeletion(bedFileId, user, userGroup);
        } catch (EntityNotFoundException ex) {
            throw new BEDLoadingException("BED was not found");
        }
        
        dbBedService.startDeleteJob(theFile, user);        
        
        mongoBedServiceAsync.removeBEDEntries(new ObjectId(theFile.getMongoId()),
            () -> this.finishDeletion(bedFileId),
            (Throwable t) ->this.finishDeletionExceptionally(bedFileId, t)
        );
    }
    
    @Transactional
    public List<BedFileDTO> getAll(String user, String userGroup) throws AuthorizationException {
        return permissionController.getAllBEDsForAdmin(user, userGroup);
    }
    
    private void finishInsertion(Integer theFileId){
        dbBedService.changeBEDStatus(theFileId, MongoFileStatus.AVAILABLE);
    }
    
    private void finishInsertionExceptionally(Integer theFileId, Throwable t){
        BedFile theFile = this.dbBedService.retrieveBEDOrNull(theFileId);
        dbBedService.addMessageToBED(theFile, "ERROR WHILE INSERTING: "+t.getMessage(), JobMessage.Type.ERROR);
        dbBedService.changeBEDStatus(theFileId, MongoFileStatus.ERROR);
    }
    
    private void finishDeletion(Integer theFileId){
        dbBedService.changeBEDStatus(theFileId, MongoFileStatus.NOT_AVAILABLE);
    }
    
    private void finishDeletionExceptionally(Integer theFileId, Throwable t){
        BedFile theFile = this.dbBedService.retrieveBEDOrNull(theFileId);
        dbBedService.addMessageToBED(theFile, "ERROR WHILE DELETING: "+t.getMessage(), JobMessage.Type.ERROR);
        dbBedService.changeBEDStatus(theFileId, MongoFileStatus.ERROR);
    }
    
}
