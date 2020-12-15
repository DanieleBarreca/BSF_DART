/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.permission;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.bed.BEDLoadingException;
import org.open.medgen.dart.core.controller.vcf.VCFLoadingException;
import org.open.medgen.dart.core.model.rdbms.dto.PermissionsDTO;
import org.open.medgen.dart.core.model.rdbms.dto.bed.BedFileDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BEDFilePermission;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.QueryPreset;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFile;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFilePermission;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.rdbms.service.DbBEDService;
import org.open.medgen.dart.core.service.rdbms.service.DbVCFService;
import org.open.medgen.dart.core.service.rdbms.service.UserService;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;

import java.util.*;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author dbarreca
 */
@Stateless
public class PermissionController {
         
    @Inject DbVCFService vcfService;
    @Inject UserService userService;
    @Inject DbBEDService bedService;

    
    public Map<String, PermissionsDTO> getPermissionsForUser(String userName) {
        Map<String, PermissionsDTO>  result = new HashMap();
        
        User theUser = userService.getUserOrNull(userName);
        if (theUser == null) return null;
        
        for (UserRole role: theUser.getUserRoles()){
            String group = role.getGroup().getGroup();
            if (!result.containsKey(group)){                
                result.put(group, new PermissionsDTO());
            }
            
            this.setPermissionForRole(result.get(group), role);
            if (theUser.getPublicUser()){
                result.get(group).setPublicUser(true);
            }
        }

        return result;
        
    }
    
    public PermissionsDTO getPermissionsForUser(String userName, String userGroup) {
        
        User theUser = userService.getUserOrNull(userName);
        if (theUser == null) return null;
        
        
        for (UserRole role: theUser.getUserRoles()){            
            if (role.getGroup().getGroup().equals(userGroup)){                         
                PermissionsDTO permissionsDTO =  this.setPermissionForRole(new PermissionsDTO(), role);
                if (theUser.getPublicUser()){
                    permissionsDTO.setPublicUser(true);
                }
                return permissionsDTO;
            }                       
        }

        return null;
        
    }
    
    PermissionsDTO setPermissionForRole(PermissionsDTO permissions, UserRole role) {

        permissions.setCanQueryVCF(role.getCanQuery());
        permissions.setCanSavePreset(role.getCanSaveQueryPreset());
        permissions.setCanSavePanel(role.getCanSaveQueryPanel());
        permissions.setIsAdmin(role.getIsAdmin());
        permissions.setCanUploadVCF(role.getCanUploadVCF());
        permissions.setCanAnnotatePathogenicity(role.getCanAnnotatePathogenicity());
        permissions.setCanValidateVariants(role.getCanValidateVariants());
        permissions.setCanSaveReport(role.getCanSaveReport());
        
        return permissions;
    }

    
    public boolean isGroupAdmin(String user, String group) {
        
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) return false;
        
         for (UserRole role : theUser.getUserRoles()){
            if (role.getGroup().getGroup().equals(group)){
              return role.getIsAdmin();
            }                 
        }
        
        return false;
    }
    
    //VCF FILES 
    public VCFFileDTO getVcfForAnalysis(Integer vcfFileId, String user) throws EntityNotFoundException{
        VCFFile theVCF = vcfService.retrieveVCFEntity(vcfFileId);
        
        if (theVCF == null){
            throw new EntityNotFoundException();
        }                        
        
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) return null;
        
        for (UserRole role : theUser.getUserRoles()){
            if (role.getGroup().getVcfList().contains(theVCF)) 
                return new VCFFileDTO(theVCF,false);
        }
        
        return null;
    }        
    
    public VCFFileDTO getVcfForQuery(Integer vcfFileId, String user, String userGroup) throws EntityNotFoundException{
        
        VCFFile theVCF = vcfService.retrieveVCFEntity(vcfFileId);
        
        if (theVCF == null){
            throw new EntityNotFoundException();
        }                        
        
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) return null;
        
        for (UserRole role : theUser.getUserRoles()){
            if (role.getCanQuery() &&
                role.getGroup().getGroup().equals(userGroup) &&
                role.getGroup().getVcfList().contains(theVCF)) 
                return new VCFFileDTO(theVCF,false);
        }
        
        return null;
    }
    
    public List<VCFFileDTO> getAllVCFsForQuery(String user, String userGroup) {
        
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) return new LinkedList<>();

        for (UserRole role : theUser.getUserRoles()) {
            if (role.getCanQuery() && role.getGroup().getGroup().equals(userGroup)){
                return role.getGroup().getVcfList().stream()
                        .filter((VCFFile entity) -> {
                            return MongoFileStatus.AVAILABLE.equals(entity.getStatus());
                        }).map((VCFFile entity) -> {
                            VCFFileDTO result = new VCFFileDTO(entity,false);
                            return  result;
                        }).collect(Collectors.toList());
            }
        }
 
        return new LinkedList();
    }
    
    public List<VCFFileDTO> getAllVCFsForAdmin(String user, String userGroup) {
        
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) return new LinkedList<>();

        for (UserRole role : theUser.getUserRoles()) {
            if (role.getIsAdmin() && role.getGroup().getGroup().equals(userGroup)){
                return role.getGroup().getVcfList().stream()
                        .filter((VCFFile entity) -> {
                            for (VCFFilePermission permission: entity.getPermissions()){
                                if (permission.getKey().getGroup().equals(role.getGroup())){
                                    return permission.getIsOwner();
                                }
                            }
                            return false;
                        }).map((VCFFile entity) -> {
                            VCFFileDTO result = new VCFFileDTO(entity,true);
                            return result;
                        }).collect(Collectors.toList());
            }
        }
 
        return new LinkedList();
    }
    
    public VCFFile getVcfForDeletion(Integer vcfFileId, String user, String userGroup) throws VCFLoadingException, EntityNotFoundException, AuthorizationException {
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) {
            throw new AuthorizationException();
        }

        VCFFile theVCF = vcfService.retrieveVCFEntity(vcfFileId);
        if (theVCF == null) {
            throw new EntityNotFoundException();
        }

        for (UserRole userRole : theUser.getUserRoles()) {
            if (userRole.getGroup().getGroup().equals(userGroup)) {
                if (!userRole.getIsAdmin()) {
                    throw new AuthorizationException();
                }

                for (VCFFilePermission permission : theVCF.getPermissions()) {
                    if (permission.getIsOwner() && permission.getKey().getGroup().equals(userRole.getGroup())) {
                        switch (theVCF.getStatus()) {
                            case LOADING:
                                throw new VCFLoadingException("The vcf is still loading");
                            case DELETING:
                                throw new VCFLoadingException("The vcf is being deleted");
                            case NOT_AVAILABLE:
                                throw new VCFLoadingException("The vcf is not Available");
                            default:
                                return theVCF;
                        }
                    }
                }

            }
        }

        throw new AuthorizationException();

    }

   
    //BED FILES
    public BedFile getBedWithMD5ForInsert(String bedFileMD5)  throws BEDLoadingException{
        BedFile theBED = bedService.retrieveBEDOrNull(bedFileMD5);
        if (theBED == null) return null;
        
        if (!MongoFileStatus.NOT_AVAILABLE.equals(theBED.getStatus())) {
            throw new BEDLoadingException("The file already exists in status " + theBED.getStatus());
        }
        
        
        return theBED;
    }

    public BedFile getBedForQuery(Integer bedFileId, String user, String userGroup) throws EntityNotFoundException, AuthorizationException {
        BedFile theBED = bedService.retrieveBEDOrNull(bedFileId);

        if (theBED == null) {
            throw new EntityNotFoundException();
        }

        User theUser = userService.getUserOrNull(user);
        if (theUser == null) {
            throw new AuthorizationException();
        }

        for (UserRole role : theUser.getUserRoles()) {
            if (role.getCanQuery()
                    && role.getGroup().getGroup().equals(userGroup)
                    && role.getGroup().getBedFileList().contains(theBED)) {
                return theBED;
            }
        }

        throw new AuthorizationException();
    }
    
    
    public boolean bedIsVisibleToGroup(Integer bedFileId, String userGroup) throws EntityNotFoundException {
        BedFile theBED = bedService.retrieveBEDOrNull(bedFileId);
        if (theBED==null) throw new EntityNotFoundException();
        
        for (BEDFilePermission permission: theBED.getPermissions()){
            if (permission.getKey().getGroup().getGroup().equals(userGroup)){
                return true;
            }
        }
        
        return false;
    }
    
    public BedFile getBedFileForDeletion(Integer bedId, String user, String userGroup) throws BEDLoadingException, EntityNotFoundException, AuthorizationException{
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) {
            throw new AuthorizationException();
        }

        BedFile theBED = bedService.retrieveBEDOrNull(bedId);
        if (theBED == null) {
            throw new EntityNotFoundException();
        }

        for (UserRole userRole : theUser.getUserRoles()) {
            if (userRole.getGroup().getGroup().equals(userGroup)) {
                if (!userRole.getIsAdmin()) {
                    throw new AuthorizationException();
                }

                for (BEDFilePermission permission : theBED.getPermissions()) {
                    if (permission.getIsOwner() && permission.getKey().getGroup().equals(userRole.getGroup())) {
                        switch (theBED.getStatus()) {
                            case LOADING:
                                throw new BEDLoadingException("The bed file is still loading");
                            case DELETING:
                                throw new BEDLoadingException("The bed file is being deleted");
                            case NOT_AVAILABLE:
                                throw new BEDLoadingException("The bed file is not Available");
                            default:
                                return theBED;
                        }
                    }
                }

            }
        }

        throw new AuthorizationException();

    }
    
    
    public List<BedFileDTO> getAllBEDsForQuery(String user, String userGroup, String genome) {
        
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) return new LinkedList<>();

        for (UserRole role : theUser.getUserRoles()) {
            if (role.getCanQuery() && role.getGroup().getGroup().equals(userGroup)){
                return role.getGroup().getBedFileList().stream()
                        .filter((BedFile entity) -> {
                                return MongoFileStatus.AVAILABLE.equals(entity.getStatus()) && (genome==null || genome.equals(entity.getGenome()));
                        }).map((BedFile entity) -> {
                            BedFileDTO result = new BedFileDTO(entity,false);
                            return  result;
                        }).collect(Collectors.toList());
            }
        }
 
        return new LinkedList();
    }
    
    public List<BedFileDTO> getAllBEDsForAdmin(String user, String userGroup) {
        
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) return new LinkedList<>();

        for (UserRole role : theUser.getUserRoles()) {
            if (role.getIsAdmin() && role.getGroup().getGroup().equals(userGroup)){
                return role.getGroup().getBedFileList().stream()
                        .filter((BedFile entity) -> {
                            for (BEDFilePermission permission: entity.getPermissions()){
                                if (permission.getKey().getGroup().equals(role.getGroup())){
                                    return permission.getIsOwner();
                                }
                            }
                            return false;
                        }).map((BedFile entity) -> {
                            BedFileDTO result = new BedFileDTO(entity,true);
                            return result;
                        }).collect(Collectors.toList());
            }
        }
 
        return new LinkedList();
    }
    
    //PRESETS
    public User canSavePreset(String userName, String userGroup) {
        User theUser = userService.getUserOrNull(userName);
        if (theUser == null) return null;
                
         for (UserRole role : theUser.getUserRoles()){
            if (role.getCanSaveQueryPreset() && role.getGroup().getGroup().equals(userGroup)) 
               return theUser;
        }
                
        return null;
    }
    
    public User canSavePanel(String userName, String userGroup) {
        User theUser = userService.getUserOrNull(userName);
        if (theUser == null) return null;
                
        for (UserRole role : theUser.getUserRoles()){
            if (role.getCanSaveQueryPanel() && role.getGroup().getGroup().equals(userGroup)) 
               return theUser;
        }
                
        return null;
    }
    
    public User canDeletePreset(String userName, QueryPreset preset) {
        User theUser = userService.getUserOrNull(userName);
        if (theUser == null) return null;
                                      
        for (UserRole role : theUser.getUserRoles()) {
            if (role.getCanSaveQueryPreset() && role.getGroup().equals(preset.getUserGroup())) {
                return theUser;
            }
        }
                
        return null;
    }
    
    public User canDeletePanel(String userName, PanelPreset panelPreset) {
        User theUser = userService.getUserOrNull(userName);
        if (theUser == null) return null;
                
        for (UserRole role : theUser.getUserRoles()) {
            if (role.getCanSaveQueryPanel() && role.getGroup().equals(panelPreset.getUserGroup())) {
                return theUser;
            }
        }
                
        return null;
    }
    
    public boolean canAccessLocalFile(String userName, String fileURI) throws ControllerException {
        User theUser = userService.getUserOrNull(userName);
        if (theUser == null) return false;
        
        try {
            List<VCFSample> samples = vcfService.getSamplesForFileURI(fileURI);
            
            
            for (VCFSample sample: samples){
                for (VCFFilePermission permission: sample.getVcf().getPermissions()){
                    for (UserRole userRole: theUser.getUserRoles()){
                        if (userRole.getCanQuery() && userRole.getGroup().equals(permission.getKey().getGroup())){
                            return true;
                        }
                    }                     
                }
            }
            
            return false;
            
        }catch (RDBMSServiceException e){
            throw new ControllerException(e);
        }
        
        
    }
    
}
