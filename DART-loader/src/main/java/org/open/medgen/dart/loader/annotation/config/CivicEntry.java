/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.annotation.config;

import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author dbarreca
 */
public class CivicEntry {
    
    private String link;
    
    private String refGenome;
    
    private String chrom;
    private Integer start;
    private Integer end;
    
    
    private String chrom2;
    private Integer start2;
    private Integer end2;
    
    private String ref;
    private String alt;
    
    public CivicEntry(CSVRecord record) throws IllegalArgumentException {
        link = record.get(CIVICHeader.variant_civic_url);

        chrom = record.get(CIVICHeader.chromosome);
        if (chrom != null && !chrom.isEmpty()) {
            refGenome = record.get(CIVICHeader.reference_build);

            start = Integer.parseInt(record.get(CIVICHeader.start));
            end = Integer.parseInt(record.get(CIVICHeader.stop));
            chrom2 = record.get(CIVICHeader.chromosome2);
            if (chrom2 != null && !chrom2.isEmpty()) {
                start2 = Integer.parseInt(record.get(CIVICHeader.start2));
                end2 = Integer.parseInt(record.get(CIVICHeader.stop2));
            } else {
                chrom2 = null;
                ref = record.get(CIVICHeader.reference_bases);
                alt = record.get(CIVICHeader.variant_bases);
            }
        }else{
            throw new IllegalArgumentException("Position data not found for variant "+link);
        }
    }

    public String getLink() {
        return link;
    }

    public String getRefGenome() {
        return refGenome;
    }

    public String getChrom() {
        return chrom;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public String getChrom2() {
        return chrom2;
    }

    public Integer getStart2() {
        return start2;
    }

    public Integer getEnd2() {
        return end2;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }
    
    
    
    
    
    
}
