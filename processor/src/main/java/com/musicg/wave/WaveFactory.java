package com.musicg.wave;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

public final class WaveFactory {

    private WaveFactory() {
        super();
    }

    /**
     * Constructor
     *
     * @param inputFile Wave file
     */
    public static Wave createWave(final File inputFile) throws IOException {
        return initWaveFromFile(inputFile);
    }

    /**
     * Constructor
     *
     * @param filename The input file to load.
     * @throws com.musicg.wave.WaveException
     */
    public static Wave createWave(final String filename) throws IOException {
        URL input = Thread.currentThread().getContextClassLoader().getResource(filename);
        if (input == null) {
            throw new WaveException("Source file not found.");
        }
        try {
            return initWaveFromFile(new File(input.toURI()));
        } catch (URISyntaxException e) {
            throw new WaveException("Invalid input location.", e);
        }
    }

    /**
     * Constructor
     *
     * @param inputStream Wave file input stream
     */
    public static Wave createWave(final InputStream inputStream) throws IOException {
        return initWaveWithInputStream(inputStream);
    }

    /**
     * Constructor
     *
     * @param data The data to sample.
     */
    public static Wave createWave(final byte[] data) throws IOException {
        return initWaveWithInputStream(new ByteArrayInputStream(data));
    }

    private static Wave initWaveFromFile(final File inputFile) throws IOException {
        return initWaveWithInputStream(new FileInputStream(inputFile));
    }

    private static Wave initWaveWithInputStream(final InputStream inputStream) throws IOException {
        // try to get the reader from the system.

        try {
            AudioInputStream input = AudioSystem.getAudioInputStream(inputStream);
            return new Wave(new SystemWaveHeader(input), input);
        } catch (UnsupportedAudioFileException e) {

            // move to the backup
            return new Wave(new CustomWaveHeader(inputStream), inputStream);
        }
    }
}