package org.jbus.remote.commands;

import java.io.Serializable;

public record RemoteMethodCall(long requestId, String methodName, byte[][] serializedArgs) implements Serializable {
}
