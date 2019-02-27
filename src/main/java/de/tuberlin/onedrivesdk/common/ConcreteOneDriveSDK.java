package de.tuberlin.onedrivesdk.common;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.drive.ConcreteOneDrive;
import de.tuberlin.onedrivesdk.drive.DrivePermission;
import de.tuberlin.onedrivesdk.drive.DriveUser;
import de.tuberlin.onedrivesdk.drive.OneDrive;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.ConcreteOneFolder;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.networking.ConcreteOneResponse;
import de.tuberlin.onedrivesdk.networking.OneDriveSession;
import de.tuberlin.onedrivesdk.networking.OneResponse;
import de.tuberlin.onedrivesdk.networking.PreparedRequest;
import de.tuberlin.onedrivesdk.networking.PreparedRequestMethod;
import de.tuberlin.onedrivesdk.shared.ConcreteSharedItem;
import de.tuberlin.onedrivesdk.shared.SharedItem;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides the functionality to authenticate to OneDrive and handles the communication.
 */
public class ConcreteOneDriveSDK implements OneDriveSDK {
    private static final Logger logger = LogManager.getLogger(OneDriveSession.class);
    private static final Gson gson = new Gson();

    private String baseUrl = "https://graph.microsoft.com/v1.0/";
    private OneDriveSession session;

    /**
     * Instantiates a new ConcreteOneDriveSDK.
     *
     * @param session
     */
    private ConcreteOneDriveSDK(OneDriveSession session) {
        this.session = session;

    }


    /**
     * Instantiates a new ConcreteOneDriveSDK.
     *
     * @param clientId
     * @param clientSecret
     * @param scopes
     * @return OneDriveSDK
     */
    public static OneDriveSDK createOneDriveConnection(String clientId, String clientSecret, String redirect_uri, ExceptionEventHandler handler, OneDriveScope[] scopes) {
        OkHttpClient cli = new OkHttpClient();
        cli.newBuilder().followRedirects(true);
        OneDriveSession session = OneDriveSession.initializeSession(cli, clientId, clientSecret, redirect_uri, scopes);
        return new ConcreteOneDriveSDK(session);
    }

    static OneDriveSDK createFromSession(OneDriveSession session) {
        return new ConcreteOneDriveSDK(session);
    }

    @Override
    public List<OneDrive> getAllDrives() throws IOException, OneDriveException {
        String requestURL = "drives/";

        PreparedRequest request = new PreparedRequest(requestURL, PreparedRequestMethod.GET);
        String json = this.makeRequest(request).getBodyAsString();
        List<OneDrive> drives = null;
        try {
            drives = ConcreteOneDrive.parseDrivesFromJson(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }

        for (OneDrive drive : drives) {
            ((ConcreteOneDrive) drive).setApi(this);
        }

        return drives;
    }

    @Override
    public DriveUser me() throws IOException, OneDriveException {
        String requestURL = "me";

        PreparedRequest request = new PreparedRequest(requestURL, PreparedRequestMethod.GET);
        String json = this.makeRequest(request).getBodyAsString();
        DriveUser me;
        try {
            me = DriveUser.parseFromJson(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }

        return me;
    }

    @Override
    public OneDrive getDefaultDrive() throws IOException, OneDriveException {
        String requestURL = "drive/";

        PreparedRequest request = new PreparedRequest(requestURL, PreparedRequestMethod.GET);

        String json = this.makeRequest(request).getBodyAsString();

        ConcreteOneDrive oneDrive = null;
        try {
            oneDrive = ConcreteOneDrive.fromJSON(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
        oneDrive.setApi(this);

        return oneDrive;
    }

    @Override
    public OneDrive getDrive(String driveId) throws IOException, OneDriveException {
        String requestURL = "drives/%s";

        PreparedRequest request = new PreparedRequest(String.format(requestURL, driveId), PreparedRequestMethod.GET);

        String json = this.makeRequest(request).getBodyAsString();

        ConcreteOneDrive oneDrive = null;
        try {
            oneDrive = ConcreteOneDrive.fromJSON(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
        oneDrive.setApi(this);

        return oneDrive;
    }

    @Override
    public OneFolder getFolderById(String id) throws IOException, OneDriveException {
        return getRemoteFolderById(null, id);
    }

    @Override
    public OneFolder getRemoteFolderById(String driveId, String id) throws IOException, OneDriveException {
        String requestURL;
        if (driveId == null) {
            requestURL = String.format("drive/items/%s", id);
        } else {
            requestURL = String.format("drives/%s/items/%s", driveId, id);
        }
        PreparedRequest request = new PreparedRequest(requestURL, PreparedRequestMethod.GET);

        String json = this.makeRequest(request).getBodyAsString();
        ConcreteOneFolder oneFolder = null;
        try {
            oneFolder = ConcreteOneFolder.fromJSON(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
        oneFolder.setApi(this);

        return oneFolder;
    }

    @Override
    public OneFolder getFolderByPath(String pathToFolder) throws IOException, OneDriveException {
        return getFolderByPath(pathToFolder, null);
    }

    @Override
    public OneFolder getRootFolder() throws IOException, OneDriveException {
        return getRootFolder(null);
    }

    @Override
    public OneFolder getRootFolder(OneDrive drive) throws IOException, OneDriveException {
        PreparedRequest request;
        if (drive == null) {
            request = new PreparedRequest("drive/root", PreparedRequestMethod.GET);
        } else {
            request = new PreparedRequest(String.format("drives/%s/root/", drive.getId()), PreparedRequestMethod.GET);
        }

        String json = this.makeRequest(request).getBodyAsString();

        ConcreteOneFolder oneFolder = null;
        try {
            oneFolder = ConcreteOneFolder.fromJSON(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
        oneFolder.setApi(this);

        return oneFolder;
    }

    @Override
    public OneFile getFileById(String id) throws IOException, OneDriveException {
        return getRemoteFileById(null, id);
    }

    @Override
    public OneFile getRemoteFileById(String driveId, String id) throws IOException, OneDriveException {
        String requestURL;
        if (driveId == null) {
            requestURL = String.format("drive/items/%s", id);
        } else {
            requestURL = String.format("drives/%s/items/%s", driveId, id);
        }

        PreparedRequest request = new PreparedRequest(requestURL, PreparedRequestMethod.GET);

        String json = this.makeRequest(request).getBodyAsString();
        ConcreteOneFile file = null;
        try {
            file = ConcreteOneFile.fromJSON(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
        file.setApi(this);
        return file;
    }

    @Override
    public List<DrivePermission> getItemPermission(String driveId, String id) throws IOException, OneDriveException {
        String requestURL;
        if (driveId == null) {
            requestURL = String.format("drive/items/%s/permissions", id);
        } else {
            requestURL = String.format("drives/%s/items/%s/permissions", driveId, id);
        }

        PreparedRequest request = new PreparedRequest(requestURL, PreparedRequestMethod.GET);
        String json = this.makeRequest(request).getBodyAsString();
        JsonObject response = gson.fromJson(json, JsonObject.class);
        List<DrivePermission> drivePermissions = new ArrayList<>();
        JsonArray items = response.getAsJsonArray("value");
        for (JsonElement item : items) {
            drivePermissions.add(gson.fromJson(item.toString(), DrivePermission.class));
        }
        return drivePermissions;
    }

    @Override
    public OneFile getFileByPath(String pathToFile) throws IOException, OneDriveException {
        return getFileByPath(pathToFile, null);
    }

    @Override
    public OneFolder getFolderByPath(String pathToFolder, OneDrive drive) throws IOException, OneDriveException {

        String requestURL = (drive == null) ? "drive" : String.format("drives/%s", drive.getId());
        requestURL += "/%s";

        PreparedRequest request = new PreparedRequest(String.format(requestURL, this.convertPathToApiPath(pathToFolder)), PreparedRequestMethod.GET);

        String json = this.makeRequest(request).getBodyAsString();
        ConcreteOneFolder oneFolder = null;
        try {
            oneFolder = ConcreteOneFolder.fromJSON(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
        oneFolder.setApi(this);

        return oneFolder;
    }

    @Override
    public List<SharedItem> getAllSharedItems() throws IOException, OneDriveException {
        return this.getAllSharedItems(null);
    }

    @Override
    public List<SharedItem> getAllSharedItems(OneDrive drive) throws IOException, OneDriveException {
        String requestURL = (drive == null) ? "me/drive" : String.format("drives/%s", drive.getId());
        requestURL += "/%s";

        PreparedRequest request = new PreparedRequest(String.format(requestURL, "sharedWithMe"), PreparedRequestMethod.GET);
        String json = this.makeRequest(request).getBodyAsString();
        JsonObject response = gson.fromJson(json, JsonObject.class);
        JsonArray sharedItems = response.getAsJsonArray("value");
        List<SharedItem> listSharedItems = new ArrayList<>();
        for (JsonElement concreteSharedItem : sharedItems) {
            ConcreteSharedItem sharedItem = null;
            try {
                sharedItem = ConcreteSharedItem.fromJSON(concreteSharedItem.toString());
            } catch (ParseException e) {
                throw new OneDriveException("API - response could not be processed", e);
            }
            sharedItem.setApi(this);
            listSharedItems.add(sharedItem);
        }
        return listSharedItems;
    }

    @Override
    public OneFile getFileByPath(String pathToFile, OneDrive drive) throws IOException, OneDriveException {
        String requestURL = (drive == null) ? "drive" : String.format("drives/%s", drive.getId());
        requestURL += "/%s";

        PreparedRequest request = new PreparedRequest(String.format(requestURL, this.convertPathToApiPath(pathToFile)), PreparedRequestMethod.GET);

        String json = this.makeRequest(request).getBodyAsString();
        ConcreteOneFile file = null;
        try {
            file = ConcreteOneFile.fromJSON(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
        file.setApi(this);

        return file;
    }

    @Override
    public void authenticate(String oAuthCode) throws IOException, OneDriveException {
        OneDriveSession.authorizeSession(this.session, oAuthCode);
    }

    @Override
    public void authenticateWithRefreshToken(String refreshToken) throws IOException, OneDriveException {
        OneDriveSession.refreshSession(this.session, refreshToken);
    }

    @Override
    public String getRefreshToken() throws OneDriveException {
        if (!this.session.isAuthenticated() || this.session.getRefreshToken() == null
                || this.session.getRefreshToken().isEmpty()) {
            throw new OneDriveException("can not return a valid refreshToken");
        }
        return this.session.getRefreshToken();
    }

    @Override
    public void disconnect() throws IOException {
        this.session.terminate();
    }

    @Override
    public String getAuthenticationURL() {
        return session.getAccessURL();
    }

    @Override
    public boolean isAuthenticated() {
        return session.isAuthenticated();
    }

    /**
     * Gets all children of the given folder depending on the type.
     *
     * @param concreteOneFolder
     * @param type
     * @return children
     * @throws IOException
     * @throws OneDriveException
     */
    public List<OneItem> getChildren(ConcreteOneFolder concreteOneFolder, OneItemType type) throws IOException, OneDriveException {
        return getRemoteChildren(null, concreteOneFolder, type);
    }

    /**
     * Gets all children of the given folder depending on the type.
     *
     * @param concreteOneFolder
     * @param type
     * @return children
     * @throws IOException
     * @throws OneDriveException
     */
    public List<OneItem> getRemoteChildren(String driveId, ConcreteOneFolder concreteOneFolder, OneItemType type) throws IOException, OneDriveException {
        String requestURL;
        if (driveId == null) {
            requestURL = String.format("drive/items/%s/children", concreteOneFolder.getId());
        } else {
            requestURL = String.format("drives/%s/items/%s/children", driveId, concreteOneFolder.getId());
        }
        PreparedRequest request = new PreparedRequest(requestURL, PreparedRequestMethod.GET);
        String json = this.makeRequest(request).getBodyAsString();

        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, type);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
        for (OneItem item : items) {
            item.setApi(this);
        }
        return items;
    }

    /**
     * Gets all child folder of the specified folder.
     *
     * @param concreteOneFolder
     * @return children
     * @throws IOException
     * @throws OneDriveException
     */
    public List<OneFolder> getChildFolder(ConcreteOneFolder concreteOneFolder) throws IOException, OneDriveException {
        return getRemoteChildFolder(null, concreteOneFolder);
    }

    /**
     * Gets all child files of the specified folder.
     *
     * @param concreteOneFolder
     * @return children
     * @throws IOException
     * @throws OneDriveException
     */
    public List<OneFile> getChildFiles(ConcreteOneFolder concreteOneFolder) throws IOException, OneDriveException {
        return getRemoteChildFiles(null, concreteOneFolder);
    }

    /**
     * Gets all child folder of the specified folder.
     *
     * @param concreteOneFolder
     * @return children
     * @throws IOException
     * @throws OneDriveException
     */
    public List<OneFolder> getRemoteChildFolder(String driveId, ConcreteOneFolder concreteOneFolder) throws IOException, OneDriveException {
        List<OneFolder> folder = new ArrayList<>();
        for (OneItem item : this.getRemoteChildren(driveId, concreteOneFolder, OneItemType.FOLDER)) {
            folder.add((OneFolder) item);
        }

        return folder;
    }

    /**
     * Gets all child files of the specified folder.
     *
     * @param concreteOneFolder
     * @return children
     * @throws IOException
     * @throws OneDriveException
     */
    public List<OneFile> getRemoteChildFiles(String driveId, ConcreteOneFolder concreteOneFolder) throws IOException, OneDriveException {
        List<OneFile> files = new ArrayList<>();
        for (OneItem item : this.getRemoteChildren(driveId, concreteOneFolder, OneItemType.FILE)) {
            files.add((OneFile) item);
        }

        return files;
    }

    /**
     * Perform the HTTP request to the OneDrive API with a json body.
     *
     * @param url
     * @param method
     * @param json   body of the request
     * @return OneResponse
     * @throws IOException
     */
    public OneResponse makeRequest(String url, PreparedRequestMethod method, String json) throws IOException, OneDriveException {
        PreparedRequest request = new PreparedRequest(url, method);
        request.addHeader("Content-Type", "application/json");
        request.setBody(json.getBytes());
        return makeRequest(request);
    }

    /**
     * Perform the HTTP request to the OneDrive API.
     *
     * @param preparedRequest
     * @return OneResponse
     * @throws IOException
     */
    public OneResponse makeRequest(PreparedRequest preparedRequest) throws IOException, OneDriveException {

        if (!this.session.isAuthenticated()) {
           // throw new OneDriveAuthenticationException("Session is no longer valid. Look for a failure of the refresh Thread in the log.");
            synchronized (session) {
                if (!this.session.isAuthenticated()) {
                    logger.info("Session is no longer valid. Try refresh session.");
                    this.session.refresh();
                }
            }
        }

        String url;
        RequestBody body = null;

        if (preparedRequest.getBody() != null) {
            body = RequestBody.create(null, preparedRequest.getBody());
        }

        if (isCompleteURL(preparedRequest.getPath())) {
            url = preparedRequest.getPath();
        } else {
            url = String.format("%s%s", this.baseUrl, preparedRequest.getPath());
        }
        preparedRequest.addHeader("Authorization", "Bearer " + session.getAccessToken());

        logger.debug(String.format("making request to %s", url));

        Request.Builder builder = new Request.Builder().method(preparedRequest.getMethod(), body).url(url);

        for (String key : preparedRequest.getHeader().keySet()) {
            builder.addHeader(key, preparedRequest.getHeader().get(key));
        }

        //Add auth permanently to header with redirection
        builder.header("Authorization", "bearer " + session.getAccessToken());
        Request request = builder.build();

        Response getDrivesResponse = session.getClient().newCall(request).execute();
        return new ConcreteOneResponse(getDrivesResponse);
    }

    /**
     * Create a new folder in OneDrive.
     *
     * @param folder the parent folder
     * @param name   name of the new folder
     * @return OneFolder the newly created folder
     * @throws IOException
     * @throws OneDriveException
     */
    public OneFolder createFolder(OneFolder folder, String name) throws IOException, OneDriveException {
        return createFolder(folder, name, ConflictBehavior.RENAME);
    }

    /**
     * Create a new folder in OneDrive and define the behavior on folder name conflict.
     *
     * @param folder   the parent folder
     * @param name
     * @param behavior
     * @return OneFolder the newly created folder
     * @throws IOException
     * @throws OneDriveException
     */
    public OneFolder createFolder(OneFolder folder, String name, ConflictBehavior behavior) throws IOException, OneDriveException {
        String requestURL = String.format("drive/items/%s/children", folder.getId());

        String createFolderJson = "{\"name\": \"" + name + "\", \"folder\": { }, \"@microsoft.graph.conflictBehavior\": \"" + behavior.name + "\"}";

        OneResponse response = makeRequest(requestURL, PreparedRequestMethod.POST, createFolderJson);

        if (isResponseSuccess(response.getStatusCode())) {
            ConcreteOneFolder createdFolder = null;
            try {
                createdFolder = ConcreteOneFolder.fromJSON(response.getBodyAsString());
            } catch (ParseException e) {
                throw new OneDriveException("API - response could not be processed", e);
            }
            createdFolder.setApi(this);
            return createdFolder;
        } else {
            throw new OneDriveException(response.toString());
        }

    }

    /**
     * Checks if the given URL is a syntactic correct url.
     *
     * @param url
     * @return boolean
     */
    private boolean isCompleteURL(String url) {
        try {
            URL u = new URL(url); // this would check for the protocol
            u.toURI();// does the extra checking required for validation of URI
        } catch (URISyntaxException | MalformedURLException e) {
            // if exception then no url
            return false;
        }
        return true;
    }

    /**
     * Deletes a OneDriveItem form OneDrive.
     *
     * @param oneItem to delete
     * @return true on success
     * @throws IOException
     * @throws OneDriveException
     */
    public boolean deleteItem(OneItem oneItem) throws IOException, OneDriveException {
        String requestURL = String.format("drive/items/%s", oneItem.getId());

        PreparedRequest request = new PreparedRequest(requestURL, PreparedRequestMethod.DELETE);
        OneResponse response = this.makeRequest(request);

        if (response.getStatusCode() == 204) {
            return true;
        } else {
            throw new OneDriveException(response.toString());
        }
    }

    /**
     * Converts a path to api specific path.
     * "/" -> "root/"
     * "/folder" -> "root:/folder:/"
     *
     * @param path
     * @return String
     */
    private String convertPathToApiPath(String path) {
        if (path.equals("/")) {
            return "root/";
        } else {
            path = this.removeSlashes(path);
            return "root:/" + path + ":/";
        }
    }

    /**
     * Remove slashes from the beginning and the end.
     *
     * @param path
     * @return String
     */
    private String removeSlashes(String path) {
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }


    /**
     * Download a file from OneDrive by id and returns the byte[].
     *
     * @param fileID the OneDrive file id
     * @return byte[]
     * @throws IOException
     */
    public byte[] download(String fileID) throws IOException, OneDriveException {
        return downloadRemote(null, fileID);
    }

    /**
     * Download a file from OneDrive by id and returns the byte[].
     *
     * @param fileID the OneDrive file id
     * @return byte[]
     * @throws IOException
     */
    public byte[] downloadRemote(String driveId, String fileID) throws IOException, OneDriveException {
        session.getClient().newBuilder().followRedirects(true);
        String url;
        if (driveId == null) {
            url = String.format("drive/items/%s/content", fileID);
        } else {
            url = String.format("drives/%s/items/%s/content", driveId, fileID);

        }

        PreparedRequest downloadRequest = new PreparedRequest(url, PreparedRequestMethod.GET);
        OneResponse getResponse = makeRequest(downloadRequest);

        return getResponse.getBodyAsBytes();
    }

    /**
     * Copy a file in OneDrive to a location in OneDrive.
     *
     * @param id            OneDrive item id of the file to be copied
     * @param destinationId id of the target folder
     * @return OneFile the copied file
     * @throws IOException
     * @throws OneDriveException
     * @throws ParseException
     * @throws InterruptedException
     */
    public OneFile copy(String id, String destinationId) throws IOException, OneDriveException, ParseException, InterruptedException {
        return this.copy(id, destinationId, null);
    }

    /**
     * Copy and rename a file in OneDrive to a location in OneDrive.
     *
     * @param id            OneDrive item id of the file to be copied
     * @param destinationId id of the target folder
     * @param newName       the new name of the copied file
     * @return OneFile the copied file
     * @throws IOException
     * @throws OneDriveException
     * @throws ParseException
     * @throws InterruptedException
     */
    public OneFile copy(String id, String destinationId, String newName) throws IOException, OneDriveException, ParseException, InterruptedException {
        ParentReference reference = new ParentReference();
        reference.setId(destinationId);

        OneDestinationItem destination = new OneDestinationItem(reference);

        if (newName != null)
            destination.setName(newName);

        String url = String.format("drive/items/%s/copy", id);
        String json = gson.toJson(destination);

        PreparedRequest request = new PreparedRequest(url, PreparedRequestMethod.POST);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Prefer", "respond-async");
        request.setBody(json.getBytes());

        OneResponse response = this.makeRequest(request);
        if (response.getStatusCode() != 202) {
            OneDriveError error = gson.fromJson(response.getBodyAsString(), OneDriveError.class);
            throw new OneDriveException("Request error: " + response.getStatusCode() + " " + error);
        } else {
            int tries = 0;
            do {
                tries++;
                try {
                    return this.getFileByPath(this.getFolderById(destinationId).getFullPath() + "/" + this.getFileById(id).getName());
                } catch (OneDriveException ignored) {

                }
                if (tries == 10) {
                    throw new OneDriveException("Failed to retrieve copied file");
                }
                Thread.sleep(500);
            } while (true);
        }
    }

    /**
     * Move a file in OneDrive.
     *
     * @param id            OneDrive item id of the file to be moved
     * @param destinationId id of the target folder
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     * @throws ParseException
     * @throws InterruptedException
     */
    public OneFile move(String id, String destinationId) throws IOException, OneDriveException, ParseException, InterruptedException {
        ParentReference reference = new ParentReference();
        reference.setId(destinationId);

        OneDestinationItem destination = new OneDestinationItem(reference);

        String url = String.format("drive/items/%s", id);
        String json = gson.toJson(destination);

        PreparedRequest request = new PreparedRequest(url, PreparedRequestMethod.PATCH);
        request.addHeader("Content-Type", "application/json");
        request.setBody(json.getBytes());

        OneResponse response = this.makeRequest(request);
        if (response.getStatusCode() != 200) {
            OneDriveError error = gson.fromJson(response.getBodyAsString(), OneDriveError.class);
            throw new OneDriveException("Request error: " + response.getStatusCode() + " " + error);
        }

        return (OneFile) ConcreteOneFile.fromJSON(response.getBodyAsString()).setApi(this);
    }

    @Override
    public void startSessionAutoRefresh() {
        this.session.startRefreshThread();
    }


    @Override
    public boolean isResponseSuccess(int code) {
        return code >= 200 && code <= 299;
    }
}
