/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.manamgement;

import org.open.medgen.dart.core.service.mongo.provider.AsyncCollectionFactory;
import java.lang.management.ManagementFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 *
 * @author dbarreca
 */
@Singleton
@Startup
public class MongoManagementServiceImpl implements MongoManagementService {
    
    private ObjectName objectName;
    private MBeanServer mbeanServer;
    
    @PostConstruct
    protected void startup() {
        String name = this.getClass().getSimpleName();
        try {
            objectName = new ObjectName("DART", "management", name);
            mbeanServer = ManagementFactory.getPlatformMBeanServer();
            mbeanServer.registerMBean(this, objectName);
        } catch (Exception e) {
            throw new IllegalStateException("Error during registration of "
                + name + " into JMX:" + e, e);
        }
    }

    @PreDestroy
    protected void destroy() {
        try {
            mbeanServer.unregisterMBean(this.objectName);
        } catch (Exception e) {
            throw new IllegalStateException("Error during unregistration of "
                + objectName.getKeyProperty("management") + " into JMX:" + e, e);
        }
    }

    
    @Inject
    AsyncCollectionFactory collections;
    
    @Override
    public void indexMongo() {
       collections.indexCollections();
    }
}
