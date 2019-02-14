package de.tuberlin.onedrivesdk.downloadFile;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.file.OneFile;

import java.io.File;
import java.io.IOException;
/**
 * This Interface provides all Methods to handle a File download
 *
 */
public interface OneDownloadFile {

    /**
     * Gets the meta data of the downloaded file.
     *
     * @return meta data
     */
    OneFile getMetaData();

    /**
     * Starts Download, blocks until finished.
     *
     * @throws IOException
     */
    void startDownload(String driveId) throws IOException, OneDriveException;

    /**
     * Gets the file handel of the downloaded file.
     *
     * @return downloaded file
     */
    File getDownloadedFile();
}
