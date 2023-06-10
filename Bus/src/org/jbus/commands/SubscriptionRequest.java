package org.jbus.commands;

import java.io.Serializable;

public record SubscriptionRequest(String channel) implements Serializable {
}
