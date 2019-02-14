package de.tuberlin.onedrivesdk;

import de.tuberlin.onedrivesdk.drive.DrivePermission;
import de.tuberlin.onedrivesdk.drive.DriveUser;
import de.tuberlin.onedrivesdk.drive.OneDrive;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.networking.OneDriveSession;
import de.tuberlin.onedrivesdk.shared.SharedItem;

import java.io.IOException;
import java.util.List;

/**
 * This interface provides the functionality of the OneDrive API.
 */
public interface OneDriveSDK {

    /**
     * Get user
     *
     * @return DriveUser default drive
     */
    DriveUser me() throws IOException, OneDriveException;

    /**
     * Get user's default drive on OneDrive.
     *
     * @return OneDrive default drive
     * @throws IOException
     * @throws IOException
     */
    OneDrive getDefaultDrive() throws IOException, OneDriveException;

    /**
     * Gets drive by the specified drive id.
     *
     * @param driveId the drive id
     * @return OneDrive
     * @throws IOException
     * @throws IOException
     */
    OneDrive getDrive(String driveId) throws IOException, OneDriveException;

    /**
     * Gets all drives of the user.
     *
     * @return List<OneDrive>
     * @throws IOException
     * @throws OneDriveException
     */
    List<OneDrive> getAllDrives() throws IOException, OneDriveException;

    /**
     * Gets the root folder of the default drive.
     *
     * @return root folder
     * @throws IOException
     * @throws IOException
     */
    OneFolder getRootFolder() throws IOException, OneDriveException;

    /**
     * Gets the root folder of the given dive.
     *
     * @param drive
     * @return root folder
     * @throws IOException
     * @throws IOException
     */
    OneFolder getRootFolder(OneDrive drive) throws IOException, OneDriveException;


    /**
     * Gets folder by id.
     *
     * @param id
     * @return OneFolder
     * @throws IOException
     * @throws IOException
     */
    OneFolder getFolderById(String id) throws IOException, OneDriveException;

    /**
     * Gets shared folder by id.
     *
     * @param driveId
     * @param id
     * @return OneFolder
     * @throws IOException
     * @throws IOException
     */
    OneFolder getRemoteFolderById(String driveId, String id) throws IOException, OneDriveException;


    /**
     * Gets folder by path.
     *
     * @param pathToFolder
     * @return OneFolder
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder getFolderByPath(String pathToFolder) throws IOException, OneDriveException;


    /**
     * Gets file by id.
     *
     * @param id
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    OneFile getFileById(String id) throws IOException, OneDriveException;


    /**
     * Gets shared file by id.
     *
     * @param id
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    OneFile getRemoteFileById(String driveId, String id) throws IOException, OneDriveException;


    /**
     * Gets permissions of a file  by id.
     *
     * @param id
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    List<DrivePermission> getItemPermission(String driveId, String id) throws IOException, OneDriveException;


    /**
     * Gets file by path.
     *
     * @param pathToFile
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    OneFile getFileByPath(String pathToFile) throws IOException, OneDriveException;


    /**
     * Gets folder by path.
     *
     * @param pathToFolder
     * @param drive
     * @return OneFolder
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder getFolderByPath(String pathToFolder, OneDrive drive) throws IOException, OneDriveException;

    /**
     * Gets all shared items by drive.
     *
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    List<SharedItem> getAllSharedItems() throws IOException, OneDriveException;

    /**
     * Gets all shared items by drive.
     *
     * @param drive
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    List<SharedItem> getAllSharedItems(OneDrive drive) throws IOException, OneDriveException;


    /**
     * Gets file by path.
     *
     * @param pathToFile
     * @param drive
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    OneFile getFileByPath(String pathToFile, OneDrive drive) throws IOException, OneDriveException;

    /**
     * Used to authorize the session with the OAuth Response Code (used for first authentication)
     *
     * @param oAuthCode the code from the OneDrive OAuth authentication process.
     * @throws IOException
     * @throws OneDriveException
     */
    void authenticate(String oAuthCode) throws IOException, OneDriveException;

    /**
     * Used to authorize the session with a RefreshToken
     *
     * @param refreshToken
     * @throws IOException
     * @throws OneDriveException
     */
    void authenticateWithRefreshToken(String refreshToken) throws IOException, OneDriveException;

    /**
     * Returns the RefreshToken of the Current Session, if any exists and
     * the current session is valid.
     *
     * A refresh token is only generated if @see OneDriveScope.OFFLINE_ACCESS has been requested for this
     * session.
     *
     * <b><big>WARNING</big> The RefreshToken is equivalent to a user password and should be stored/encrypted similarly. </b>
     * @return
     * @throws OneDriveException
     */
    String getRefreshToken() throws OneDriveException;

    /**
     * Disconnect from the current session.
     *
     * @throws IOException
     */
    void disconnect() throws IOException;

    /**
     * Returns the OneDrive oAuth URL.
     * @return url
     */
    String getAuthenticationURL();

    /**
     * Returns true if the session is authenticated.
     * @return is authenticated
     */
    boolean isAuthenticated();

    /**
     * used to start a thread that request a new authentication token
     *
     * @see OneDriveSession#refreshDelay
     * @see OneDriveSession#startRefreshThread()
     */
    void startSessionAutoRefresh();


    /**
     * Check if a response status is success
     *
     * @return boolean
     */
    boolean isResponseSuccess(int code);
}
