/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.dto;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author dbarreca
 */
public class PermissionsDTO {
    
    private boolean isAdmin = false;
    private boolean canQueryVCF = false;
    private boolean canSavePreset = false;
    private boolean canSavePanel = false;
    private boolean canUploadVCF = false;
    private boolean canAnnotatePathogenicity = false;
    private boolean canValidateVariants = false;
    private boolean canSaveReport = false;
    private boolean isPublicUser = false;

    public boolean isCanQueryVCF() {
        return canQueryVCF;
    }

    public void setCanQueryVCF(boolean canQueryVCF) {
        this.canQueryVCF = canQueryVCF;
    }

    public boolean isCanSavePreset() {
        return canSavePreset;
    }

    public void setCanSavePreset(boolean canSavePreset) {
        this.canSavePreset = canSavePreset;
    }

    public boolean isCanSavePanel() {
        return canSavePanel;
    }

    public void setCanSavePanel(boolean canSavePanel) {
        this.canSavePanel = canSavePanel;
    }
    
    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isCanUploadVCF() {
        return canUploadVCF;
    }

    public void setCanUploadVCF(boolean canUploadVCF) {
        this.canUploadVCF = canUploadVCF;
    }

    public boolean isPublicUser() {
        return isPublicUser;
    }

    public void setPublicUser(boolean publicUser) {
        isPublicUser = publicUser;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isCanAnnotatePathogenicity() {
        return canAnnotatePathogenicity;
    }

    public void setCanAnnotatePathogenicity(boolean canAnnotatePathogenicity) {
        this.canAnnotatePathogenicity = canAnnotatePathogenicity;
    }

    public boolean isCanValidateVariants() {
        return canValidateVariants;
    }

    public void setCanValidateVariants(boolean canValidateVariants) {
        this.canValidateVariants = canValidateVariants;
    }

    public boolean isCanSaveReport() {
        return canSaveReport;
    }

    public void setCanSaveReport(boolean canSaveReport) {
        this.canSaveReport = canSaveReport;
    }
}
