package de.tuberlin.onedrivesdk.uploadFile;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ConcreteOneUploadFileTest {

    public File fileToUploadPath;

    ConcreteOneDriveSDK mockApi;

    @Test
    public void getNextRange() throws InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchFieldException, SecurityException, OneDriveException {

        UploadSession upSession = getEmptyUploadSession();

        String[] nextRanges = {"1435-", "16843-65786", "547547-65756"};
        getUnaccessableField("nextExpectedRanges", UploadSession.class).set(upSession, nextRanges);

        assertEquals(1435L, upSession.getNextRange());


    }

    @After
    public void removeTestFile() {
        fileToUploadPath.delete();
    }

    @Before
    public void createTestFile() throws IOException {
        fileToUploadPath = File.createTempFile("TestOneSDKFile", "txt");
    }

    @Before
    public void createAPIMock() {
        mockApi = mock(ConcreteOneDriveSDK.class);
    }

    public static Field getUnaccessableField(String fieldName, Class<?> clazz)
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {

        Field privateField = clazz.getDeclaredField(fieldName);

        privateField.setAccessible(true);

        return privateField;

    }

    private static UploadSession getEmptyUploadSession() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // get constructor that takes a String as argument
        Constructor<UploadSession> constructor = (Constructor<UploadSession>) UploadSession.class
                .getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        UploadSession upSession = constructor.newInstance();
        return upSession;
    }

}
