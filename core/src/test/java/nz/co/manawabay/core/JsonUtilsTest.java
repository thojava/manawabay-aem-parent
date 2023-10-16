package nz.co.manawabay.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonUtilsTest {

    @Test
    void testGetJsonString() {
        assertEquals("\"source\"", JsonUtils.getJsonString("source"));
    }
}
