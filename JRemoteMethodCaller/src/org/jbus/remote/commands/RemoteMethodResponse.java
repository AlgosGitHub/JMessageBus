package org.jbus.remote.commands;

import java.io.Serializable;

public record RemoteMethodResponse(long requestId, String returnClassName, byte[] serializedReturn) implements Serializable {
}
