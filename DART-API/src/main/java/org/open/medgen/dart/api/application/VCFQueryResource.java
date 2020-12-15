/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.application;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.variant.VariantController;
import org.open.medgen.dart.core.model.query.FullQuery;
import org.open.medgen.dart.core.model.query.result.CoverageResultResponse;
import org.open.medgen.dart.core.model.query.result.FilterResultResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author dbarreca
 */
@Path("queryVCF")
public class  VCFQueryResource {
    private final static Logger LOGGER = Logger.getLogger(VCFQueryResource.class.getName());

    private final VariantController controller;

    public VCFQueryResource(VariantController controller) {
        this.controller = controller;
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResult(
            @Context SecurityContext context,
            @PathParam("id") String encodedQueryId,
            @QueryParam("userGroup") String userGroup,
            @QueryParam("first") Integer first,
            @QueryParam("pageSize") Integer pageSize){
        
        if (first!=null && pageSize==null){
            pageSize = 10;
        }

        if (first==null) first = 0;
        
        String userName = context.getUserPrincipal().getName();
        
                
        try {
            FilterResultResponse response =controller.getResults(encodedQueryId, userName, userGroup, first, pageSize);
            if (response==null) {
                return Response.status(Status.NOT_FOUND).build();
            }else{
                return Response.ok(response).build();
            }
            
        } catch (AuthorizationException ex) {
            return Response.status(Status.FORBIDDEN).build();
        } catch (ControllerException ex) {
            Logger.getLogger(VCFQueryResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }

    @GET
    @Path("coverage/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoverageResult(
            @Context SecurityContext context,
            @PathParam("id") String encodedQueryId,
            @QueryParam("first") Integer first,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("geneFilter") String geneFilter,
            @QueryParam("statusFilter") String statusFilter){


        if (first==null) first = 0;
        if (pageSize == null) pageSize =10;

        String userName = context.getUserPrincipal().getName();

        try {
            CoverageResultResponse response =controller.getCoverageResults(encodedQueryId, userName, first, pageSize, geneFilter,statusFilter);
            if (response==null) {
                return Response.status(Status.NOT_FOUND).build();
            }else{
                return Response.ok(response).build();
            }

        } catch (AuthorizationException ex) {
            return Response.status(Status.FORBIDDEN).build();
        } catch (ControllerException ex) {
            Logger.getLogger(VCFQueryResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }
    
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitQuery(@Context SecurityContext context, FullQuery theQuery, @QueryParam("userGroup") String userGroup, @QueryParam("userToken") String userToken ) {
       if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();
       LOGGER.info("Submit Query request ...");
       String userName = context.getUserPrincipal().getName();           
       
        try {
            String queryId = controller.submitQuery(theQuery, userName, userGroup,userToken );
            LOGGER.info("Return query id " + queryId);
            return Response.ok(queryId).build();
        } catch (AuthorizationException ex) {
            return Response.status(Status.FORBIDDEN).build();
        } catch (ControllerException ex) {
            ex.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
       
    }

    
}
