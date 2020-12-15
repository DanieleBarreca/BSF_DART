/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.bed;

import org.open.medgen.dart.core.model.mongo.bed.BedEntry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.codec.binary.Hex;
import org.bson.types.ObjectId;

/**
 *
 * @author dbarreca
 */
public class BEDParser {

    public final static Integer BUCKET_SIZE = 1000000;

    public static ParsedBed parse(InputStream is, String bedFileName, String genome) throws BEDLoadingException {
        return parse(is, bedFileName, null, genome);
    }
    
    public static ParsedBed parse(InputStream is, String bedFileName, List<String> additionalFields, String genome) throws BEDLoadingException {
        
        ObjectId mongoID = ObjectId.get();
        
        List<BedEntry> result = new LinkedList();
        MessageDigest md5;
        try {
            md5= MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new BEDLoadingException("Cannot find digest MD5 algorithm", ex);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is));        
        try{
            String line;
            while ((line = br.readLine())!=null){
                md5.update(line.getBytes());
                if (line.startsWith("track")) continue;
                String[] splitLine = line.split("\t");
                if (splitLine.length<3){
                    throw new BEDLoadingException("Invalid line "+line+" in file"+bedFileName);
                }
                String chrom = splitLine[0];
                BedGenomeVersion.getCorrectedContig(genome,chrom);
                
                try{
                    Integer start = Integer.parseInt(splitLine[1]);
                    Integer end = Integer.parseInt(splitLine[2]);

                    Integer bucket = (int) (1.0*start/BUCKET_SIZE);
                    Integer finalBucket = (int) (1.0*end/BUCKET_SIZE);


                    while (bucket<=finalBucket){
                        Integer bucketEnd = (bucket+1)*BUCKET_SIZE-1;
                        if (bucketEnd<end){
                            BedEntry entry = new BedEntry (mongoID,chrom, start,bucketEnd, String.valueOf(bucket));
                            start=bucketEnd+1;
                            result.add(entry);
                        } else {
                            BedEntry entry = new BedEntry (mongoID,chrom, start,end, String.valueOf(bucket));
                            result.add(entry);
                            break;
                        }
                        bucket = bucket+1;
                    }


                    /*Integer endBucket = (int) (1.0*end/BUCKET_SIZE);
                    for (int i = startBucket; i<=endBucket; i++){
                        BedEntry entry = new BedEntry (mongoID,chrom, start,end, String.valueOf(i));
                        result.add(entry);
                    }*/

                }catch(NumberFormatException e){
                    throw new BEDLoadingException("Invalid line "+line+" in file"+bedFileName);
                }
            }
        } catch (IOException ex){
            throw new BEDLoadingException(ex);
        }finally{
            if (br!=null){
                try {
                    br.close();
                } catch (IOException ex) {}
            }
        }
        
        return new ParsedBed(result, Hex.encodeHexString(md5.digest()),mongoID );
    }
}
