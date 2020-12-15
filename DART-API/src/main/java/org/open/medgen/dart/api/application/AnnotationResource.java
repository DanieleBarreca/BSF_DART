package org.open.medgen.dart.api.application;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.annotation.AnnotationController;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.AnnotatedPathogenicityDTO;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.AnnotatedVariantSampleDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("annotation")
public class AnnotationResource {

    private final AnnotationController annotationController;

    public AnnotationResource(AnnotationController annotationController) {
        this.annotationController = annotationController;
    }

    @GET()
    @Path("conditionTerms/dictionaries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDictionaries(@Context SecurityContext context) {

        return Response.ok(annotationController.getConditionDictionaries()).build();
    }

    @GET()
    @Path("conditionTerms/{dictionary}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConditionTerm(@Context SecurityContext context, 
                                     @PathParam("dictionary") String conditionDictionary, 
                                     @QueryParam("query") String queryString,
                                     @QueryParam("first") Integer first,
                                     @QueryParam("pageSize") Integer pageSize) {
        
        if (first==null) first = 0;
        if (pageSize==null) pageSize = 100;
        
        return Response.ok(annotationController.getConditionTerms(conditionDictionary, queryString, first, pageSize)).build();
    }

    @GET()
    @Path("annotationTerms/dictionaries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnnotationTerms(@Context SecurityContext context) {

        return Response.ok(annotationController.getAnnotationDictionaries()).build();
    }
    
    @GET()
    @Path("annotationTerms/{dictionary}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnnotationTerms(@Context SecurityContext context, @PathParam("dictionary") String annotationDictionary) {

        return Response.ok(annotationController.getAnnotationDictionaryTerm(annotationDictionary)).build();
    }

    @GET()
    @Path("inheritanceTerms/{dictionary}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInheritanceTerms(@Context SecurityContext context, @PathParam("dictionary") String annotationDictionary) {

        return Response.ok(annotationController.getInheritanceDictionaryTerm(annotationDictionary)).build();
    }

    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    public Response annotateVariant(@Context SecurityContext context, AnnotatedPathogenicityDTO annotatedVariant, 
                         @QueryParam("userGroup") String userGroup) {

        if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();

        String userName = context.getUserPrincipal().getName();
        
        try {
            annotationController.annotateVariant(userName, userGroup,annotatedVariant );
            return Response.ok().build();
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (ControllerException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST()
    @Path("variant-sample")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response annotateVariant(@Context SecurityContext context, 
                                    AnnotatedVariantSampleDTO annotatedVariant,
                                    @QueryParam("userGroup") String userGroup) {

        if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();

        String userName = context.getUserPrincipal().getName();

        try {
            annotationController.annotateVariantSample(userName, userGroup,annotatedVariant);
            return Response.ok().build();
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (ControllerException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE()
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeAnnotation(@Context SecurityContext context, 
                                     @PathParam("id") Integer annotationId,
                                    @QueryParam("userGroup") String userGroup) {

        if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();

        String userName = context.getUserPrincipal().getName();

        try {
            annotationController.deleteAnnotation(annotationId, userName, userGroup );
            return Response.ok().build();
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } 
    }

    @DELETE()
    @Path("variant-sample/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeVariantSampleAnnotation(@Context SecurityContext context,
                                     @PathParam("id") Integer annotationId,
                                     @QueryParam("userGroup") String userGroup) {

        if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();

        String userName = context.getUserPrincipal().getName();

        try {
            annotationController.deleteVariantSampleAnnotation(annotationId, userName, userGroup );
            return Response.ok().build();
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("sample/{sample_id}/condition")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addConditionToSample(@Context SecurityContext context, Integer conditionId, @PathParam("sample_id") Integer sampleId,@QueryParam("userGroup") String userGroup) {
        if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();

        String userName = context.getUserPrincipal().getName();
        try{
            return Response.ok(annotationController.addConditionToSample(userName, userGroup,sampleId,conditionId)).build();

        }catch(AuthorizationException e){
            e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch (ControllerException e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("sample/{sample_id}/condition/{condition_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeConditionFromSample(@Context SecurityContext context, @PathParam("sample_id") Integer sampleId, @PathParam("condition_id") Integer conditionId, @QueryParam("userGroup") String userGroup) {
        if(userGroup==null) return Response.status(Response.Status.BAD_REQUEST).build();

        String userName = context.getUserPrincipal().getName();
        try{
            return Response.ok(annotationController.removeConditionFromSample(userName, userGroup,sampleId, conditionId)).build();

        }catch(AuthorizationException e){
            e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch (ControllerException e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
