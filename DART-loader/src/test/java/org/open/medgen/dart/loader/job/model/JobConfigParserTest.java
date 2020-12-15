package org.open.medgen.dart.loader.job.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author mhaimel
 */
public class JobConfigParserTest {
    File configFile;
    File configTemplateFile;
    File configTemplateFile2;
    File configTemplateFile3;

    @Before
    public void setUp() throws Exception {
        configFile = new File(this.getClass().getResource("JobConfigTest.txt").getFile());
        configTemplateFile = new File(this.getClass().getResource("JobConfigTemplate.txt").getFile());
        configTemplateFile2 = new File(this.getClass().getResource("JobConfigTemplate2.txt").getFile());
        configTemplateFile3 = new File(this.getClass().getResource("JobConfigTemplate3.txt").getFile());
    }

    @Test
    public void getTemplateConfig() throws IOException {
        JobConfigParser parser = new JobConfigParser(configTemplateFile);
        assertTrue(parser.getConfigMap().containsKey(JobConfigParser.TEMPLATE_KEY));
        assertEquals(1, parser.getConfigMap().size());
        validateTemplate(parser);
    }

    @Test
    public void getTemplateConfigSkipAdditionalSample() throws IOException {
        JobConfigParser parser = new JobConfigParser(configTemplateFile2);
        assertTrue(parser.getConfigMap().containsKey(JobConfigParser.TEMPLATE_KEY));
        assertEquals(1, parser.getConfigMap().size());
        validateTemplate(parser);

        parser = new JobConfigParser(configTemplateFile3);
        assertTrue(parser.getConfigMap().containsKey(JobConfigParser.TEMPLATE_KEY));
        assertEquals(1, parser.getConfigMap().size());
        validateTemplate(parser);
    }

    public void validateTemplate(JobConfigParser job) {
        assertNotNull(job.getSampleConfig("does-not-exist"));
        JobConfigParser.SampleConfig sample = job.getSampleConfig("xxx");
        assertNull(sample.getSampleAlias());
        assertEquals("test-xxx-vcf", sample.getVcfUrl());
        assertEquals("test-xxx-bam", sample.getBamUrl());
        assertEquals("test-xxx-track", sample.getCoverageTrackUrl());
        assertEquals("test-xxx-coverage", sample.getCoverageFile());
    }


    @Test
    public void getSampleConfig() throws IOException {
        validate(new JobConfigParser(configFile));
        JobConfigParser parser = new JobConfigParser();
        parser.load(configFile);
        validate(parser);
    }

    public void validate(JobConfigParser job) {
        assertNull(job.getSampleConfig("does-not-exist"));
        JobConfigParser.SampleConfig sample = job.getSampleConfig("test");
        assertEquals("test_alias", sample.getSampleAlias());
        assertEquals("test-vcf", sample.getVcfUrl());
        assertEquals("test-bam", sample.getBamUrl());
        assertEquals("test-track", sample.getCoverageTrackUrl());
        assertEquals("test-coverage", sample.getCoverageFile());

        JobConfigParser.SampleConfig sample2 = job.getSampleConfig("test2");
        assertNull(sample2.getSampleAlias());
        assertEquals("test-vcf", sample.getVcfUrl());
        assertEquals("test-bam", sample.getBamUrl());
        assertEquals("test-track", sample.getCoverageTrackUrl());
        assertEquals("test-coverage", sample.getCoverageFile());
    }
}