/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.vcf;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.permission.EntityNotFoundException;
import org.open.medgen.dart.core.controller.permission.PermissionInsertController;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.SampleDTO;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.log.JobMessage;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFile;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.mongo.service.SyncCoverageService;
import org.open.medgen.dart.core.service.mongo.service.SyncVariantService;
import org.open.medgen.dart.core.service.rdbms.service.DbVCFService;
import java.util.List; 
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.bson.types.ObjectId;
import org.open.medgen.dart.core.DartCoreConfig;


/**
 *
 * @author dbarreca
 */
@Stateless
@Remote(VCFInsertController.class)
@RolesAllowed(DartCoreConfig.VCF_INSERT_ALLOWED_ROLES)
@SecurityDomain(DartCoreConfig.VCF_INSERT_SECURITY_DOMAIN)
public class VCFInsertControllerBean implements VCFInsertController{

    public final static Integer checksumByteLimit = 1024*1024;

    @Inject
    private SyncVariantService variantService;

    @Inject
    private SyncCoverageService coverageService;
   
    @Inject
    private DbVCFService sqlVCFService;
    
    @Inject
    private PermissionInsertController permissionController;
    
    @Resource SessionContext ejbContext;  
    
    @Transactional(rollbackOn=VCFLoadingException.class)
    public Integer saveHeader(VCFFileDTO header, String userGroup) throws VCFLoadingException, AuthorizationException{    
        
        String user = ejbContext.getCallerPrincipal().getName();
        User userEntity = permissionController.getUser(user);
        UserGroup  userGroupEntity = permissionController.getUserGroup(userEntity, userGroup);

        for (SampleDTO sampleToInsert: header.getSamples()){
            checkMD5(sampleToInsert.getAlignmentUrl(), sampleToInsert.getAlignmentUrlMD5());
            checkMD5(sampleToInsert.getVarianttUrl(), sampleToInsert.getVarianttUrlMD5());
            checkMD5(sampleToInsert.getCoverageTrackUrlMD5(), sampleToInsert.getCoverageTrackUrlMD5());
        }
        
        if (header.getMongoId() == null) throw new VCFLoadingException("VCF has empty ID ");
        VCFFile theFile = permissionController.getVcfWithMD5ForInsert(header.getMd5(), header.getMongoId());
     
        return sqlVCFService.registerVCF(header,theFile, userEntity, userGroupEntity);
    }
    
    private void checkMD5(String url, String md5) throws VCFLoadingException{
        if (url!=null && url.startsWith("local://") && !permissionController.checksumMatches(url.replace("local://",""), md5, checksumByteLimit )){
            throw new VCFLoadingException(String.format("Checksum %s does not match for file %s",md5,url));
        }
    }
    
    @Override
    public void insertVariants(List<VariantModel> variants) throws VCFLoadingException, AuthorizationException, EntityNotFoundException{
        String user = ejbContext.getCallerPrincipal().getName();
        VCFFile vcf = null;
        
        try {
            if (variants != null && !variants.isEmpty()) {
                ObjectId mongoID = null;
                for (VariantModel variant : variants) {
                    if (mongoID == null) {
                        vcf = permissionController.getVCFToInsertVariants(variant.getVcfId(), user);
                        mongoID = variant.getVcfId();
                    } else {
                        if (!mongoID.equals(variant.getVcfId())) {
                            throw new VCFLoadingException("Inconsistent VCF ID across variants");
                        }
                    }
                }
                variantService.saveVariants(variants);
            }
        }catch(VCFLoadingException ex){
             if (vcf!=null) {
               this.sqlVCFService.addMessageToVCF(vcf, ex.getMessage(), JobMessage.Type.ERROR);
            }
            throw ex;
        }catch(Error ex){
            if (vcf!=null) {
               this.sqlVCFService.addMessageToVCF(vcf, "["+ex.getClass().getCanonicalName()+"]:"+ex.getMessage(), JobMessage.Type.ERROR);
            }
            throw new VCFLoadingException(ex);
        }    
    }

    @Override
    public void addCoverageRegions(List<CoverageEntry> coverageEntries, String sampleName, Integer theFileId) throws VCFLoadingException, AuthorizationException, EntityNotFoundException{
        String user = ejbContext.getCallerPrincipal().getName();
        VCFFile theVCF = permissionController.getVcfToInsertCoverage(theFileId, user);
        String coverageId = null;
        for (VCFSample sample: theVCF.getSampleList()){
            if (sample.getSampleName().equals(sampleName)){
                coverageId = sample.getCoverageMongoId();
            }
        }
        if (coverageId == null){
            throw new VCFLoadingException("Coverage Mongo ID not found for vcf "+theFileId+" and sample "+sampleName);
        }

        for (CoverageEntry entry: coverageEntries){
            entry.setCoverageFile(new ObjectId(coverageId));
        }

        coverageService.saveCoverageEntries(coverageEntries);

    }
     
    @Transactional()
    public void finishLoading(Integer theFileId) throws VCFLoadingException, AuthorizationException, EntityNotFoundException{
        String user = ejbContext.getCallerPrincipal().getName();
        VCFFile theVCF = permissionController.getVcfToFinishLoading(theFileId, user);
        
        sqlVCFService.changeVCFStatus(theVCF, MongoFileStatus.AVAILABLE);
    }
    
    @Transactional(rollbackOn=VCFLoadingException.class)
    public void finishLoadingeExceptionally(Integer theFileId, String message) throws VCFLoadingException, AuthorizationException, EntityNotFoundException {
        String user = ejbContext.getCallerPrincipal().getName();
        VCFFile theVCF = permissionController.getVcfToFinishLoading(theFileId, user);
        
        this.sqlVCFService.addMessageToVCF(theVCF, "INTERRUPTED BY CLIENT: "+message, JobMessage.Type.ERROR);
        sqlVCFService.changeVCFStatus(theVCF, MongoFileStatus.ERROR);
    }
    
    @Override
    public String ping(String testString) {
        return "PONG: "+testString;
    }
}
