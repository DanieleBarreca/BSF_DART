/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.service;

import org.open.medgen.dart.core.model.rdbms.entity.ServerRole;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.service.rdbms.dao.UserDAO;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;


/**
 *
 * @author dbarreca
 */
@Stateless
public class UserService {
    
    @Inject
    UserDAO userDAO;

    public UserService() {
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    public User createUser(String login, String email, String firstName, String lastName) throws RDBMSServiceException {
        
        User theUser = getUserWithMailOrNull(email);
        if (theUser !=null) throw new RDBMSServiceException("User with this mail already exists");
        
        theUser = getUserOrNull(login);
        if (theUser !=null) throw new RDBMSServiceException("Login name already taken");
        
        
        theUser = new User();
        theUser.setLogin(login);
        theUser.setEmail(email);
        theUser.setFirstName(firstName);
        theUser.setLastName(lastName);
        theUser.setServerRole(ServerRole.USER);
        
        userDAO.saveUser(theUser);
        
        return theUser;
    }
    
    public User getUserOrNull(String login) {
        return userDAO.getUserOrNull(login);

    }

    public UserGroup getUserGroup(String groupName) {
        return userDAO.getUserGroupOrNull(groupName);

    }
    
    public User getUserWithMailOrNull(String email) {
        return userDAO.getUserWithMail(email);

    }
   
    public boolean mailExists(String email) {
        if (userDAO.getUserWithMail(email)!=null) return true;
        
        return false;
    }

    public List<User> getAllUsers(String requestorGroup) {
        return userDAO.getAllUsers(requestorGroup);
    }

    public UserRole createNewRoleForUser(User theUser, UserGroup userGroup) throws RDBMSServiceException{
        if (theUser == null)  throw new RDBMSServiceException("User cannot be null");
        if (userGroup == null)  throw new RDBMSServiceException("User Group cannot be null");
        
        for (UserRole userRole: theUser.getUserRoles()){
            if (userRole.getGroup().equals(userGroup)) {
                throw new RDBMSServiceException("User Role already exists for requested group");
            }
        }
        
        UserRole newRole = new UserRole();
        newRole.setUser(theUser);
        newRole.setGroup(userGroup);
        
        return userDAO.saveRole(newRole);

    }

    public void removeUserFromGroup(User theUser, UserGroup userGroup) throws RDBMSServiceException{
        if (theUser == null)  throw new RDBMSServiceException("User cannot be null");
        if (userGroup == null)  throw new RDBMSServiceException("User Group cannot be null");
        
        for (UserRole userRole: theUser.getUserRoles()){
            if (userRole.getGroup().equals(userGroup)) {
                userDAO.deleteUserRole(userRole);
            }
        }        
    }

    public String resetPasswordForUser(User theUser) throws RDBMSServiceException {
        try {
            String randomPassword = RandomStringUtils.randomAlphanumeric(8);
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] digestedPassword = digest.digest(randomPassword.getBytes(StandardCharsets.UTF_8));
            String encodedPassword = Base64.getEncoder().encodeToString(digestedPassword);
            
            theUser.setPassword(encodedPassword);
            
            return randomPassword;
            
        } catch (NoSuchAlgorithmException ex) {
            throw new RDBMSServiceException("Error while encoding password");
        }
    }

    
    
    
}
