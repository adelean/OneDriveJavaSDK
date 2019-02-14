package de.tuberlin.onedrivesdk.shared;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.OneItem;
import org.json.simple.parser.ParseException;

public class ConcreteSharedItem extends OneItem implements SharedItem {
    RemoteItem remoteItem;

    public static ConcreteSharedItem fromJSON(String json) throws ParseException, OneDriveException {
        return (ConcreteSharedItem) OneItem.fromJSON(json).setRawJson(json);
    }

    @Override
    public RemoteItem getRemoteItem() {
        return remoteItem;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public String toString() {
        return getRawJson();
    }


}
