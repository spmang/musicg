package com.musicg.wave;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test the creation of Wave instances using the factory.
 * <p/>
 * Created by scottmangan on 9/17/15.
 */
public class WaveFactoryTest {

    @Test
    public void testCreateWave() throws Exception {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("audio_work/whistle.wav");
        Assert.assertNotNull("Could not load whistle.wav.", resource);
        Path input = Paths.get(resource.toURI());
        Wave wave = WaveFactory.createWave(Files.readAllBytes(input));
        Assert.assertNotNull("Did not create wave.", wave);
    }

    @Test
    public void testCreateWave1() throws Exception {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("audio_work/whistle.wav");
        Assert.assertNotNull("Could not load whistle.wav.", resource);
        Path input = Paths.get(resource.toURI());
        Wave wave = WaveFactory.createWave(input.toFile());
        Assert.assertNotNull("Did not create wave.", wave);
    }

    @Test
    public void testCreateWave2() throws Exception {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("audio_work/whistle.wav");
        Wave wave = WaveFactory.createWave(input);
        Assert.assertNotNull("Did not create wave.", wave);
    }

    @Test
    public void testCreateWave3() throws Exception {
        Wave wave = WaveFactory.createWave("audio_work/whistle.wav");
        Assert.assertNotNull("Did not create wave.", wave);
    }

    @Test
    public void testCreateWave4() throws Exception {
        Wave wave = WaveFactory.createWave("audio_work/whistle.wav");
        Wave waveCopy = WaveFactory.createWave(wave);

        // Verify the headers are equal.
        WaveHeader original = wave.getWaveHeader();
        WaveHeader headerCopy = waveCopy.getWaveHeader();

        Assert.assertEquals("Incorrect number of channels.", original.getChannels(), headerCopy.getChannels());
    }

    public void testCreateWave5() throws Exception {
        // WaveFactory.createWave(WaveHeader, new byte[0]);
    }
}