/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.user;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.model.rdbms.dto.ChangePasswordDTO;
import org.open.medgen.dart.core.model.rdbms.dto.PermissionsDTO;
import org.open.medgen.dart.core.model.rdbms.dto.UserDTO;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.service.rdbms.service.UserService;
import java.util.Map;
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
public class UserController {
    
    @Inject
    private PermissionController permissionController;
    
    @Inject
    private UserService userService;

    @Transactional
    public UserDTO getUser(String login) throws AuthorizationException{
        Map<String, PermissionsDTO> permissions = permissionController.getPermissionsForUser(login);
        if (permissions == null) throw new AuthorizationException();
        
        User theUser = userService.getUserOrNull(login);
        if (theUser == null) throw new AuthorizationException();
        
        
        return new UserDTO(
                theUser.getLogin(),
                theUser.getFirstName(),
                theUser.getLastName(),
                theUser.getEmail(),
                permissions
        );
    }
    
    @Transactional
    public void updateUser(String login, UserDTO newData) throws AuthorizationException, ControllerException {
        
        User theUser = userService.getUserOrNull(login);
        if (theUser == null) throw new AuthorizationException();
        
        if (!theUser.getLogin().equals(newData.getUserName())) throw new AuthorizationException();

        if (theUser.getPublicUser()) throw new AuthorizationException();
        
        if (newData.getFirstName()!=null && !newData.getFirstName().isEmpty()){
            theUser.setFirstName(newData.getFirstName());
        }
        
       if (newData.getLastName()!=null && !newData.getLastName().isEmpty()){
            theUser.setLastName(newData.getLastName());
        }
       
       if (newData.getEmail()!=null && !newData.getEmail().isEmpty() && !newData.getEmail().equals(theUser.getEmail())){
           if (userService.mailExists(newData.getEmail())){
               throw new ControllerException("The mail is already associated with another user");
           }
           
           theUser.setEmail(newData.getEmail());
       }
        
    }
    
    @Transactional
    public void updateUserPassword(String login, ChangePasswordDTO passwords) throws AuthorizationException,ControllerException {
        
        User theUser = userService.getUserOrNull(login);
        if (theUser == null) throw new AuthorizationException();

        if (theUser.getPublicUser()) throw new AuthorizationException();
        
        if (passwords.getNewPwd() == null || passwords.getNewPwd().isEmpty()) throw new ControllerException("The new password cannot be empty");
        if (passwords.getOldPwd()== null || passwords.getOldPwd().isEmpty()) throw new AuthorizationException();
        
        if (!theUser.getPassword().equals(passwords.getOldPwd())) throw new AuthorizationException();
        
        theUser.setPassword(passwords.getNewPwd());
    }
    
}
