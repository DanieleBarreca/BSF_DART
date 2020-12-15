/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.vcf;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.permission.EntityNotFoundException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.controller.permission.PermissionInsertController;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.SampleDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.log.JobMessage;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFile;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.cache.CacheService;
import org.open.medgen.dart.core.service.mongo.service.AsyncVariantService;
import org.open.medgen.dart.core.service.mongo.service.SyncCoverageService;
import org.open.medgen.dart.core.service.rdbms.service.DbVCFService;
import java.util.List;
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
public class VCFAdminController {

    @Inject
    private AsyncVariantService variantServiceAsync;

    @Inject
    private SyncCoverageService syncCoverageService;
    
    @Inject
    private DbVCFService sqlVCFService;
     
    @Inject
    private CacheService cacheService;  
    
    @Inject
    private PermissionController permissionAdminController;
    
    @Transactional(rollbackOn=VCFLoadingException.class)
    public void removeVCF(Integer fileId, String user, String userGroup) throws VCFLoadingException, AuthorizationException {
        VCFFile theVCF;
        try {
            theVCF = permissionAdminController.getVcfForDeletion(fileId, user, userGroup);
        } catch (EntityNotFoundException ex) {
            throw new VCFLoadingException("VCF was not found");
        }

        sqlVCFService.startDeleteJob(theVCF, user);
        cacheService.evictVCF(fileId);

        ObjectId id = new ObjectId(theVCF.getMongoId());

        for (VCFSample sample: theVCF.getSampleList()){
            if (sample.getCoverageMongoId()!=null) {
                syncCoverageService.removeCoverageEntries(new ObjectId(sample.getCoverageMongoId()));
            }
        }

        variantServiceAsync.removeVCFVariants(
                id,
                () -> {
                    this.finishDeletion(theVCF.getVcfId());
                },
                (Throwable t) -> {
                    this.finishDeletionExceptionally(theVCF.getVcfId(),t);
                }
        );
    }

    
    private void finishDeletion(Integer theFileId){
        sqlVCFService.changeVCFStatus(theFileId, MongoFileStatus.NOT_AVAILABLE);
    }
    
    private void finishDeletionExceptionally(Integer theFileId, Throwable t){
        VCFFile theFile = this.sqlVCFService.retrieveVCFEntity(theFileId);
        this.sqlVCFService.addMessageToVCF(theFile, "ERROR WHILE DELETING: "+t.getMessage(), JobMessage.Type.ERROR);
        sqlVCFService.changeVCFStatus(theFileId, MongoFileStatus.ERROR);
    }
    
    @Transactional
    public List<VCFFileDTO> getAll(String user, String userGroup) throws AuthorizationException {
        return permissionAdminController.getAllVCFsForAdmin(user, userGroup);
    }
    
}
