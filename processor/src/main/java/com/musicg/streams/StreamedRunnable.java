package com.musicg.streams;

import java.io.DataOutputStream;

/**
 * Interface that allows a Runnable with a DataOutputStream attached.
 * <p/>
 *
 * @see StreamFactory
 * <p/>
 * Created by scottmangan on 9/17/15.
 */
public interface StreamedRunnable extends Runnable {

    /**
     * Set the output stream to write to.
     *
     * @param output A DataOutputStream to write the data to.
     */
    void setOutputStream(DataOutputStream output);
}
