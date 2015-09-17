package com.musicg.wave;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

/**
 * Test for CustomWaveHeader.
 *
 * Created by scottmangan on 9/17/15.
 */
public class CustomWaveHeaderTest {

    @Test
    public void testConstructHeader() throws Exception {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("audio_work/whistle.wav");
        CustomWaveHeader header = new CustomWaveHeader(input);
        Assert.assertEquals("Wrong number of channels.", header.getChannels(), 1);
    }
}