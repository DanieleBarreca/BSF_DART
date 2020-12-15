/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.manamgement;

import javax.management.MXBean;

/**
 *
 * @author dbarreca
 */
@MXBean
public interface MongoManagementService {

    public void indexMongo();
    
}
