/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author dbarreca
 */
public class FileDigester {
    
     public static String md5Digest(File file, Integer bytesLimit) throws ChecksumException {
         try {
             MessageDigest md5 = MessageDigest.getInstance("MD5");
             return FileDigester.digest(file, md5,bytesLimit);
         } catch (NoSuchAlgorithmException ex) {
             throw new ChecksumException("MD5 Algorithm was not found");
         }
     }

    public static String md5Digest(File file ) throws ChecksumException {
         return FileDigester.md5Digest(file,null);
    }

    public static String digest(File file, MessageDigest md) throws ChecksumException{
         return FileDigester.digest(file,md,null);
    }
    
    public static String digest(File file, MessageDigest md, Integer bytesLimit) throws ChecksumException{
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new ChecksumException("File not found!");
        }
        
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        int tolalBytesRead = 0;
        
        try {
            while ((bytesCount = fis.read(byteArray)) != -1) {
                md.update(byteArray, 0, bytesCount);
                tolalBytesRead+=bytesCount;
                
                if (bytesLimit!=null && tolalBytesRead>bytesLimit){
                    break;
                }
            }
        } catch (IOException ex) {
            throw new ChecksumException(ex.getMessage(), ex);
        }
        
        byte[] bytes = md.digest();
        return Hex.encodeHexString(bytes);
    }
}
