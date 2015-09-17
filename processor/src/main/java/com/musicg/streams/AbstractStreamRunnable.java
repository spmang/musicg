package com.musicg.streams;

import java.io.DataOutputStream;

/**
 * Abstract class allowing a Runnable task that streams data to the given DataOutputStream.
 *
 * @author smangan
 */
public abstract class AbstractStreamRunnable implements StreamedRunnable {

    protected DataOutputStream outputStream;

    @Override
    public void setOutputStream(DataOutputStream output) {
        outputStream = output;
    }
}
