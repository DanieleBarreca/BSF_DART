/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.permission;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.utils.ChecksumException;
import org.open.medgen.dart.core.controller.utils.FileDigester;
import org.open.medgen.dart.core.controller.vcf.VCFLoadingException;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFile;
import org.open.medgen.dart.core.service.rdbms.service.DbVCFService;
import org.open.medgen.dart.core.service.rdbms.service.UserService;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.open.medgen.dart.core.DartCoreConfig;

import java.io.File;
import java.util.logging.Logger;

/**
 *
 * @author dbarreca
 */
@RolesAllowed(DartCoreConfig.VCF_INSERT_ALLOWED_ROLES)
@SecurityDomain(DartCoreConfig.VCF_INSERT_SECURITY_DOMAIN)
@Stateless
@Default
public class PermissionInsertController {
    
    private final static Logger LOGGER= Logger.getLogger(PermissionController.class.getName());
    
    @Inject private UserService userService;
    @Inject private DbVCFService vcfService;
    
    public User getUser(String username) throws AuthorizationException {
        User theUser = userService.getUserOrNull(username);
        if (theUser == null) throw new AuthorizationException();
        
        return theUser;
    }
    
    public UserGroup getUserGroup(User theUser, String group) throws AuthorizationException{
        
        UserRole theRole = null;
        if (group==null){
            if (theUser.getUserRoles().size()!=1)
                throw new AuthorizationException("The user has "+theUser.getUserRoles().size()+" groups assigned and no group was specified");
            theRole = theUser.getUserRoles().get(0);
        }else{
            for (UserRole role: theUser.getUserRoles()){
                if (role.getGroup().getGroup().equals(group)){
                    theRole = role;
                    break;
                }
            }
        }
        
        if (theRole == null) throw new AuthorizationException("The user is not part of the specified group");
        if (!theRole.getCanUploadVCF()) throw new AuthorizationException("The user is not allowed to upload VCFs");
        
        
        return theRole.getGroup();
    }   
   
    
    public VCFFile getVcfWithMD5ForInsert(String vcfFileMD5, ObjectId mongoId)  throws VCFLoadingException{
        VCFFile theVCF = vcfService.retrieveVCFEntity(vcfFileMD5);
        if (theVCF == null) return null;
        
        if (!MongoFileStatus.NOT_AVAILABLE.equals(theVCF.getStatus())) {
            throw new VCFLoadingException("The file already exists in status " + theVCF.getStatus());
        }
        
        if (vcfService.retrieveVCFEntity(mongoId)!=null) throw new VCFLoadingException("VCF with the same mongo ID already exists");
        
        return theVCF;
    }
    
    public boolean checksumMatches(String file, String expectedChecksum, Integer checksumBytesLimit) throws VCFLoadingException{
        if (expectedChecksum==null || expectedChecksum.trim().isEmpty()) return false;
        
        File fileToCheck = new File(System.getProperty("projects.folder"),file);
        if (!fileToCheck.exists() || !fileToCheck.canRead()){
            throw new VCFLoadingException(String.format("File %s not found or unreadable",fileToCheck.getAbsolutePath()));
        }

        try {
            String foundChecksum = FileDigester.md5Digest(fileToCheck, checksumBytesLimit);
            
            LOGGER.info(String.format("Checksum for file %s is %s",fileToCheck,foundChecksum));
            
            return (foundChecksum.equals(expectedChecksum));
        }catch (ChecksumException e){
            throw new VCFLoadingException(e);
        }
        
        
    }
    
    public VCFFile getVCFToInsertVariants(ObjectId objectId, String user)  throws EntityNotFoundException, AuthorizationException, VCFLoadingException{
        
        VCFFile theVCF = vcfService.retrieveVCFEntity(objectId);
        this.checkLoadingVcf(theVCF, user);
        return theVCF;
    }
    
    public VCFFile getVcfToFinishLoading(Integer vcfFileId, String user) throws EntityNotFoundException, AuthorizationException, VCFLoadingException{
        
        VCFFile theVCF = vcfService.retrieveVCFEntity(vcfFileId);
        this.checkLoadingVcf(theVCF, user);
        return theVCF;
    }

    public VCFFile getVcfToInsertCoverage(Integer vcfFileId, String user) throws EntityNotFoundException, AuthorizationException, VCFLoadingException{

        VCFFile theVCF = vcfService.retrieveVCFEntity(vcfFileId);
        this.checkLoadingVcf(theVCF, user);
        return theVCF;
    }
    
    private void checkLoadingVcf(VCFFile theVCF, String user) throws VCFLoadingException, EntityNotFoundException, AuthorizationException {
        User theUser = this.getUser(user);
        
        if (theVCF == null) throw new EntityNotFoundException();
        
        if (!MongoFileStatus.LOADING.equals(theVCF.getStatus())) throw new VCFLoadingException("Trying to insert variants but the VCF is not in loading status");
        
        if (!theVCF.getLatestJob().getUser().equals(theUser)){
                throw new AuthorizationException();
        }
        
    }
      
}
