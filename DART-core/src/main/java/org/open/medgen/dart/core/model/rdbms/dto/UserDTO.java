/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 *
 * @author dbarreca
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    
    private Map<String, PermissionsDTO> permissions;

    public UserDTO(String userName, String firstName, String lastName, String email, Map<String, PermissionsDTO> permissions) {
        this.userName = userName;
        this.permissions = permissions;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserDTO() {
    }
    

    public String getUserName() {
        return userName;
    }

    public Map<String, PermissionsDTO> getPermissions() {
        return permissions;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
    
    

    
}
