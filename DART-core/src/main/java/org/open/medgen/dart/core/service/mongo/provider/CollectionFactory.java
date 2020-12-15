/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.provider;

import org.open.medgen.dart.core.model.mongo.MongoModel;
import org.open.medgen.dart.core.model.mongo.bed.BedEntry;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import java.util.Arrays;
import java.util.List;

/*
 * @author dbarreca
 */
public abstract class CollectionFactory {
    
    protected static final List<MongoModel> mongoModels = Arrays.asList(
            new VariantModel(),
            new BedEntry(),
            new CoverageEntry()
    );
     
    protected boolean isTest = false;
    
    public String getCollectionName(MongoModel mongoModel){
        
        return isTest ? "unitTest_"+mongoModel.getCollectionName() : mongoModel.getCollectionName();
    }
    
    public abstract String getCollectionName(Class clazz);
    

}
