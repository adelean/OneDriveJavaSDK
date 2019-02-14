package de.tuberlin.onedrivesdk.drive;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.OneDriveError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.simple.parser.ParseException;

/**
 * Data object for drive user
 */
public class DriveUser {
    private String id;
    private String email;
    private String displayName;

    public DriveUser(String displayName, String email, String id) {
        this.displayName = displayName;
        this.email = email;
        this.id = id;
    }

    public static DriveUser parseFromJson(String json) throws ParseException, OneDriveException {
        OneDriveError error;
        if ((error = OneDriveError.parseError(json)) != null) {
            throw new OneDriveException(error.toString());
        }

        Gson gson = new Gson();
        JsonObject userParsed = gson.fromJson(json, JsonObject.class);
        DriveUser driveUser = gson.fromJson(json, DriveUser.class);
        // driveUser mapping differs from grantedTo attribute from file permission and /me
        if (userParsed.get("mail").isJsonNull()) {
            if (!userParsed.get("userPrincipalName").isJsonNull()) {
                driveUser.email = userParsed.get("userPrincipalName").getAsString();
            }
        } else {
            driveUser.email = userParsed.get("mail").getAsString();
        }
        return driveUser;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Owner: " + displayName + " - " + email + " - " + id;
    }
}
