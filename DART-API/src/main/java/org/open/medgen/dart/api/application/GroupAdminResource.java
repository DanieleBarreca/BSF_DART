/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.application;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.admin.GroupUsersController;
import org.open.medgen.dart.core.controller.bed.BEDAdminController;
import org.open.medgen.dart.core.controller.vcf.VCFAdminController;
import org.open.medgen.dart.core.model.rdbms.dto.PermissionsDTO;
import org.open.medgen.dart.core.model.rdbms.dto.UserDTO;
import org.open.medgen.dart.api.model.AppResponse;
import org.open.medgen.dart.api.model.AppResponseStatus;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author dbarreca
 */
@Path("group-admin")
public class GroupAdminResource {
    private final static Logger LOGGER = Logger.getLogger(GroupAdminResource.class.getName());
    
    private final GroupUsersController groupUsersController;
    private final VCFAdminController vcfController;
    private final BEDAdminController bedController;

    GroupAdminResource(GroupUsersController groupUsersController, VCFAdminController vcfController,BEDAdminController bedController) {
        this.groupUsersController = groupUsersController;
        this.vcfController = vcfController;
        this.bedController = bedController;    
    }
    
    @GET
    @Path("/user/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserWithEmail(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup, @PathParam("email") String email) {
        
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        if (email == null || email.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "Email not specified" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();
                
        try {
            UserDTO response = groupUsersController.getUserWithEmail(requestor, requestorGroup,email );
            return Response.ok(new AppResponse(AppResponseStatus.OK, "" ,response)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "" ,null)).build();
        }
    }
    
    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup) {
        
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();
                
        try {
            List<UserDTO> response = groupUsersController.getAllUsersForGroup(requestor, requestorGroup );
            return Response.ok(new AppResponse(AppResponseStatus.OK, "" ,response)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "" ,null)).build();
        }
    }
    
    @PUT
    @Path("/user/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserToGroup(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup, @PathParam("login") String login) {
        
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        if (login == null || login.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "Email not specified" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();
                
        try {
           groupUsersController.addExistingUserToGroup(requestor, requestorGroup,login);
            return Response.ok(new AppResponse(AppResponseStatus.OK, "" ,null)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "" ,null)).build();
        } catch (ControllerException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage() ,null)).build();
        }
    }
    
    @DELETE
    @Path("/user/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUserFromGroup(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup, @PathParam("login") String login) {
        
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        if (login == null || login.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "Email not specified" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();
                
        try {
             groupUsersController.removeExistingUserFromGroup(requestor, requestorGroup,login );
            return Response.ok(new AppResponse(AppResponseStatus.OK, "" ,null)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "" ,null)).build();
        } catch (ControllerException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage() ,null)).build();
        }
    }
    
    @POST
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewUserToGroup(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup, UserDTO user) {
        
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        if (user == null) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User not submitted" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();
                
        try {
            String password = groupUsersController.addNewUserToGroup(requestor, requestorGroup,user );
            return Response.ok(new AppResponse(AppResponseStatus.OK, "" ,password)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "" ,null)).build();
        } catch (ControllerException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage() ,null)).build();
        }
    }
    
    @POST
    @Path("/user/{login}/permissions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewUserToGroup(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup, @PathParam("login") String login, PermissionsDTO permissions) {
        
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        if (login == null || login.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User not specified" ,null)).build();
        if (permissions == null) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "Permissions not submitted" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();
                
        try {
            groupUsersController.setPermissionsForUser(requestor, requestorGroup,login, permissions);
            return Response.ok(new AppResponse(AppResponseStatus.OK, "" ,null)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "" ,null)).build();
        } catch (ControllerException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage() ,null)).build();
        }
    }
    
    @GET
    @Path("/user/{login}/password")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetUserPassword(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup, @PathParam("login") String login) {
        
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        if (login == null || login.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User login not specified" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();
                
        try {
            String password = groupUsersController.resetPasswordForUser(requestor, requestorGroup,login );
            return Response.ok(new AppResponse(AppResponseStatus.OK, "" ,password)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "" ,null)).build();
        } catch (ControllerException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage() ,null)).build();
        }
    }
    
    @GET
    @Path("/vcf")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getVcf(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup) {

        if (requestorGroup == null || requestorGroup.isEmpty()) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified", null)).build();
        }

        String requestor = context.getUserPrincipal().getName();

        try {
            return Response.ok(new AppResponse(AppResponseStatus.OK, "", vcfController.getAll(requestor, requestorGroup))).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "", null)).build();
        }
    }

    @DELETE
    @Path("/vcf/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteVCF(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup, @PathParam("id") Integer vcfID) {
        
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        if (vcfID == null) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "VCF ID was not specified" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();
                
        try {
            vcfController.removeVCF(vcfID, requestor, requestorGroup);
            return Response.ok(new AppResponse(AppResponseStatus.OK, "" ,null)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "" ,null)).build();
        } catch (ControllerException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage() ,null)).build();
        }
    }
    
    
    @GET
    @Path("/bed")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getBED(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup) {

        if (requestorGroup == null || requestorGroup.isEmpty()) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified", null)).build();
        }

        String requestor = context.getUserPrincipal().getName();

        try {
            return Response.ok(new AppResponse(AppResponseStatus.OK, "", bedController.getAll(requestor, requestorGroup))).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "", null)).build();
        }
    }

    @DELETE
    @Path("/bed/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteBED(@Context SecurityContext context, @QueryParam("userGroup") String requestorGroup, @PathParam("id") Integer bedID) {
        
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        if (bedID == null) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "BED ID was not specified" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();
                
        try {
            bedController.removeBEDFile(bedID, requestor, requestorGroup);
            return Response.ok(new AppResponse(AppResponseStatus.OK, "" ,null)).build();
        } catch (AuthorizationException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "" ,null)).build();
        } catch (ControllerException ex) {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage() ,null)).build();
        }
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/bed")    
    public Response uploadBedFile(
            @Context SecurityContext context,
            @QueryParam("userGroup") String requestorGroup,
            @QueryParam("genome") String genome,
            MultipartFormDataInput input) {
       
        if (requestorGroup == null || requestorGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();
        if (genome == null) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "Genome not specified" ,null)).build();
        
        String requestor = context.getUserPrincipal().getName();

        
        List<InputPart> files = input.getFormDataMap().get("file");
        if (files.size() == 1) {
            InputPart inputPart = files.get(0);
            String fileName = decodeFileName(inputPart.getHeaders());
            if (fileName == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                bedController.saveBEDFile(inputStream, fileName, requestor, requestorGroup, genome);

                return Response.ok(new AppResponse(AppResponseStatus.OK, "", null)).build();
            } catch (AuthorizationException ex) {
                return Response.ok(new AppResponse(AppResponseStatus.AUTHORIZATION_ERROR, "", null)).build();
            } catch (ControllerException | IOException ex) {
                return Response.ok(new AppResponse(AppResponseStatus.ERROR, ex.getMessage(), null)).build();
            }

        } else {
            return Response.ok(new AppResponse(AppResponseStatus.ERROR, "MISSING FILE", null)).build();
        }
        
    }

    private String decodeFileName(MultivaluedMap<String, String> header ){
        
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
	
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
	
        return null;
    }
    
}
