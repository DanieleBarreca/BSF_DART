package org.open.medgen.dart.loader.job.model;

import org.junit.Assert;
import org.junit.Test;
import org.open.medgen.dart.core.controller.vcf.VCFInsertController;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;

public class JobConfigTest {
    private ClassLoader classLoader = getClass().getClassLoader();
    private File jobConfigFile = new File(classLoader.getResource("jobConfig.tsv").getFile());
    private File jobConfigFileStar = new File(classLoader.getResource("jobConfig_star.tsv").getFile());


    @Test
    public void testParsingConfig() throws IOException {

        JobConfigParser configParser = new JobConfigParser(jobConfigFile);

        JobConfigParser.SampleConfig testSampleConfig = configParser.getSampleConfig("TEST");
        Assert.assertNotNull(testSampleConfig);
        Assert.assertEquals(testSampleConfig.getSample(),"TEST");
        Assert.assertTrue(testSampleConfig.getInclude());
        Assert.assertEquals(testSampleConfig.getVcfUrl(),"https://biomedical-sequencing.at/projects/BSA_0005_KBL_Exome_8c8598aa37884cf180592494d0546300/b37/variant_calling_split_cohort_vep_TEST.vcf.gz");
        Assert.assertEquals(testSampleConfig.getBamUrl(),"https://biomedical-sequencing.at/projects/BSA_0005_KBL_Exome_8c8598aa37884cf180592494d0546300/b37/variant_calling_process_sample_TEST_realigned.bam");
        Assert.assertEquals(testSampleConfig.getCoverageTrackUrl(),"https://biomedical-sequencing.at/projects/BSA_0005_KBL_Exome_8c8598aa37884cf180592494d0546300/b37/variant_calling_diagnose_sample_TEST_callable_loci.bb");
        Assert.assertEquals(testSampleConfig.getCoverageFile(),"/scratch/lab_bsf/projects/BSA_0005_KBL_Exome/b37/variant_calling_diagnose_sample_TEST_non_callable_regions.tsv");


        testSampleConfig = configParser.getSampleConfig("TEST2");
        Assert.assertNull(testSampleConfig);

        testSampleConfig = configParser.getSampleConfig("TEST3");
        Assert.assertNotNull(testSampleConfig);
        Assert.assertEquals(testSampleConfig.getSample(),"TEST3");
        Assert.assertTrue(testSampleConfig.getInclude());
        Assert.assertEquals(testSampleConfig.getVcfUrl(),"https://biomedical-sequencing.at/projects/BSA_0005_KBL_Exome_8c8598aa37884cf180592494d0546300/b37/variant_calling_split_cohort_vep_TEST3.vcf.gz");
        Assert.assertNull(testSampleConfig.getBamUrl());
        Assert.assertEquals(testSampleConfig.getCoverageTrackUrl(),"https://biomedical-sequencing.at/projects/BSA_0005_KBL_Exome_8c8598aa37884cf180592494d0546300/b37/variant_calling_diagnose_sample_TEST3_callable_loci.bb");
        Assert.assertEquals(testSampleConfig.getCoverageFile(),"/scratch/lab_bsf/projects/BSA_0005_KBL_Exome/b37/variant_calling_diagnose_sample_TEST3_non_callable_regions.tsv");


    }

    @Test
    public void testParsingConfigStar() throws IOException {

        JobConfigParser configParser = new JobConfigParser(jobConfigFileStar);

        JobConfigParser.SampleConfig testSampleConfig = configParser.getSampleConfig("TEST");
        Assert.assertNotNull(testSampleConfig);
        Assert.assertEquals(testSampleConfig.getSample(),"TEST");
        Assert.assertTrue(testSampleConfig.getInclude());
        Assert.assertEquals(testSampleConfig.getVcfUrl(),"https://biomedical-sequencing.at/projects/BSA_0005_KBL_Exome_8c8598aa37884cf180592494d0546300/b37/variant_calling_split_cohort_vep_TEST.vcf.gz");
        Assert.assertEquals(testSampleConfig.getBamUrl(),"https://biomedical-sequencing.at/projects/BSA_0005_KBL_Exome_8c8598aa37884cf180592494d0546300/b37/variant_calling_process_sample_TEST_realigned.bam");
        Assert.assertEquals(testSampleConfig.getCoverageTrackUrl(),"https://biomedical-sequencing.at/projects/BSA_0005_KBL_Exome_8c8598aa37884cf180592494d0546300/b37/variant_calling_diagnose_sample_TEST_callable_loci.bb");
        Assert.assertEquals(testSampleConfig.getCoverageFile(),"/scratch/lab_bsf/projects/BSA_0005_KBL_Exome/b37/variant_calling_diagnose_sample_TEST_non_callable_regions.tsv");

    }
}
