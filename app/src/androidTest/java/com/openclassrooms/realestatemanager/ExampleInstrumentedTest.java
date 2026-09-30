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
    public void testInternetAvailableWithNullContext () {
        boolean result = Utils.isInternetAvailable(null);
        assertFalse(result);

    }

    @Test
    public void testInternetAvailableWithValidContextReturnTrue () {
        boolean result = Utils.isInternetAvailable(InstrumentationRegistry.getInstrumentation().getTargetContext());
        assertTrue(result);

    }

    @Test
    public void testInternetAvailableWithValidContextReturnFalse () {
        // Use PlaneMode to test this
        boolean result = Utils.isInternetAvailable(InstrumentationRegistry.getInstrumentation().getTargetContext());
        assertFalse(result);

    }

}
