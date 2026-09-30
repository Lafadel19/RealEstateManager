package com.openclassrooms.realestatemanager;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.openclassrooms.realestatemanager.utils.Utils;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.openclassrooms.realestatemanager", appContext.getPackageName());
    }

    @Test
    public void testInternetAvailableWithNullContext() {
        boolean result = Utils.isInternetAvailable(null);
        assertFalse(result);
    }

    @Test
    public void testInternetAvailableWithValidContext() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertNotNull(context);
    }
}
