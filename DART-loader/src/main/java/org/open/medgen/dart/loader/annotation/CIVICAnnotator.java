/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.annotation;

import org.open.medgen.dart.core.model.mongo.variant.AttributeMap;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFAttributeNumber;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.FieldLocation;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.UnsupportedFieldException;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFHeaderLineType;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import org.open.medgen.dart.loader.annotation.config.CIVICHeader;
import org.open.medgen.dart.loader.annotation.config.CivicEntry;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author dbarreca
 */
public class CIVICAnnotator {

    private List<CivicEntry>  rows = new LinkedList();
    
    public CIVICAnnotator() throws IOException {
        try{
         InputStream inputStream = CIVICAnnotator.class.getClassLoader().getResourceAsStream("CIVIC_VariantSummaries.tsv");
         Iterable<CSVRecord> records = CSVFormat.RFC4180
                    .withDelimiter('\t')
                    .withHeader(CIVICHeader.class)
                    .withSkipHeaderRecord()
                    .parse(new InputStreamReader(inputStream));
         for (CSVRecord record: records){
             try{
                 rows.add(new CivicEntry(record));
             }catch(Exception e){
                 System.err.print(e.getMessage());
             }
         }
           
        }catch(Exception e){
            throw new IOException("Could not load CIVIC annotator",e);
        }
         
       
    }
    

    public static List<VCFInfoDTO> getFields() throws UnsupportedFieldException{
        List<VCFInfoDTO> result = new LinkedList<>();
       
        result.add(new VCFInfoDTO("CIVIC:Link", "Link to overlapping civic variants, if present", FieldLocation.TRANSCRIPT, VCFAttributeNumber.VALUE, 
                VCFHeaderLineType.String));
        return result;
    }
    
    public void annotateTranscript(AttributeMap transcript, String chrom ,Integer pos, String ref, String alt){       
        Integer refLength = ref.length();
        Integer altLength = alt.length();
        
        while (!ref.isEmpty() && !alt.isEmpty() && ref.charAt(0)==alt.charAt(0) && !ref.equals(alt)){
            if (ref.length() == 1){
                ref = "";
            }else{
                ref = ref.substring(1);
            }
            if (alt.length() == 1){
                alt = "";
            }else{
                alt = alt.substring(1);
            }            
            pos +=1;
            refLength -=1;
            altLength -=1;
        }
        
        Integer start = pos;
        Integer end = pos + Math.max(refLength, altLength) -1;
        
        List<String> civicEntries = new LinkedList();
        for (CivicEntry row: rows){
            if (row.getChrom().equalsIgnoreCase(chrom) && 
                row.getChrom2()==null && 
                ((row.getRef() !=null && !row.getRef().isEmpty()) || (row.getAlt()!=null && !row.getAlt().isEmpty())) &&
                row.getEnd()>=start && row.getStart()<=end){
                civicEntries.add(row.getLink());
            }
        }
        if (civicEntries.size()>0){
            transcript.put("CIVIC:Link", civicEntries);
        }
        
    }
    

}
