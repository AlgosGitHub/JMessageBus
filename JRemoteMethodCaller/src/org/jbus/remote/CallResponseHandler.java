package org.jbus.remote;

import org.jbus.remote.exceptions.RemoteMethodCallTimedOut;

import java.util.HashMap;
import java.util.Map;

class CallResponseHandler {

    private final Map<Long, Object> threadLocksViaRequestId = new HashMap<>();

    private final Map<Long, Object> responsesViaRequestId = new HashMap<>();

    private final long requestTimeoutThresholdInMilliseconds;

    CallResponseHandler() {
        this.requestTimeoutThresholdInMilliseconds = 3000;
    }

    CallResponseHandler(long timeToWait) {
        this.requestTimeoutThresholdInMilliseconds = timeToWait;
    }

    void responseReceived(long requestId, Object response) {

        //prepare to release the thread currently blocking the function call awaiting a response.
        Object threadLock = threadLocksViaRequestId.remove(requestId);

        //ensure our blocking-thread still exists. it may have been removed already by a timeout event.
        if(threadLock == null)
            return;

        //deposit the received object into the response buffer
        responsesViaRequestId.put(requestId, response);

        //release the waiting thread
        synchronized (threadLock) {
            threadLock.notify();
        }

    }

    <E> E waitForResponse(long requestId) throws InterruptedException, RemoteMethodCallTimedOut {

        //create an object to act as our synchronization lock.
        Object threadLock = new Object();

        //remember the blocking thread so that we can kill it when the response (or timeout) happens
        threadLocksViaRequestId.put(requestId, threadLock);

        //block this function until we receive a response
        synchronized (threadLock) {
            threadLock.wait(requestTimeoutThresholdInMilliseconds);
        }

        if(!responsesViaRequestId.containsKey(requestId)) {

            //stop waiting for a response
            threadLocksViaRequestId.remove(requestId);

            //inform our caller of the failure
            throw new RemoteMethodCallTimedOut(requestId);

        }

        //return the response from the buffer, may be null.
        return (E)responsesViaRequestId.remove(requestId);

    }

}
