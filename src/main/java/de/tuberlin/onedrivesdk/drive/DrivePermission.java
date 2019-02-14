package de.tuberlin.onedrivesdk.drive;

import java.util.HashMap;
import java.util.Map;

public class DrivePermission {
    private String id;
    private String[] roles;
    private Map<String, DriveUser> grantedTo = new HashMap<>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public Map<String, DriveUser> getGrantedTo() {
        return grantedTo;
    }

    public void setGrantedTo(Map<String, DriveUser> grantedTo) {
        this.grantedTo = grantedTo;
    }

    public void addGrantedTo(String label, DriveUser grantedTo) {
        this.grantedTo.put(label, grantedTo);
    }
}
