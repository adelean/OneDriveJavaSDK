package de.tuberlin.onedrivesdk.shared;

public interface SharedItem {


    /**
     * Gets the id of the SharedItem.
     *
     * @return id
     */
    String getId();


    /**
     * Gets the name of the item.
     *
     * @return name
     */
    String getName();

    /**
     * Gets the size of this item in bytes.
     *
     * @return size
     */
    long getSize();

    /**
     * Gets the RemoteItem.
     *
     * @return RemoteItem
     */
    RemoteItem getRemoteItem();


    /**
     * Gets the raw JSON which is received from the OneDrive API.
     *
     * @return raw json
     */
    String getRawJson();

}
