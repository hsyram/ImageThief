package com.idtmessaging.imagethief;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.Beta;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.intent.IntentMonitorRegistry;

import com.idtmessaging.imagethief.reactive.Updatable;
import com.idtmessaging.imagethief.util.ImageModel;
import com.idtmessaging.imagethief.util.ImageMutableRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.any;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private CountDownLatch latch;

    @Before
    public void init(){
        latch = new CountDownLatch(1);

    }


    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Test
    public void testImageServiceSuccess() throws TimeoutException {

        ImageMutableRepository.getInstance().addUpdatable(new Updatable() {
            @Override
            public void update() {
                ImageModel response = ImageMutableRepository.getInstance().get();
                assertNotNull(response);
                assertTrue(response.isSuccess());
                ImageMutableRepository.getInstance().removeUpdatable(this);
                latch.countDown();
            }
        });

        ImageThiefService.startDownload(InstrumentationRegistry.getTargetContext(), "http://www.bensound.com/bensound-img/november.jpg");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @After
    public void testImageServiceFailed() throws TimeoutException {

        ImageMutableRepository.getInstance().addUpdatable(new Updatable() {
            @Override
            public void update() {
                ImageModel response = ImageMutableRepository.getInstance().get();
                assertNotNull(response);
                assertTrue(!response.isSuccess());
                ImageMutableRepository.getInstance().removeUpdatable(this);
                latch.countDown();
            }
        });

        ImageThiefService.startDownload(InstrumentationRegistry.getTargetContext(), "123");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
