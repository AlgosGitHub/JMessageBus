package mp.net.sockets.tests;

import mp.net.sockets.Serializer;
import org.junit.jupiter.api.*;

@DisplayName("Serializer")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSerializer {

    private static final String
            one = "1",
            two = "2",
            three = "3";

    byte[][] serialized;

    @Test
    @Order(1)
    @DisplayName("Serialize")
    void serialize() throws Exception {

        String[] testStrings = new String[] {one, two, three};

        Assertions.assertDoesNotThrow(() -> serialized = Serializer.serialize(testStrings));

    }

    @Test
    @Order(2)
    @DisplayName("Deserialize")
    void deserialize() throws Exception {

        Object[] deserialize = Serializer.deserialize(serialized);

        Assertions.assertEquals(one,    deserialize[0]);
        Assertions.assertEquals(two,    deserialize[1]);
        Assertions.assertEquals(three,  deserialize[2]);

    }

    @Test
    @Order(3)
    @DisplayName("Serialize Nulls")
    void nullParam() throws Exception {

        byte[][] serialized = Serializer.serialize(null);

        Assertions.assertNotNull(serialized);

    }

}
