package org.jbus.remote.exceptions;

public class RemoteMethodCallTimedOut extends Exception {
    private final long requestId;
    public RemoteMethodCallTimedOut(long requestId) {
        super("RequestId: " + requestId);
        this.requestId = requestId;
    }
    public long getRequestId() {
        return requestId;
    }
}
