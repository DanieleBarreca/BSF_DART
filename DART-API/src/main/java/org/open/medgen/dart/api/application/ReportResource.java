package org.open.medgen.dart.api.application;

import org.open.medgen.dart.api.model.AppResponse;
import org.open.medgen.dart.api.model.AppResponseStatus;
import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.report.ReportController;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.query.result.CoverageResultResponse;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("report")
public class ReportResource {
    private static final Logger LOGGER = Logger.getLogger(ReportResource.class.getName());
    
    private ReportController reportController;

    public ReportResource(ReportController reportController) {
        this.reportController = reportController;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveReport(@Context SecurityContext context, @QueryParam("userGroup") String userGroup, String cachedQueryId) {

        if (userGroup == null || userGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();

        String userName = context.getUserPrincipal().getName();

        try {
            return Response.ok(reportController.createReportFromQuery(userName, userGroup, cachedQueryId)).build();
        } catch (AuthorizationException ex) {
            
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (ControllerException ex) {
            LOGGER.log(Level.SEVERE, "Error while saving Report", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReports(@Context SecurityContext context, @QueryParam("userGroup") String userGroup) {

        if (userGroup == null || userGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();

        String userName = context.getUserPrincipal().getName();

        try {
            return Response.ok(reportController.getAllReportsForUserAndGroup(userName, userGroup)).build();
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReport(@Context SecurityContext context, @QueryParam("userGroup") String userGroup, @PathParam("id") Integer reportId) {

        if (userGroup == null || userGroup.isEmpty()) return Response.ok(new AppResponse(AppResponseStatus.ERROR, "User group not specified" ,null)).build();

        String userName = context.getUserPrincipal().getName();

        try {
            return Response.ok(reportController.getReport(reportId,userName, userGroup)).build();
        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("coverage/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoverageResult(
            @Context SecurityContext context,
            @PathParam("id") Integer reportId,
            @QueryParam("first") Integer first,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("geneFilter") String geneFilter,
            @QueryParam("statusFilter") String statusFilter,
            @QueryParam("userGroup") String userGroup){

        if (first==null) first = 0;
        if (pageSize == null) pageSize =10;

        String userName = context.getUserPrincipal().getName();

        try {
            CoverageResultResponse response =reportController.getCoverageResults(reportId, userName, userGroup, first, pageSize, geneFilter,statusFilter);
            if (response==null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }else{
                return Response.ok(response).build();
            }

        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (ControllerException ex) {
            Logger.getLogger(VCFQueryResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GET
    @Path("variants/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoverageResult(
            @Context SecurityContext context,
            @PathParam("id") Integer reportId,
            @QueryParam("userGroup") String userGroup){

           String userName = context.getUserPrincipal().getName();

        try {
            List<VariantModel> response =reportController.getVariantResults(reportId, userName, userGroup);
            return Response.ok(response).build();

        } catch (AuthorizationException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (ControllerException ex) {
            Logger.getLogger(VCFQueryResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }
}
