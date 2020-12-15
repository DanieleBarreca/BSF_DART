/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.application;

import java.security.Principal;
import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.user.UserController;
import org.open.medgen.dart.core.model.rdbms.dto.ChangePasswordDTO;
import org.open.medgen.dart.core.model.rdbms.dto.UserDTO;
import org.open.medgen.dart.api.model.AppResponse;
import org.open.medgen.dart.api.model.AppResponseStatus;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author dbarreca
 */
@Path("user")
public class UserResource {
    private final static Logger LOGGER = Logger.getLogger(UserResource.class.getName());
    
    private UserController userController;

    public UserResource(UserController userController) {
        this.userController = userController;
    }
    
   
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@Context SecurityContext context){
        Principal principal = context.getUserPrincipal();
        if (principal == null){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        String userName = context.getUserPrincipal().getName();        
        try {
            return Response.ok(userController.getUser(userName)).build();
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyUserDetails(@Context SecurityContext context, UserDTO user){
        String userName = context.getUserPrincipal().getName();
        
        try {
            userController.updateUser(userName, user);
            return Response.ok(new AppResponse(AppResponseStatus.OK, "", null)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "", null)).build();
        } catch (ControllerException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage(), null)).build();
        }
    }
    
    @PUT()
    @Path("password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePassword(@Context SecurityContext context, ChangePasswordDTO passwords){
        
           
        String userName = context.getUserPrincipal().getName();
        
        try {
            userController.updateUserPassword(userName, passwords);
            return Response.ok(new AppResponse(AppResponseStatus.OK, "", null)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "", null)).build();
        } catch (ControllerException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage(), null)).build();
        }
    }
    
}
