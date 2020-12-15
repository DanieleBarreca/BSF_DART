/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.cache;

import org.open.medgen.dart.core.model.cache.CachedQuery;

/**
 *
 * @author dbarreca
 */
public class CachingResult {
    
    private final CachedQuery theQuery;
    private final boolean run;


    public CachingResult(CachedQuery theQuery, boolean run) {
        this.theQuery = theQuery;
        this.run = run;
    }

    public CachedQuery getQuery() {
        return theQuery;
    }
   
    public boolean toRun() {
        return run;
    }
    
    
}
