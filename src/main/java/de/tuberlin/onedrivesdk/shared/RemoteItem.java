package de.tuberlin.onedrivesdk.shared;

import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.file.FileProperty;
import de.tuberlin.onedrivesdk.folder.FolderProperty;

public class RemoteItem extends OneItem {

    FolderProperty folder;
    FileProperty file;

    @Override
    public boolean isFile() {
        return file != null;
    }

    @Override
    public boolean isFolder() {
        return folder != null;
    }
}
