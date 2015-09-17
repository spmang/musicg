package com.musicg.wave;

import java.io.IOException;
import java.io.InputStream;

/**
 * WAV File Specification. This is used as a fallback if the system cannot read the AudioFormat.
 * https://ccrma.stanford.edu/courses/422/projects/WaveFormat/
 *
 * @author Jacquet Wong
 * @author Scott Mangan
 */
public class CustomWaveHeader implements WaveHeader {

    public static final String RIFF_HEADER = "RIFF";
    public static final String WAVE_HEADER = "WAVE";
    public static final String FMT_HEADER = "fmt ";
    public static final String DATA_HEADER = "data";
    public static final int HEADER_BYTE_LENGTH = 44;    // 44 bytes for header

    private int audioFormat; // unsigned 2 bytes, little endian
    private float byteRate; // unsigned 4 bytes, little endian
    private int frameSize; // unsigned 2 bytes, little endian
    private float frameRate; // matches sample rate for wav.

    private String format;    // 4 bytes

    private int sampleSize;
    private float sampleRate; // unsigned 4 bytes, little endian
    private int bitsPerSample; // unsigned 2 bytes, little endian

    private int channels; // unsigned 2 bytes, little endian

    public CustomWaveHeader() {
        // init a 8k 16bit mono wav
        audioFormat = 1;
        channels = 1;
        sampleRate = 8000;
        byteRate = 16000;
        frameSize = 2;
        bitsPerSample = 16;
        sampleSize = 2;
    }

    public CustomWaveHeader(InputStream inputStream) throws IOException {
        loadHeader(inputStream);
    }

    private void loadHeader(InputStream inputStream) throws IOException {

        int bytesRead = 0;
        byte[] headerBuffer = new byte[HEADER_BYTE_LENGTH];
        while (bytesRead < HEADER_BYTE_LENGTH) {
            bytesRead += inputStream.read(headerBuffer);
        }

        // read header  - skip over data we don't care about.
        int pointer = 8;
        // little endian
        format = new String(new byte[]{headerBuffer[pointer++],
                headerBuffer[pointer++], headerBuffer[pointer++],
                headerBuffer[pointer++]});
        pointer += 8;
        audioFormat = ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        channels = ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        sampleRate = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff) << 24;
        frameRate = sampleRate;
        byteRate = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff) << 24;
        frameSize = ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        bitsPerSample = ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer] & 0xff) << 8);
        sampleSize = bitsPerSample / 8;
        // end read header

        if (bitsPerSample != 8 && bitsPerSample != 16) {
            throw new WaveException("WaveHeader: only supports bitsPerSample 8 or 16");
        }
    }

    public String getFormatName() {
        return format;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
        this.byteRate = sampleRate * bitsPerSample / 8;
    }

    public float getByteRate() {
        return byteRate;
    }

    public void setByteRate(long byteRate) {
        this.byteRate = byteRate;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(final float rate) {
        frameRate = rate;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public void setBitsPerSample(int bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
        this.sampleSize = bitsPerSample / 8;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("format: ");
        sb.append(format);
        sb.append("\n");
        sb.append("audioFormat: ");
        sb.append(audioFormat);
        sb.append("\n");
        sb.append("channels: ");
        sb.append(channels);
        sb.append("\n");
        sb.append("sampleRate: ");
        sb.append(sampleRate);
        sb.append("\n");
        sb.append("byteRate: ");
        sb.append(byteRate);
        sb.append("\n");
        sb.append("frameSize: ");
        sb.append(frameSize);
        sb.append("\n");
        sb.append("bitsPerSample: ");
        sb.append(bitsPerSample);
        sb.append("\n");
        return sb.toString();
    }
}
