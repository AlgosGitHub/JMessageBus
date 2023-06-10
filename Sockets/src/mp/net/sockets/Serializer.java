package mp.net.sockets;

import java.io.*;

public class Serializer {

    public static Object[] deserialize(byte[][] serializedObjects) throws IOException, ClassNotFoundException {

        Object[] toReturn = new Object[serializedObjects.length];

        for(int i = 0; i < serializedObjects.length; i++)
            toReturn[i] = Serializer.deserializeObject(serializedObjects[i]);

        return toReturn;

    }

    public static byte[][] serialize(Object[] objects) throws IOException {

        if(objects == null)
            return new byte[0][0];

        byte[][] toReturn = new byte[objects.length][];

        for(int i = 0; i < objects.length; i++)
            toReturn[i] = Serializer.serializeObject((Serializable) objects[i]);

        return toReturn;

    }

    public static byte[] serializeObject(Serializable object) throws IOException {

        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();

            ObjectOutputStream out = new ObjectOutputStream(bos)) {

            out.writeObject(object);
            out.flush();

            return bos.toByteArray();

        }

    }

    public static <E> E deserializeObject(byte[] bytes) throws IOException, ClassNotFoundException {

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {

            return (E) ois.readObject();

        }

    }

}
