package org.jbus;

import java.io.Serializable;

record Message(String channel, Serializable contents) implements Serializable {
}
