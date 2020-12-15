/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.application;


import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.bed.BEDController;
import java.util.logging.Logger;
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
@Path("bed")
public class BedResource {
    private final static Logger LOGGER = Logger.getLogger(BedResource.class.getName());
    
    private final BEDController controller;

    public BedResource(BEDController controller) {
        this.controller = controller;
    }
      
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBed(@Context SecurityContext context, @QueryParam("userGroup") String userGroup, @QueryParam("genome") String genome){
        
        String userName = context.getUserPrincipal().getName();
        if (userGroup == null) return Response.status(Response.Status.BAD_REQUEST).build();
        
        try {
            return Response.ok(controller.getAll(userName, userGroup, genome)).build();
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

}
