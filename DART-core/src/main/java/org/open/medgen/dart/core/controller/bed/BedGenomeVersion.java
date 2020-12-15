package org.open.medgen.dart.core.controller.bed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BedGenomeVersion {
    
    private static final Pattern PATCHED_REGEXP= Pattern.compile("(.*)\\.p[0-9]+");
    
    public static String getCorrectedVersion(String genome){
        
        if (genome!=null){
            Matcher m = PATCHED_REGEXP.matcher(genome);
            if (m.matches()){
                genome = m.group(1);
            }
        }
        
        return genome;
    }
    
    public static String getCorrectedContig(String genome, String contig){
        if (contig.contains("chr") && genome.trim().toUpperCase().equals("GRCH37")) return contig.replace("chr", "");
        
        return contig;
    }
}
