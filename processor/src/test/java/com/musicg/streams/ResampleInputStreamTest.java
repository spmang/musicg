package com.musicg.streams;

import com.musicg.fingerprint.FingerprintProperties;
import org.junit.Assert;
import org.junit.Test;

import java.io.DataOutputStream;

import static org.junit.Assert.*;

/**
 * Created by Scott on 9/18/2015.
 */
public class ResampleInputStreamTest {

    @Test
    public void testReadValue() throws Exception {

        String filename = "cock_a_1.wav";
        FingerprintProperties fingerprintProperties = FingerprintProperties.getInstance();

        // resample to target rate
        int targetRate = fingerprintProperties.getSampleRate();

        AudioFormatInputStream wave = AudioFormatInputStreamFactory.createAudioFormatInputStream("audio_work/" + filename);
        PipedAudioFormatInputStream resampledWaveData = new ResampleInputStream(wave.getAudioInputStream(), targetRate, true);

        Pipe pipe = new Pipe(resampledWaveData.getAudioFormat());
        resampledWaveData.connect(pipe.getOutput());

        resampledWaveData.readValue();
        Assert.assertEquals("Resampled value is incorrect.", 259, pipe.getInput().readShort());
        resampledWaveData.readValue();
        Assert.assertEquals("Resampled value is incorrect.", 505, pipe.getInput().readShort());
        resampledWaveData.readValue();
    }
}