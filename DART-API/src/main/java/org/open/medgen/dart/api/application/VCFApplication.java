/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.api.application;

import org.open.medgen.dart.core.controller.admin.GroupUsersController;
import org.open.medgen.dart.core.controller.aggregation.AggregationController;
import org.open.medgen.dart.core.controller.annotation.AnnotationController;
import org.open.medgen.dart.core.controller.bed.BEDController;
import org.open.medgen.dart.core.controller.bed.BEDAdminController;
import org.open.medgen.dart.core.controller.preset.PresetController;
import org.open.medgen.dart.core.controller.query.QueryController;
import org.open.medgen.dart.core.controller.report.ReportController;
import org.open.medgen.dart.core.controller.user.UserController;
import org.open.medgen.dart.core.controller.variant.VariantController;
import org.open.medgen.dart.core.controller.vcf.VCFController;
import org.open.medgen.dart.core.controller.vcf.VCFAdminController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;


/**
 *
 * @author dbarreca
 */
@Singleton
@ApplicationPath("v1")
public class VCFApplication extends Application {
    
    private static final Logger LOGGER = Logger.getLogger(VCFApplication.class.getName());
    
    
    @Context
    ServletContext servletContext;
   
    @Inject private UserController userController;
    @Inject private QueryController queryContoller;
    @Inject private VariantController variantController;
    @Inject private VCFController vcfController;
    @Inject private VCFAdminController vcfDeleteController;
    @Inject private BEDAdminController bedAdminController;
    @Inject private BEDController bedController;
    @Inject private AggregationController aggregationController;
    @Inject private PresetController presetController;
    @Inject private GroupUsersController groupUsersController;
    @Inject private VCFAdminController groupVCFController;
    @Inject private AnnotationController annotationController;
    @Inject private ReportController reportController;
    
    @PostConstruct
    private void startup(){

        LOGGER.info("Initializing Application...");        
        
    }
    
    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down application...");

    }
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        resources.addAll(super.getClasses());
        return resources;
    }
    
    
    @Override
    public Set<Object> getSingletons() {
        HashSet<Object> set = new HashSet();

        try {
            CorsFilter corsFilter = new CorsFilter();
            corsFilter.getAllowedOrigins().add("*");
            corsFilter.setExposedHeaders("Content-Disposition");
            set.add(corsFilter);
            set.add(new JacksonProvider());
            set.add(new VCFQueryResource(variantController));
            set.add(new VCFResource(vcfController,vcfDeleteController));
            set.add(new SavedQueryResource(queryContoller));
            set.add(new BedResource(bedController));
            set.add(new AggregationsResource(aggregationController));
            set.add(new UserResource(userController));
            set.add(new PresetResource(presetController));
            set.add(new GroupAdminResource(groupUsersController,groupVCFController,bedAdminController));
            set.add(new AnnotationResource(annotationController));
            set.add(new ReportResource(reportController));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return set;
    }

    
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put("jersey.config.server.disableMoxyJson", true);

        return props;
    }
    
    
}
