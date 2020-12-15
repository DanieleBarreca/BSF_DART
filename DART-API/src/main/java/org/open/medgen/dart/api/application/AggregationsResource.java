/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.application;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.aggregation.AggregationController;
import org.open.medgen.dart.core.model.mongo.aggregations.TranscriptMutationCount;
import java.util.List;
import javax.ws.rs.GET;
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
@Path("aggregations")
public class AggregationsResource {
    
    private AggregationController controller;

    public AggregationsResource(AggregationController controller) {
        this.controller = controller;
    }
    
    @GET
    @Path("variants/{vcfId}/{transcriptId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context SecurityContext context,
            @PathParam("vcfId") Integer vcfId,
            @PathParam("transcriptId") String transcriptId,
            @QueryParam("sampleName") String sampleName){
        
        String userName = context.getUserPrincipal().getName();    

        
        try {
            List<TranscriptMutationCount> result = controller.getMutationByVCFAndTranscript(
                    vcfId,
                    transcriptId,
                    sampleName,
                    userName
            );
            
            return Response.ok(result).build();
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (ControllerException ex2){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }
   
    
}
