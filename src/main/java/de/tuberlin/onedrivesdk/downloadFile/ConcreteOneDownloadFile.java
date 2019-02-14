package de.tuberlin.onedrivesdk.downloadFile;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.file.OneFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Implementation of OneDownloadFile
 * Blocking download operation
 */
public class ConcreteOneDownloadFile implements OneDownloadFile {

    private static final Logger logger = LogManager.getLogger(ConcreteOneDownloadFile.class);

    private final ConcreteOneFile metadata;
    private final ConcreteOneDriveSDK api;
    private final File destinationFile;

    public ConcreteOneDownloadFile(ConcreteOneFile metadata, ConcreteOneDriveSDK api, File destinationFile) throws FileNotFoundException {
        this.metadata = metadata;
        this.api = api;
        this.destinationFile = destinationFile;
    }

    @Override
    public OneFile getMetaData() {
        return metadata;
    }

    @Override
    public void startDownload(String driveId) throws IOException, OneDriveException {
        RandomAccessFile destination = new RandomAccessFile(this.destinationFile, "rw");
        try {
            logger.info("Starting download of " + metadata.getName());
            if (driveId == null) {
                destination.write(api.download(metadata.getId()));
            } else {
                destination.write(api.downloadRemote(driveId, metadata.getId()));
            }
        } finally {
            logger.info("Finished download of " + this.metadata.getName());
            try {
                destination.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public File getDownloadedFile() {
        return destinationFile;
    }
}
