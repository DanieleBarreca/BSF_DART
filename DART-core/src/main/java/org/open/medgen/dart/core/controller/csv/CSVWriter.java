/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.csv;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dbarreca
 */
public class CSVWriter {

    private static final Logger LOG = Logger.getLogger(CSVWriter.class.getName());

    private final static String SEPARATOR = ",";
    private final static String LIST_SEPARATOR = ",";
    private final static String NEWLINE = "\n";
    private final static String QUOTE = "\"";

    protected static void writeCSV(OutputStream os, VCFFileDTO header, Iterator<VariantModel> variants, List<Integer> requestedFields) {
        BufferedWriter bw = null;

        try {
            List<VCFInfoDTO> fields = new LinkedList();
            
            if (requestedFields!=null && !requestedFields.isEmpty()){
                for (Integer fieldId: requestedFields) {
                    for (VCFInfoDTO field: header.getVcfFields()){
                        if (field.getId().equals(fieldId)){
                            fields.add(field);
                        }
                    }
                }
            }else{
                fields = header.getVcfFields();
            }
            
            bw = new BufferedWriter(new OutputStreamWriter(os));

            bw.write(getHeader(fields, false));
            bw.write(NEWLINE);
            //bw.write(getHeader(header, true));
            //bw.write(NEWLINE);

            while (variants.hasNext()) {
                bw.write(getLine(variants.next(), fields));
                bw.write(NEWLINE);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "ERROR while generating CSV file", e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
            }
        }

    }

    private static String getHeader(List<VCFInfoDTO> fields, boolean desc) {
        StringBuilder headerLine = new StringBuilder();
        boolean first = true;
        for (VCFInfoDTO field : fields) {
            if (!first) {
                headerLine.append(SEPARATOR);
            } else {
                first = false;
            }

            headerLine.append(QUOTE);
            if (desc) {
                headerLine.append(field.getDescription());
            } else {
                headerLine.append(field.getDisplayName());
            }
            headerLine.append(QUOTE);

        }

        return headerLine.toString();
    }

    private static String getLine(VariantModel variant, List<VCFInfoDTO> fields) {
        StringBuilder line = new StringBuilder();
        boolean first = true;
        for (VCFInfoDTO field : fields) {

            if (!first) {
                line.append(SEPARATOR);
            } else {
                first = false;
            }
            line.append(getAsString(variant.getAttribute(field.getFieldPath())));

        }

        return line.toString();
    }

    private static String getAsString(Object attribute) {
        StringBuilder result = new StringBuilder(QUOTE);
        if (attribute == null) {
            result.append("");
        } else {

            if (attribute instanceof List) {
                boolean isFirstElement = true;
                for (Object subAttribute : ((List) attribute)) {
                    if (subAttribute != null) {
                        if (!isFirstElement) {
                            result.append(LIST_SEPARATOR);
                        } else {
                            isFirstElement = false;
                        }
                        result.append(subAttribute.toString());
                    }
                }
            } else {
                result.append(attribute.toString());
            }
        }
        return result.append(QUOTE).toString();
    }
}
