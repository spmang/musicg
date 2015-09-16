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

    private String chunkId;    // 4 bytes
    private long chunkSize; // unsigned 4 bytes, little endian
    private String format;    // 4 bytes
    private String subChunk1Id;    // 4 bytes
    private long subChunk1Size; // unsigned 4 bytes, little endian
    private int audioFormat; // unsigned 2 bytes, little endian
    private int channels; // unsigned 2 bytes, little endian
    private long sampleRate; // unsigned 4 bytes, little endian
    private long byteRate; // unsigned 4 bytes, little endian
    private int blockAlign; // unsigned 2 bytes, little endian
    private int bitsPerSample; // unsigned 2 bytes, little endian
    private String subChunk2Id;    // 4 bytes
    private long subChunk2Size; // unsigned 4 bytes, little endian

    private int trimSample;

    public CustomWaveHeader() {
        // init a 8k 16bit mono wav
        chunkSize = 36;
        subChunk1Size = 16;
        audioFormat = 1;
        channels = 1;
        sampleRate = 8000;
        byteRate = 16000;
        blockAlign = 2;
        bitsPerSample = 16;
        subChunk2Size = 0;
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

        // read header
        int pointer = 0;
        chunkId = new String(new byte[]{headerBuffer[pointer++],
                headerBuffer[pointer++], headerBuffer[pointer++],
                headerBuffer[pointer++]});
        // little endian
        chunkSize = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff << 24);
        format = new String(new byte[]{headerBuffer[pointer++],
                headerBuffer[pointer++], headerBuffer[pointer++],
                headerBuffer[pointer++]});
        subChunk1Id = new String(new byte[]{headerBuffer[pointer++],
                headerBuffer[pointer++], headerBuffer[pointer++],
                headerBuffer[pointer++]});
        subChunk1Size = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff) << 24;
        audioFormat = ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        channels = ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        sampleRate = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff) << 24;
        byteRate = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff) << 24;
        blockAlign = ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        bitsPerSample = ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        subChunk2Id = new String(new byte[]{headerBuffer[pointer++],
                headerBuffer[pointer++], headerBuffer[pointer++],
                headerBuffer[pointer++]});
        subChunk2Size = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer] & 0xff) << 24;
        // end read header

        if (bitsPerSample != 8 && bitsPerSample != 16) {
            throw new WaveException("WaveHeader: only supports bitsPerSample 8 or 16");
        }

        calculateTrimSample();

        // check the format is support
        if (chunkId.toUpperCase().equals(RIFF_HEADER)
                && format.toUpperCase().equals(WAVE_HEADER) && audioFormat == 1) {
            return;
        }
        throw new WaveException("WaveHeader: Unsupported header format");
    }

    private void calculateTrimSample() {
        trimSample = getSampleRate() * getSampleSize() / 8 * getChannels();
    }

    public String getChunkId() {
        return chunkId;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public String getFormat() {
        return format;
    }

    public String getSubChunk1Id() {
        return subChunk1Id;
    }

    public long getSubChunk1Size() {
        return subChunk1Size;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public int getChannels() {
        return channels;
    }

    public int getSampleRate() {
        return (int) sampleRate;
    }

    public int getByteRate() {
        return (int) byteRate;
    }

    public int getBlockAlign() {
        return blockAlign;
    }

    public int getSampleSize() {
        return bitsPerSample;
    }

    public String getSubChunk2Id() {
        return subChunk2Id;
    }

    public long getSubChunk2Size() {
        return subChunk2Size;
    }

    public void setSampleRate(int sampleRate) {
        int newSubChunk2Size = (int) (this.subChunk2Size * sampleRate / this.sampleRate);
        // if num bytes for each sample is even, the size of newSubChunk2Size also needed to be in even number
        if ((bitsPerSample / 8) % 2 == 0) {
            if (newSubChunk2Size % 2 != 0) {
                newSubChunk2Size++;
            }
        }

        this.sampleRate = sampleRate;
        this.byteRate = sampleRate * bitsPerSample / 8;
        this.chunkSize = newSubChunk2Size + 36;
        this.subChunk2Size = newSubChunk2Size;
        calculateTrimSample();
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setSubChunk1Id(String subChunk1Id) {
        this.subChunk1Id = subChunk1Id;
    }

    public void setSubChunk1Size(long subChunk1Size) {
        this.subChunk1Size = subChunk1Size;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    public void setChannels(int channels) {
        this.channels = channels;
        calculateTrimSample();
    }

    public void setByteRate(long byteRate) {
        this.byteRate = byteRate;
    }

    public void setBlockAlign(int blockAlign) {
        this.blockAlign = blockAlign;
    }

    public void setBitsPerSample(int bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
        calculateTrimSample();
    }

    public void setSubChunk2Id(String subChunk2Id) {
        this.subChunk2Id = subChunk2Id;
    }

    public void setSubChunk2Size(long subChunk2Size) {
        this.subChunk2Size = subChunk2Size;
    }

    public int getTrimSample() {
        return trimSample;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("chunkId: ");
        sb.append(chunkId);
        sb.append("\n");
        sb.append("chunkSize: ");
        sb.append(chunkSize);
        sb.append("\n");
        sb.append("format: ");
        sb.append(format);
        sb.append("\n");
        sb.append("subChunk1Id: ");
        sb.append(subChunk1Id);
        sb.append("\n");
        sb.append("subChunk1Size: ");
        sb.append(subChunk1Size);
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
        sb.append("blockAlign: ");
        sb.append(blockAlign);
        sb.append("\n");
        sb.append("bitsPerSample: ");
        sb.append(bitsPerSample);
        sb.append("\n");
        sb.append("subChunk2Id: ");
        sb.append(subChunk2Id);
        sb.append("\n");
        sb.append("subChunk2Size: ");
        sb.append(subChunk2Size);
        return sb.toString();
    }
}
