/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.application;


import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.vcf.VCFController;
import org.open.medgen.dart.core.controller.vcf.VCFAdminController;
import java.util.logging.Logger;;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author dbarreca
 */
@Path("vcf")
public class VCFResource {
    
    private final static Logger LOGGER = Logger.getLogger(VCFResource.class.getName());
    
    private final VCFController controller;
    private final VCFAdminController deleteController;
    
    public VCFResource(VCFController controller, VCFAdminController deleteController) {
        this.controller= controller;
        this.deleteController = deleteController;
    }
 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVcf(@Context SecurityContext context, @QueryParam("fileId") Integer fileId, @QueryParam("userGroup") String userGroup){
        String userName = context.getUserPrincipal().getName();
        try {
            if (fileId == null) {
                if (userGroup == null) return Response.status(Response.Status.BAD_REQUEST).build();
                
                return Response.ok(controller.getAll(userName, userGroup)).build();
            }else{
                return Response.ok(controller.getVCFInfo(fileId, userName)).build();
            }
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
    
    
}
