package org.open.medgen.dart.files;


import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.handlers.DefaultServlet;
import io.undertow.servlet.spec.ServletContextImpl;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.permission.PermissionController;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

@RequestScoped 
public class StaticContentServlet extends DefaultServlet {

    private final static Logger LOGGER = Logger.getLogger(StaticContentServlet.class.getName());

    @Inject
    private PermissionController permissionController;

    public void init(ServletConfig config) throws ServletException {
        
        ResourceManager resourceManager = new PathResourceManager(Paths.get(System.getProperty("projects.folder")), 1024L, true, true, new String[0]);
        
        DeploymentInfo deploymentInfo =  ((ServletContextImpl) config.getServletContext()).getDeployment().getDeploymentInfo();
        deploymentInfo.setResourceManager(resourceManager);
        deploymentInfo.getPreCompressedResources().clear();
        
        
        super.init(config);
        
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String file=req.getPathInfo();
        while(file.startsWith("/")){
            file=file.substring(1);
        }
        if ( file.contains("/") && file.split("/")[0].equals("genomes")){
            super.service(req,resp);
        }else {

            String userName = req.getUserPrincipal().getName();

            try {

                if (permissionController.canAccessLocalFile(userName, file)) {
                    super.service(req, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }

            } catch (ControllerException e) {
                throw new ServletException(e);
            }
        }
        
      
    }
    
}
