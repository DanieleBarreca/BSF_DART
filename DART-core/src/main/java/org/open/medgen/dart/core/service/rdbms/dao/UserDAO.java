/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.dao;

import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole_;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup_;
import org.open.medgen.dart.core.model.rdbms.entity.User_;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

/**
 *
 * @author dbarreca
 */
@Stateless
public class UserDAO extends DAO {

    public UserDAO() {
    }

    public UserDAO(EntityManager em) {
        super(em);
    }
    
    public User getUserOrNull(String login){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<User> userCriteria = cb.createQuery(User.class);     

        Root<User> userRoot = userCriteria.from(User.class);        
        userCriteria.where(cb.equal(userRoot.get(User_.login), login));
        
        try{
            return em.createQuery(userCriteria).getSingleResult();          
        }catch(NoResultException e){
            return null;
        }
    }
    
    public void saveUser(User user){
        em.persist(user);
        em.flush();
    }
    
    
    public UserGroup getUserGroupOrNull(String groupName) throws NoResultException{
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<UserGroup> userGroupCriteria = cb.createQuery(UserGroup.class);     

        Root<UserGroup> userGroupRoot = userGroupCriteria.from(UserGroup.class);        
        userGroupCriteria.where(
            cb.equal(userGroupRoot.get(UserGroup_.group), groupName)
        );
        
        try{            
            return em.createQuery(userGroupCriteria).getSingleResult();          
        }catch(NoResultException e){
            return null;
        }          
    }
    
    public UserGroup createUserGroup(String groupName){
        UserGroup theGroup = new UserGroup();
        theGroup.setGroup(groupName);
        
        em.persist(theGroup);
        em.flush();
        
        return theGroup;
    }

    public List<User> getAllUsers() {
        return em.createQuery("Select u from User u", User.class).getResultList();
    }

    public List<UserGroup> getAllGroups() {
        return em.createQuery("Select g from UserGroup g", UserGroup.class).getResultList();
    }

    public User getUserWithMail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<User> userCriteria = cb.createQuery(User.class);

        Root<User> userRoot = userCriteria.from(User.class);
        userCriteria.where(cb.equal(userRoot.get(User_.email), email));

        try{
            return em.createQuery(userCriteria).getSingleResult();          
        }catch(NoResultException e){
            return null;
        }
    }

    public List<User> getAllUsers(String requestorGroup) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<User> userCriteria = cb.createQuery(User.class);
        Root<User> userRoot = userCriteria.from(User.class);
        Join<User, UserRole> userRole = userRoot.join(User_.userRoles);
        Join<UserRole, UserGroup> userGroup = userRole.join(UserRole_.group);
        
        userCriteria.where(cb.equal(userGroup.get(UserGroup_.group), requestorGroup));
        userCriteria.orderBy(cb.asc(userRoot.get(User_.email)));
        return em.createQuery(userCriteria).getResultList();
        
    }

    public UserRole saveRole(UserRole newRole) {
    
        em.persist(newRole);
        em.flush();
        
        return newRole;
        
    }

    public void deleteUserRole(UserRole userRole) {
        em.remove(userRole);
        em.flush();
    }

}
