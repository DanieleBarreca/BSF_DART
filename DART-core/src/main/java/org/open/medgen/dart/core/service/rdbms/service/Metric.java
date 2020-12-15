/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.service;

/**
 *
 * @author dbarreca
 */
public class Metric {

    private Long totalTime = 0L;
    private Integer totalSamples =0;

    
    public void startSample(){
        this.totalSamples +=1;
        this.totalTime +=System.currentTimeMillis();
    }
    
    public void stopSample(){
        this.totalTime -= System.currentTimeMillis();
    }
    
    public Double getAverageTime(){
        return (this.totalTime*-1.0/totalSamples);
    }
    
    public void reset() {
        this.totalSamples = 0;
        this.totalTime = 0L;
    }
    
    
    
}
