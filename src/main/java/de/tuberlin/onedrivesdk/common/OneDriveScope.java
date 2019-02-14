package de.tuberlin.onedrivesdk.common;

/**
 * The different scopes for using the OneDrive API. These scopes are used in the authentication process.
 */
public enum OneDriveScope {
    USER_READ("user.read"),
    OFFLINE_ACCESS("offline_access"),
    READONLY("files.read.all"),
    READWRITE("files.readwrite.all");

    private String code;

    OneDriveScope(String s){
        this.code = s;
    }

    public String getCode(){
        return code;
    }
}
