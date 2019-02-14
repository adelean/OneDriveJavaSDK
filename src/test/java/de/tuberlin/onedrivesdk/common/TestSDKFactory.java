package de.tuberlin.onedrivesdk.common;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import org.junit.Assert;

import java.io.IOException;

public class TestSDKFactory {

    public static OneDriveSDK getInstance() {

        try {
            return ConcreteOneDriveSDK.createFromSession(SessionProvider.getSession());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return null;
        } catch (OneDriveException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return null;
        }
    }

}
