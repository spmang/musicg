package com.musicg.streams;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

public final class AudioFormatInputStreamFactory {

    private AudioFormatInputStreamFactory() {
        super();
    }

    /**
     * Constructor
     *
     * @param inputFile Wave file
     */
    public static AudioFormatInputStream createAudioFormatInputStream(final File inputFile) throws IOException, UnsupportedAudioFileException {
        return initWaveFromFile(inputFile);
    }

    /**
     * Constructor
     *
     * @param filename The input file to load.
     * @throws AudioException
     */
    public static AudioFormatInputStream createAudioFormatInputStream(final String filename) throws IOException, UnsupportedAudioFileException {
        URL input = Thread.currentThread().getContextClassLoader().getResource(filename);
        if (input == null) {
            throw new AudioException("Source file not found.");
        }
        try {
            return initWaveFromFile(new File(input.toURI()));
        } catch (URISyntaxException e) {
            throw new AudioException("Invalid input location.", e);
        }
    }

    /**
     * Constructor
     *
     * @param inputStream Wave file input stream
     */
    public static AudioFormatInputStream createAudioFormatInputStream(final InputStream inputStream) throws IOException, UnsupportedAudioFileException {
        return initWaveWithInputStream(inputStream);
    }

    /**
     * Constructor
     *
     * @param data The data to sample.
     */
    public static AudioFormatInputStream createAudioFormatInputStream(final byte[] data) throws IOException, UnsupportedAudioFileException {
        return initWaveWithInputStream(new ByteArrayInputStream(data));
    }

    public static AudioFormatInputStream createAudioFormatInputStream(final AudioFormat header, byte[] audioBytes) throws IOException, UnsupportedAudioFileException {
        // try to get the reader from the system.
        return new AudioFormatInputStream(createInputStream(new ByteArrayInputStream(audioBytes)));
    }

    /**
     * Create a stream that performs resampling.
     *
     * @param newSampleRate sample rate of the interpolated samples
     * @param samples       original samples
     * @return interpolated samples
     */
    public static PipedAudioFormatInputStream createResampleStream(AudioFormatInputStream samples, float newSampleRate) throws IOException {
        return new ResampleInputStream(samples.getAudioInputStream(), newSampleRate);
    }


    private static AudioFormatInputStream initWaveFromFile(final File inputFile) throws IOException, UnsupportedAudioFileException {
        return initWaveWithInputStream(new FileInputStream(inputFile));
    }

    private static AudioFormatInputStream initWaveWithInputStream(final InputStream inputStream) throws IOException, UnsupportedAudioFileException {
        // try to get the reader from the system.
        return new AudioFormatInputStream(createInputStream(inputStream));
    }

    private static AudioInputStream createInputStream(InputStream inputStream) throws IOException, UnsupportedAudioFileException {
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        return AudioSystem.getAudioInputStream(inputStream);
    }

    private static AudioFormat createAudioFormat(final AudioFormat sourceHeader) {
        return new AudioFormat(sourceHeader.getEncoding(), sourceHeader.getSampleRate(),
                sourceHeader.getSampleSizeInBits(), sourceHeader.getChannels(),
                sourceHeader.getFrameSize(), sourceHeader.getFrameRate(), sourceHeader.isBigEndian());
    }
}