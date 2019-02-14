package de.tuberlin.onedrivesdk.download;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.common.TestSDKFactory;
import de.tuberlin.onedrivesdk.downloadFile.OneDownloadFile;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.shared.ConcreteSharedItem;
import de.tuberlin.onedrivesdk.shared.SharedItem;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConcreteOneDownloadSharePointIntegrationTest {

    @Test
    public void downloadSharedFiles() throws IllegalArgumentException, SecurityException, OneDriveException, IOException, InterruptedException {
        OneDriveSDK api = TestSDKFactory.getInstance();
        List<SharedItem> sharedItems = api.getAllSharedItems();
        Assert.assertEquals(1, sharedItems.size());
        Assert.assertEquals("FOLDER", sharedItems.get(0).getName());
        ConcreteSharedItem concreteSharedItem = (ConcreteSharedItem) sharedItems.get(0);
        OneFolder sharedFolder = api.getRemoteFolderById(concreteSharedItem.getRemoteItem().getParentReference().getDriveId(),
                concreteSharedItem.getRemoteItem().getId());

        List<OneFile> files = sharedFolder.getRemoteChildFiles(concreteSharedItem.getRemoteItem().getParentReference().getDriveId());
        Assert.assertEquals(51, files.size());

        downloadFiles(concreteSharedItem.getRemoteItem().getParentReference().getDriveId(), files);
    }

    private void downloadFiles(String driveId, List<OneFile> files) throws IllegalArgumentException, SecurityException, OneDriveException, IOException {
        String outPutDir = "target/";
        for (OneFile file : files) {

            String fileName=file.getName();
            File localCopy = new File(outPutDir + fileName);
            localCopy.createNewFile();
            //File localCopy = File.createTempFile(file.getName(), "");
            OneDownloadFile f = file.download(localCopy);
            f.startDownload(driveId);
            // HashCode code = Files.hash(localCopy, Hashing.sha1());
            // assertEquals(file.getName() + " mismatch", code.toString().toUpperCase(), file.getSHA1Hash());
        }
    }

}
