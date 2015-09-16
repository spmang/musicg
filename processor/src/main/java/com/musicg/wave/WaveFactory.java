package com.musicg.wave;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

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

    public static Wave createWave(final WaveHeader header, byte[] audioBytes) throws IOException {
        // try to get the reader from the system.

        try {
            AudioInputStream input = createInputStream(new ByteArrayInputStream(audioBytes));
            return new Wave(new SystemWaveHeader(input), input);
        } catch (UnsupportedAudioFileException e) {

            // move to the backup
            InputStream inputStream = createCustomInputStream(new ByteArrayInputStream(audioBytes));
            return new Wave(new CustomWaveHeader(inputStream), inputStream);
        }
    }

    /**
     * Create a new custom wave from the given sourceWave.
     *
     * @param sourceWave the source Wave to copy the properties from.
     * @return A new custom Wave.
     */
    public static Wave createWave(final Wave sourceWave) {
        return new Wave(createCustomHeader(sourceWave.getWaveHeader()), sourceWave.getAudioStream());
    }

    private static Wave initWaveFromFile(final File inputFile) throws IOException {
        return initWaveWithInputStream(new FileInputStream(inputFile));
    }

    private static Wave initWaveWithInputStream(final InputStream inputStream) throws IOException {
        // try to get the reader from the system.

        try {
            AudioInputStream input = createInputStream(inputStream);
            return new Wave(new SystemWaveHeader(input), input);
        } catch (UnsupportedAudioFileException e) {

            // move to the backup
            InputStream customInput = createCustomInputStream(inputStream);
            return new Wave(new CustomWaveHeader(customInput), customInput);
        }
    }

    private static AudioInputStream createInputStream(InputStream inputStream) throws IOException, UnsupportedAudioFileException {
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        return AudioSystem.getAudioInputStream(inputStream);
    }

    private static InputStream createCustomInputStream(InputStream inputStream) throws IOException {
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        return inputStream;
    }

    private static WaveHeader createCustomHeader(final WaveHeader sourceHeader) {

        // TODO create custom header here.
        return sourceHeader;
    }
}