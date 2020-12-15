/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.query;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.controller.vcf.VCFController;
import org.open.medgen.dart.core.model.cache.AuthorizationError;
import org.open.medgen.dart.core.model.cache.CachedQueryInfo;
import org.open.medgen.dart.core.model.cache.CachedQuery;
import org.open.medgen.dart.core.model.rdbms.dto.PermissionsDTO;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.ConditionTermDTO;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.ConditionDictionary;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.cache.CacheService;
import org.open.medgen.dart.core.service.cache.EntityNotFoundException;
import org.open.medgen.dart.core.service.rdbms.service.AnnotationService;
import org.open.medgen.dart.core.service.rdbms.service.DbVCFService;
import org.open.medgen.dart.core.service.rdbms.service.UserService;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 *
 * @author dbarreca
 */
@Stateless
@Default
public class QueryController {
    
    @Inject 
    CacheService cacheService;
    
    @Inject
    AnnotationService annotationService;
    
    @Inject
    PermissionController permissionService;

    @Inject
    UserService userService;
    
    @Inject
    DbVCFService vcfService;

    public List<CachedQueryInfo> getQueriesForUser(String userName, String userGroup, String userToken) throws AuthorizationException{
        PermissionsDTO permissions = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissions==null || (permissions.isPublicUser() && (userToken== null || userToken.trim().isEmpty()))){
            throw new AuthorizationException("Empty or null token for pulic user "+userToken);
        }
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();
        
        List<CachedQuery> result = cacheService.retrieveUserQueries(userName, userGroup, userToken);
        
        try{
            List<CachedQueryInfo> returnValue = result.stream().map(
                    (CachedQuery cachedQuery) -> {
                        return  retrieveQueryInfo(cachedQuery, user ,userGroupEntity);
                    }
            ).collect(Collectors.toList());
            
            return returnValue;
        }catch(AuthorizationError e){
            throw new AuthorizationException();
        }                
    }
    
    public CachedQueryInfo getQueryWithId (String userName, String userGroup, String id) throws AuthorizationException{
        PermissionsDTO permissions = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissions==null){
            throw new AuthorizationException();
        }
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();
        
        CachedQuery result = cacheService.retrieveQueryForUserGroupWithId(userName, userGroup, id);
        
        if (result == null) throw new AuthorizationException();
        
        try{
            return retrieveQueryInfo(result, user ,userGroupEntity);
        }catch(AuthorizationError e){
            throw new AuthorizationException();
        }
    }
    
    private CachedQueryInfo retrieveQueryInfo(CachedQuery query, User user, UserGroup userGroupEntity) {
        VCFSample sample = vcfService.retrieveVCFSampleEntity(query.getTheQuery().getSampleRefId());
        List<ConditionTermDTO> conditions = annotationService.getConditionsForSample(sample, userGroupEntity);
        
       return new CachedQueryInfo(query, user.getLogin(), userGroupEntity.getGroup(), conditions);
    }
    
    public void removeUserFromQueries(String userName, String userGroup, List<String> queryIds) throws ControllerException{
         try {
             for (String queryId: queryIds) {
                 cacheService.removeUser(userName, queryId, userGroup);
             }
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(QueryController.class.getName()).log(Level.SEVERE, null, ex);
            throw new ControllerException("Not found");
        }
        
    }
}
