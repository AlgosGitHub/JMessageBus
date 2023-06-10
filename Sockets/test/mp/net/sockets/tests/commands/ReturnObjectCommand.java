package mp.net.sockets.tests.commands;

import java.io.Serializable;

public record ReturnObjectCommand(Serializable object) implements Serializable {
}
