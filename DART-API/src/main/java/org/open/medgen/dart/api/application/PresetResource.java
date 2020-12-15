/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.application;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.preset.PresetController;
import org.open.medgen.dart.core.model.query.QueryFilter;
import org.open.medgen.dart.core.model.query.QueryPanel;
import org.open.medgen.dart.core.model.query.QueryRule;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
@Path("preset")
public class PresetResource {
    
    private static final Logger LOG = Logger.getLogger(PresetResource.class.getName());
     
    private final PresetController controller;

    public PresetResource(PresetController controller) {
        this.controller = controller;
    }
  
    
    @GET
    @Path("queries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreset(@Context SecurityContext context, @QueryParam("vcfId") Integer vcfId, @QueryParam("userGroup") String userGroup){
        if (userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();
        
        String userName = context.getUserPrincipal().getName();
        
        try{        
            if (vcfId!=null){
                return Response.ok(controller.getQueryPresetsForUserAndVCF(userName,userGroup, vcfId)).build();
            }else{
                return Response.ok(controller.getAllQueryPresetsForUser(userName,userGroup)).build();
            }
        }catch(AuthorizationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch(ControllerException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @POST
    @Path("check-query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkPreset(@Context SecurityContext context, @QueryParam("userGroup") String userGroup,  QueryRule theQuery){
        
        if (userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();
        String userName = context.getUserPrincipal().getName();

        try{
            return Response.ok(controller.getPresetForQuery(userName,userGroup, theQuery)).build();
        }catch(AuthorizationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch(ControllerException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GET
    @Path("panels")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPanel(@Context SecurityContext context, @QueryParam("userGroup") String userGroup){
        
        String userName = context.getUserPrincipal().getName();
        if (userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();
        
        try{        
            return Response.ok(controller.getPanelsForUser(userName,userGroup)).build();
        }catch(AuthorizationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("check-panel")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkPanel(@Context SecurityContext context, QueryPanel thePanel, @QueryParam("userGroup") String group){
        String userName = context.getUserPrincipal().getName();

        if (group==null) return Response.status(Response.Status.BAD_REQUEST).build();
        
        try{
            return Response.ok(controller.getPresetForPanel(userName, group, thePanel)).build();
        }catch(AuthorizationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch(ControllerException e){
            LOG.log(Level.SEVERE, "ERROR while persisting query preset", e);
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("fields")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFieldsPreset(@Context SecurityContext context, @QueryParam("vcfType") VCFType vcfType){
        if (vcfType==null) return Response.status(Response.Status.BAD_REQUEST).build();
        
        String userName = context.getUserPrincipal().getName();
        
        return Response.ok(controller.getFieldsPreset(userName,vcfType)).build();
        
    }
    
    @DELETE
    @Path("queries/{id}")
    public Response deleteQuery(@Context SecurityContext context, @PathParam("id") Integer presetId){
        
        String userName = context.getUserPrincipal().getName();
        
        try{
            controller.deleteQueryPreset(userName, presetId);
            return Response.ok().build();
        }catch(AuthorizationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
    
    @DELETE
    @Path("panels/{id}")
    public Response deletePanel(@Context SecurityContext context, @PathParam("id") Integer panelId){
        
        String userName = context.getUserPrincipal().getName();
        
        try{
            controller.deletePanelPreset(userName, panelId);
            return Response.ok().build();
        }catch(AuthorizationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
    
    @POST
    @Path("queries")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPreset(@Context SecurityContext context, QueryFilter theQueryPreset, @QueryParam("userGroup") String group){
        String userName = context.getUserPrincipal().getName();
        
        if (group==null) return Response.status(Response.Status.BAD_REQUEST).build();
        
        try{        
            controller.addQueryToGroup(userName, group, theQueryPreset);
            return Response.ok().build();
        }catch(AuthorizationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch(ControllerException e){
            LOG.log(Level.SEVERE, "ERROR while persisting query preset", e);
            return Response.serverError().build();
        }
    }
    
    @POST
    @Path("panels")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPreset(@Context SecurityContext context, QueryPanel thePanel, @QueryParam("userGroup") String group){
        String userName = context.getUserPrincipal().getName();
        
        if (group==null) return Response.status(Response.Status.BAD_REQUEST).build();
        
        try{        
            controller.addPanelPresetToGroup(userName, group, thePanel);
            return Response.ok().build();
        }catch(AuthorizationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch(ControllerException e){
            LOG.log(Level.SEVERE, "ERROR while persisting query preset", e);
            return Response.serverError().build();
        }
    }
    
    @POST
    @Path("fields")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postFieldPreset(@Context SecurityContext context, List<Integer> thePreset, @QueryParam("vcfType") VCFType vcfType){
        String userName = context.getUserPrincipal().getName();
        
        if (vcfType==null) return Response.status(Response.Status.BAD_REQUEST).build();
        
        try{        
            controller.saveFieldsPreset(userName, vcfType, thePreset);
            return Response.ok().build();
       }catch(ControllerException e){
            LOG.log(Level.SEVERE, "ERROR while persisting query preset", e);
            return Response.serverError().build();
        }
    }
}
