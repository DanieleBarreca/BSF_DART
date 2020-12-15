/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.parser.vep_config;

import java.util.Objects;

/**
 *
 * @author dbarreca
 */
public class OntologyTerm {
    private final String term;
    private final String description;
    private String accession;

    public OntologyTerm(String term, String description) {
        this.term = term;
        this.description = description;
        this.accession="";
    }

    public OntologyTerm(String term, String description, String accession) {
        this.term = term;
        this.description = description;
        this.accession = accession;
    }

    
    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getTerm() {
        return term;
    }

    public String getDescription() {
        return description;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.term);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OntologyTerm other = (OntologyTerm) obj;
        if (!Objects.equals(this.term, other.term)) {
            return false;
        }
        return true;
    }
    
    
    
}
