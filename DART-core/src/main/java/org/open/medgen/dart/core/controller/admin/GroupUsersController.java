/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.admin;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.model.rdbms.dto.PermissionsDTO;
import org.open.medgen.dart.core.model.rdbms.dto.UserDTO;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.service.rdbms.service.UserService;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author dbarreca
 */
@Stateless
@Default
public class GroupUsersController {

    @Inject UserService userService;
    @Inject PermissionController permissionController;
    
    @Transactional
    public UserDTO getUserWithEmail(String requestor, String requestorGroup, String email) throws AuthorizationException {
        
        if (!permissionController.isGroupAdmin(requestor, requestorGroup)) throw new AuthorizationException();
        
        User theUser = userService.getUserWithMailOrNull(email);
        if (theUser == null) {
            return null;
        }

        return new UserDTO(theUser.getLogin(), theUser.getFirstName(), theUser.getLastName(), theUser.getEmail(), new HashMap());
    }

    @Transactional
    public List<UserDTO> getAllUsersForGroup(String requestor, String requestorGroup) throws AuthorizationException{
        if (!permissionController.isGroupAdmin(requestor, requestorGroup)) throw new AuthorizationException();
        
        List<User> users = userService.getAllUsers(requestorGroup);
        return users.stream().map((User theUser) -> {
            PermissionsDTO permissions = permissionController.getPermissionsForUser(theUser.getLogin(), requestorGroup);
            if (permissions == null) permissions = new PermissionsDTO();
            
            Map<String, PermissionsDTO> groupPermissions = new HashMap();
            groupPermissions.put(requestorGroup, permissions);
            return new UserDTO(theUser.getLogin(), theUser.getFirstName(), theUser.getLastName(), theUser.getEmail(), groupPermissions);
        }).collect(Collectors.toList());
        
    }

    @Transactional
    public void addExistingUserToGroup(String requestor, String requestorGroup, String login) throws AuthorizationException, ControllerException {
        if (!permissionController.isGroupAdmin(requestor, requestorGroup)) throw new AuthorizationException();
        
        User theUser = userService.getUserOrNull(login);
        if (theUser == null) throw new ControllerException("User not found");
                
      
        try{
            userService.createNewRoleForUser(theUser, userService.getUserGroup(requestorGroup));
        }catch (RDBMSServiceException ex){
            throw new ControllerException(ex.getMessage());
        }           
    }

    @Transactional
    public void removeExistingUserFromGroup(String requestor, String requestorGroup, String login) throws AuthorizationException, ControllerException {
        if (!permissionController.isGroupAdmin(requestor, requestorGroup)) throw new AuthorizationException();
        
        User theUser = userService.getUserOrNull(login);
        if (theUser == null) throw new ControllerException("User not found");
       
        try{
            userService.removeUserFromGroup(theUser, userService.getUserGroup(requestorGroup));
        }catch (RDBMSServiceException ex){
            throw new ControllerException(ex.getMessage());
        } 
    }

    @Transactional
    public String addNewUserToGroup(String requestor, String requestorGroup, UserDTO user) throws AuthorizationException, ControllerException {
        if (!permissionController.isGroupAdmin(requestor, requestorGroup)) throw new AuthorizationException();
        
        try {
            User theUser = userService.createUser(user.getUserName(), user.getEmail(), user.getFirstName(), user.getLastName());
            userService.createNewRoleForUser(theUser, userService.getUserGroup(requestorGroup));
            return userService.resetPasswordForUser(theUser);
            
        } catch (RDBMSServiceException ex) {
            throw new ControllerException(ex.getMessage());
        }        
    }
    
    @Transactional
    public String resetPasswordForUser(String requestor, String requestorGroup, String login) throws AuthorizationException, ControllerException {
        if (!permissionController.isGroupAdmin(requestor, requestorGroup)) throw new AuthorizationException();
        
        User theUser = userService.getUserOrNull(login);
        if (theUser == null) throw new ControllerException("User not found");
       
        try{
            return userService.resetPasswordForUser(theUser);
        }catch (RDBMSServiceException ex){
            throw new ControllerException(ex.getMessage());
        } 
    }
    
    @Transactional
    public void setPermissionsForUser(String requestor, String requestorGroup, String login, PermissionsDTO permissions) throws AuthorizationException, ControllerException {
        if (!permissionController.isGroupAdmin(requestor, requestorGroup)) throw new AuthorizationException();
        
        User theUser = userService.getUserOrNull(login);
        if (theUser == null) throw new ControllerException("User not found");
        
        for (UserRole role: theUser.getUserRoles()){
            if (role.getGroup().getGroup().equals(requestorGroup)){
                role.setIsAdmin(permissions.isIsAdmin());
                role.setCanUploadVCF(permissions.isCanUploadVCF());
                
                if (role.getCanQuery() && !permissions.isCanQueryVCF()){
                    //PERMISSION TO QUERY WAS REVOKED
                    role.setCanSaveQueryPanel(false);
                    role.setCanSaveQueryPreset(false);
                    role.setCanQuery(false);
                    role.setCanAnnotatePathogenicity(false);
                    role.setCanValidateVariants(false);
                    role.setCanSaveReport(false);
                }else{
                    role.setCanSaveQueryPanel(permissions.isCanSavePanel());
                    role.setCanSaveQueryPreset(permissions.isCanSavePreset());
                    role.setCanSaveReport(permissions.isCanSaveReport());
                    
                    if (role.getCanSaveQueryPanel() || role.getCanSaveQueryPreset() || role.getCanSaveReport()){
                        role.setCanQuery(true);
                    }else{
                        role.setCanQuery(permissions.isCanQueryVCF());
                    }

                    role.setCanAnnotatePathogenicity(permissions.isCanAnnotatePathogenicity());
                    role.setCanValidateVariants(permissions.isCanValidateVariants());
                    
                }
                
                return;
            }
        }
        
        throw new ControllerException("The user does not belong to your group");
    }
    
}
