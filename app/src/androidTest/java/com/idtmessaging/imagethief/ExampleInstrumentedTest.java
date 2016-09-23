package com.idtmessaging.imagethief;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 * very basic test for checking if the service is working correctly in both success and faild case
 *
 */
// TODO: 24/09/16 error in internet dc case
// TODO: 24/09/16 check multi threading and concurrency
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
