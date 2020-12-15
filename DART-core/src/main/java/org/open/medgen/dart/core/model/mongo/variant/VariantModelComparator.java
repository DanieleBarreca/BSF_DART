package org.open.medgen.dart.core.model.mongo.variant;

import java.util.*;

public class VariantModelComparator implements Comparator<VariantModel> {


    private String attribute = null;
    private boolean ascending = true;

    public VariantModelComparator() {
    }

    public VariantModelComparator(boolean ascending) {
        this.ascending = ascending;
    }

    public VariantModelComparator(String attribute, boolean ascending) {
        this.attribute = attribute;
        this.ascending = ascending;
    }

    private int compareNoAttribute(VariantModel o1, VariantModel o2) {

        int result = 0;

        if (o1.getChromosome().equals(o2.getChromosome())){
            result = o1.getPosition().compareTo(o2.getPosition());
        }else{
            result = (o1.getChromosome().compareTo(o2.getChromosome()));
        }

        return this.ascending ? result : -result;
    }

    private int compareWithAttribute(VariantModel o1, VariantModel o2) {

        int result = 0;

        Object att1 = o1.getAttribute(attribute);
        Object att2 = o2.getAttribute(attribute);

        if (att1 == null) {
            if (att2 != null) {
                result = -1;
            }
        } else if (att2 == null) {
            result = 1;
        } else {
            if (att1 instanceof Set){
                att1 = new LinkedList<>((Set) att1);
                att2 = new LinkedList<>((Set) att2);
            }

            if (att1 instanceof List) {
                if (((List) att1).isEmpty()) {
                    if (!((List) att2).isEmpty()) {
                        result = -1;
                    }
                } else if (((List) att2).isEmpty()) {
                    result = 1;
                } else {
                    if (ascending) {
                        Collections.sort((List) att1);
                        Collections.sort((List) att2);
                    } else {
                        Collections.sort((List) att1, Collections.reverseOrder());
                        Collections.sort((List) att2, Collections.reverseOrder());
                    }
                    Iterator iter1 = ((List) att1).iterator();
                    Iterator iter2 = ((List) att2).iterator();

                    Integer partialResult = null;
                    while (iter1.hasNext() & iter2.hasNext()) {
                        Object el1 = iter1.next();
                        Object el2 = iter2.next();
                        if (el1 == null){
                            if (el2==null){
                                continue;
                            }else{
                                result = -1;
                                break;
                            }
                        }else if (el2==null){
                            result = 1;
                            break;
                        }
                        if (el1 instanceof Comparable) {
                            partialResult = ((Comparable) el1).compareTo(el2);
                            if (partialResult == 0) {
                                partialResult = null;
                            } else {
                                break;
                            }
                        }
                    }

                    if (partialResult == null) {
                        if (iter1.hasNext()) {
                            result = 1;
                        } else if (iter2.hasNext()) {
                            result = -1;
                        }
                    } else {
                        result = partialResult;
                    }
                }
            } else if (att1 instanceof Comparable) {
                result = ((Comparable) att1).compareTo(att2);
            }

        }

        return this.ascending ? result : -result;
    }


    @Override
    public int compare(VariantModel o1, VariantModel o2) {

        int result;

        if (attribute==null){
            result = this.compareNoAttribute(o1,o2);
        }else {
            result = this.compareWithAttribute(o1,o2);
        }

        if (result==0){
            result= o1.getId().compareTo(o2.getId());
        }

        return  result;
    }
}
