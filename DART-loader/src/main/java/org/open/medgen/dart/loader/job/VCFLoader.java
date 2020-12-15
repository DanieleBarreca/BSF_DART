/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.job;

import org.apache.commons.collections4.ListUtils;
import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.permission.EntityNotFoundException;
import org.open.medgen.dart.core.controller.utils.ChecksumException;
import org.open.medgen.dart.core.controller.utils.FileDigester;
import org.open.medgen.dart.core.controller.vcf.VCFInsertController;
import org.open.medgen.dart.core.controller.vcf.VCFInsertControllerBean;
import org.open.medgen.dart.core.controller.vcf.VCFLoadingException;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;
import org.open.medgen.dart.loader.job.model.CoverageFileParser;
import org.open.medgen.dart.loader.job.model.JobConfigParser;
import org.open.medgen.dart.loader.parser.ParserInitializationException;
import org.open.medgen.dart.loader.parser.VCFParser;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.open.medgen.dart.loader.parser.vep_config.VEPPreferences;

/**
 *
 * @author dbarreca
 */              
public class VCFLoader {
    
    public static void main(String[] args){
        
        int readThreads = 2;
        int writeThreads = 12;
        
        ExecutorService readExecutor = Executors.newFixedThreadPool(readThreads);
        ExecutorService writeExecutor = Executors.newFixedThreadPool(writeThreads);

        Options options = new Options();
        
        Option help = Option.builder("help").argName("help").desc("Prints this help").build();
        
        Option connectionStringOpt = Option.builder("server").argName("server").desc("Connection String (username:password@domain:port) to the DART serves").hasArg().build();

        Option typeOpt = Option.builder("type").argName("type").desc("Special VCF Type String: GERMLINE or SOMATIC or SV").hasArg().build();
        Option configFileOpt = Option.builder("config").argName("config").desc("Path to sample configuration file").hasArg().build();
        Option vcfFileOpt = Option.builder("VCF").argName("VCF").desc("The VCF file to be loaded").required(true).hasArg().build();
        Option geneFieldOption =  Option.builder("geneField").argName("geneField").desc("CSQ sub-field containing the gene name").required(false).hasArg().build();
        Option genomicChangeFieldOption =  Option.builder("genomicChangeField").argName("genomicChangeField").desc("CSQ sub-field containing HGVSg change").required(false).hasArg().build();
        Option codingChangeFieldOption =  Option.builder("codingChangeField").argName("codingChangeField").desc("CSQ sub-field containing HGVSc change").required(false).hasArg().build();
        Option userGroupOpt = Option.builder("group").argName("group").desc("User group to use").required(false).hasArg().build();
        Option vepEnumFile = Option.builder("vepEnum").argName("vepEnum").desc("VEP Enum configuration").required(false).hasArg().build();
        Option vepOutputFile = Option.builder("vepOutputFields").argName("vepOutputFields").desc("VEP Output Field configuration").required(false).hasArg().build();
        Option chromWhitelistOpt = Option.builder("chrom").argName("chrom").desc("Chromosomes to load (comma separated)").required(false).hasArgs().valueSeparator(',').build();
        Option staticContentFolderOpt = Option.builder("staticContentBase").argName("staticContentBase").desc("Static content base folder to serve local files").required(false).hasArgs().valueSeparator(',').build();
        
        options.addOption(help);
        options.addOption(typeOpt);
        options.addOption(configFileOpt);
        options.addOption(vcfFileOpt);
        options.addOption(geneFieldOption);
        options.addOption(genomicChangeFieldOption);
        options.addOption(codingChangeFieldOption);
        options.addOption(connectionStringOpt);
        options.addOption(userGroupOpt);
        options.addOption(vepEnumFile);
        options.addOption(vepOutputFile);
        options.addOption(chromWhitelistOpt);
        options.addOption(staticContentFolderOpt);
        
        
        HelpFormatter formatter = new HelpFormatter();
        
        CommandLineParser parser = new DefaultParser();
        
       
        Long started = System.currentTimeMillis();

        VEPPreferences.Builder vepBuilder = new VEPPreferences.Builder();

        AtomicBoolean isCompleted = new AtomicBoolean(false);
        
        try {
            CommandLine cl = parser.parse(options, args);
            
            if (cl.hasOption("help")) {
                formatter.printHelp("VCFLoader [OPTIONS]", options);
                return;
            }

            if (cl.hasOption(vepEnumFile.getOpt())) {
                vepBuilder.setEnumFile(new File(cl.getOptionValue(vepEnumFile.getOpt())));
            }
            if (cl.hasOption(vepOutputFile.getOpt())) {
                vepBuilder.setVepFieldsFile(new File(cl.getOptionValue(vepOutputFile.getOpt())));
            }
            VEPPreferences vepPreferences = vepBuilder.build();
            

            String connectionString = cl.getOptionValue("server");
            
            VCFInsertController remoteController;
            try {
                remoteController = lookupRemoteBean(new ConnectionString(connectionString));
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            String fileName = cl.getOptionValue("VCF");
            
            VCFType type = VCFType.GERMLINE;
            if (cl.hasOption("type")){
                type = VCFType.valueOf(cl.getOptionValue("type").toUpperCase());
                if (type == null){
                    System.out.println("Type not recognized: "+cl.getOptionValue("type"));
                    return;
                }
            }
            
            File sampleConfigFile= null;
            if (cl.hasOption("config")){
                sampleConfigFile =new File(cl.getOptionValue("config"));
                if (!sampleConfigFile.exists()){
                    throw new FileNotFoundException(String.format("Sample configuration file %s not found",sampleConfigFile.getAbsolutePath()));
                }else if (!sampleConfigFile.canRead()){
                    throw new FileNotFoundException(String.format("Sample configuration file %s not readable",sampleConfigFile.getAbsolutePath()));
                }
                System.out.println("Sample configuration: "+sampleConfigFile);
            }else{
                System.out.println("No configuration file specified...using defaults");
            }

            JobConfigParser jobConfig = new JobConfigParser(sampleConfigFile);
            
            String geneField = "SYMBOL";            
            if (cl.hasOption(geneFieldOption.getOpt())){
                geneField = cl.getOptionValue(geneFieldOption.getOpt());
            }

            String genomicChangeField = "HGVSg";
            if (cl.hasOption(genomicChangeFieldOption.getOpt())){
                genomicChangeField = cl.getOptionValue(genomicChangeFieldOption.getOpt());
            }

            String codingChangeField = "HGVSc";
            if (cl.hasOption(codingChangeFieldOption.getOpt())){
                codingChangeField = cl.getOptionValue(codingChangeFieldOption.getOpt());
            }
            
            String userGroup = null;
            if (cl.hasOption(userGroupOpt.getOpt())){
                userGroup = cl.getOptionValue(userGroupOpt.getOpt());
            }

            final List<String> chromWhiteList;
            if (cl.hasOption(userGroupOpt.getOpt())){
                chromWhiteList = Arrays.asList(cl.getOptionValues(chromWhitelistOpt.getOpt()));
            }else{
                chromWhiteList = null;
            }

            final File staticContentFolder;
            if (cl.hasOption(staticContentFolderOpt.getOpt())){
                staticContentFolder = new File(cl.getOptionValue(staticContentFolderOpt.getOpt()));
                if (!staticContentFolder.exists() || !staticContentFolder.canRead()) {
                    throw new VCFLoadingException(String.format("Static content base folder %s was not found or is not readable",staticContentFolder.getAbsolutePath()));
                }
                System.out.println(String.format("STATIC CONTENT IS SERVED FROM : %s",staticContentFolder.getAbsolutePath()));
            }else{
                staticContentFolder = null;
            }
            
                    
            
            final File vcfFile;
            if (fileName.startsWith("/")) {
                vcfFile = new File(fileName);
            } else {
                vcfFile = new File(System.getProperty("user.dir"), fileName);
            }
            if (!vcfFile.exists()) {
                System.out.println("ERROR: File " + vcfFile.getAbsolutePath() + " does not exist");
                return;
            }
            
            String md5;
            try{
                md5 = FileDigester.md5Digest(vcfFile);
            }catch(ChecksumException e){
                Logger.getLogger(VCFLoader.class.getName()).log(Level.SEVERE, null, e);
                return;
            }
            
            System.out.println("File: " + vcfFile.getAbsolutePath());
            
            VCFFileReader reader =new VCFFileReader(vcfFile);
            VCFParser vcfParser;
            try{
                vcfParser = new VCFParser(vepPreferences, reader.getFileHeader(), vcfFile.getName(), type, jobConfig, geneField, genomicChangeField, codingChangeField, chromWhiteList,staticContentFolder);
            }catch(ParserInitializationException e){
                System.out.println("ERROR while initialization of Parser: "+e.getMessage());
                return;
            }
            VCFFileDTO header = vcfParser.getHeader();
            
            header.setMd5(md5);
            
            Integer theFile = remoteController.saveHeader(header,userGroup);
            if (theFile == null) throw new Exception("Got Null VCF id!");
            
            
            Runtime.getRuntime().addShutdownHook(new Thread( () -> {
                if (!isCompleted.get()){
                    try {
                        remoteController.finishLoadingeExceptionally(theFile, "Job interrupted before completion");
                    } catch (VCFLoadingException ex) {
                        Logger.getLogger(VCFLoader.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (AuthorizationException ex) {
                        Logger.getLogger(VCFLoader.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (EntityNotFoundException ex) {
                        Logger.getLogger(VCFLoader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("Interrupted before completion");
                }
                System.out.println("ShuttingDown...");
            }));
            
            AtomicBoolean isError = new AtomicBoolean(false);
            try{
                for (Map.Entry<String, String> sampleAlias: vcfParser.getSampleAlias().entrySet()){
                    JobConfigParser.SampleConfig sampleConfig = jobConfig.getSampleConfig(sampleAlias.getKey());
                    if (sampleConfig.getCoverageFile()!=null){
                        System.out.println("Parsing coverage file for sample "+ sampleAlias.getValue());
                        CoverageFileParser coverageFileParser = new CoverageFileParser(sampleConfig.getCoverageFile());
                        for (List<CoverageEntry> entries: ListUtils.partition(coverageFileParser.parse(),10000)){
                            if (chromWhiteList!=null){
                                entries = entries.stream().filter(coverageEntry -> chromWhiteList.contains(coverageEntry.getChrom()) ).collect(Collectors.toList());
                            }
                            if (!entries.isEmpty()) {
                                remoteController.addCoverageRegions(new LinkedList<>(entries), sampleAlias.getValue(), theFile);
                            }
                        }
                    }
                }

                BlockingQueue<List<VariantModel>> variantsToInsert = new LinkedBlockingQueue(50);

                AtomicBoolean isParsing = new AtomicBoolean(true);
                
                for (int i = 0; i < writeThreads; i++) {
                    writeExecutor.submit(() -> {
                        System.out.println("Starting Writing.");
                                                
                        while (!isError.get() && (isParsing.get() || !variantsToInsert.isEmpty())) {
                            try {
                                List<VariantModel> toInsert = variantsToInsert.poll(2,TimeUnit.SECONDS);
                                if (toInsert!=null && !toInsert.isEmpty()){
                                    toInsert.forEach((VariantModel variant) -> {
                                        variant.setVcfId(header.getMongoId());
                                    });
                                    remoteController.insertVariants(toInsert);
                                }
                            } catch (Exception | Error ex) {
                                Logger.getLogger(VCFLoader.class.getName()).log(Level.SEVERE, null, ex);
                                isError.set(true);
                                break;
                            }
                        }
                        System.out.println("Closing writing thread");
                    });
                }
                writeExecutor.shutdown();
                
                VCFFileReader.getSequenceDictionary(vcfFile);              
                
                AtomicInteger totalLines = new AtomicInteger();
                AtomicInteger remainingCapacity = new AtomicInteger();
                
                for (final SAMSequenceRecord seq: VCFFileReader.getSequenceDictionary(vcfFile).getSequences()) {
                    if (chromWhiteList==null || chromWhiteList.contains(seq.getSequenceName())) {
                        System.out.println("Starting parsing contig " + seq.getSequenceName());
                    }else{
                        System.out.println("Skipping contig " + seq.getSequenceName());
                        continue;
                    }
                    
                    readExecutor.submit(() -> {
                        
                        final VCFFileReader  vcfFileReader = new VCFFileReader(vcfFile, true);
                          
                        CloseableIterator<VariantContext> iter =  vcfFileReader.query(seq.getSequenceName(), 1, seq.getSequenceLength());
                        while (!isError.get() && iter.hasNext()) {
                            try {
                                List<VariantModel> parsedVariants = vcfParser.parseContext(iter.next());
                                remainingCapacity.addAndGet(variantsToInsert.remainingCapacity());
                                totalLines.incrementAndGet();
                                
                                variantsToInsert.put(parsedVariants);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(VCFLoader.class.getName()).log(Level.SEVERE, null, ex);
                                isError.set(true);
                                try{
                                    variantsToInsert.add(new LinkedList<>());
                                }catch(IllegalStateException e){}
                            }
                        }
                        
                        iter.close();
                        vcfFileReader.close();
                        
                        System.out.println("Finished parsing "+seq.getSequenceName());
                    });
                }
                readExecutor.shutdown();

                readExecutor.awaitTermination(2, TimeUnit.DAYS);
                System.out.println("Finished parsing");
                System.out.println("Average capacity "+remainingCapacity.get()*1.0/totalLines.get());
                isParsing.set(false);
                writeExecutor.awaitTermination(2, TimeUnit.DAYS);
                System.out.println("Finished writing");
                
            }catch(Exception| Error ex){
                Logger.getLogger(VCFLoader.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                
                if (isError.get()){
                    System.out.println("FINISHED EXCEPTIONALLY");
                    remoteController.finishLoadingeExceptionally(theFile, "Error while loading variants. Check local log");
                }else{
                    System.out.println("FINISHED");
                    remoteController.finishLoading(theFile);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("FINISHED EXCEPTIONALLY");
            formatter.printHelp("VCFLoader [OPTIONS] <FILE>", options);
        }finally{
            System.out.println("Took "+(System.currentTimeMillis()-started)/1000+"s");
            isCompleted.set(true);
        }
     
    }
    
     private static VCFInsertController lookupRemoteBean(ConnectionString connectionString) throws NamingException {
        final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://"+connectionString.domain+":"+connectionString.port.toString());
        jndiProperties.put(Context.SECURITY_PRINCIPAL, connectionString.userName);
        jndiProperties.put(Context.SECURITY_CREDENTIALS, connectionString.password);
        
        final Context context = new InitialContext(jndiProperties);
        final String beanName = VCFInsertControllerBean.class.getSimpleName();
        final String viewClassName = VCFInsertController.class.getName();
        final String name = "ejb:DART-package/DART-core/" + beanName + "!" + viewClassName;
        final Object lookup = context.lookup(name);
        return (VCFInsertController) lookup;
    }
     
    private static class ConnectionString {
        private final String userName;
        private final String password;
        private final String domain;
        private final Integer port;
        
        public ConnectionString(String theString) throws ParserInitializationException{
            if (theString == null || theString.isEmpty()) throw new ParserInitializationException("Malformed connection string");
            
            String[] split1 = theString.split("@");
            if (split1.length!=2) throw new ParserInitializationException("Malformed connection string");
            String[] split2 = split1[0].split(":");
            if (split2.length!=2 || split2[0].isEmpty() || split2[1].isEmpty()) throw new ParserInitializationException("Malformed connection string");
            this.userName = split2[0];
            this.password = split2[1];
            split2 = split1[1].split(":");
            if (split2.length!=2 || split2[0].isEmpty() || split2[1].isEmpty()) throw new ParserInitializationException("Malformed connection string");
            this.domain = split2[0];
            try{
                this.port = Integer.parseInt(split2[1]);
            }catch(NumberFormatException e){
                 throw new ParserInitializationException("Malformed connection string");
            }
                    
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public String getDomain() {
            return domain;
        }

        public Integer getPort() {
            return port;
        }
        
        
        
    }

}
