package org.open.medgen.dart.loader.job.model;

import org.biojava.nbio.genome.parsers.gff.FeatureI;
import org.biojava.nbio.genome.parsers.gff.FeatureList;
import org.biojava.nbio.genome.parsers.gff.GFF3Reader;
import org.junit.Test;

import java.io.IOException;

public class GFF3ParserTest {

    private FeatureList features = GFF3Reader.read(getClass().getClassLoader().getResource("gencode.v29.basic.annotation_sample.gff3").getFile());

    public GFF3ParserTest() throws IOException {
    }

    @Test
    public void testParse() {
        for (FeatureI feature: features.selectByType("exon")) {
            System.out.println(feature);
            exploreParents(feature, 0);
        }
    }

    public void exploreParents(FeatureI feature, int level){
        if (feature.hasAttribute("Parent")) {
            String parentss = feature.getAttribute("Parent");
            String[] parents = parentss.split(",");
            for (String parent: parents){
                FeatureList parentFeatures = features.selectByGroup("ID="+parent);
                for (FeatureI parentFeature : parentFeatures){
                    System.out.println("Found parent of level "+level+" and type "+parentFeature.type() +": "+parentFeature.group());
                    exploreParents(parentFeature, level+1);
                }
            }
        }
    }

}
