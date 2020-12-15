/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.application;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.query.QueryController;
import org.open.medgen.dart.core.model.cache.CachedQueryInfo;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static java.util.stream.Collectors.groupingBy;

/**
 *
 * @author dbarreca
 */
@Path("query")
public class SavedQueryResource {

    private static final Logger LOG = Logger.getLogger(SavedQueryResource.class.getName());
    
    
    private final QueryController controller;

    public SavedQueryResource(QueryController controller) {
        this.controller = controller;
    }
     
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserQueries(@Context SecurityContext context, @QueryParam("userGroup") String userGroup, @QueryParam("userToken") String userToken) {
        if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();
        String userName = context.getUserPrincipal().getName();
        try{
            List<CachedQueryInfo> queries = controller.getQueriesForUser(userName, userGroup,userToken);
            if (queries == null || queries.size()==0) {
                return Response.ok(new HashMap<>()).build();
            } else {
                Collections.sort(queries, (CachedQueryInfo q1, CachedQueryInfo q2) -> -q1.getLastAccessed().compareTo(q2.getLastAccessed()));
                return Response.ok(queries.stream().collect(groupingBy(CachedQueryInfo::getSampleName, LinkedHashMap::new, Collectors.toList()))).build();
            }
        }catch(AuthorizationException e){
            LOG.info(e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
    
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuery(@Context SecurityContext context,@PathParam("id") String queryId,@QueryParam("userGroup") String userGroup){
        if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();
                
        String userName = context.getUserPrincipal().getName(); 
        
        try{
            CachedQueryInfo theQuery = controller.getQueryWithId(userName,userGroup, queryId);
            if (theQuery == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }else{
                return Response.ok(theQuery).build();
            }
        }catch(AuthorizationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        } 
         
    }
    
    @DELETE
    public Response removeUserFromQueries(@Context SecurityContext context,@QueryParam("ids") List<String> queryIds,@QueryParam("userGroup") String userGroup){
        if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();

        String userName = context.getUserPrincipal().getName(); 
        
        try {
            controller.removeUserFromQueries(userName, userGroup, queryIds);
            return Response.ok().build();
        } catch (ControllerException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
