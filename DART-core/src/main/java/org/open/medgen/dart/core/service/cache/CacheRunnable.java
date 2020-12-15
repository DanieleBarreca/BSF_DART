/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.cache;

/**
 *
 * @author dbarreca
 */
@FunctionalInterface
public interface CacheRunnable {
    
    public void run() throws EntityNotFoundException;
}
