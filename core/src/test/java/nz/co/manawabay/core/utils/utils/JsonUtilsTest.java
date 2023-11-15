package nz.co.manawabay.core.utils;

import nz.co.manawabay.core.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonUtilsTest {

    @Test
    void testGetJsonString() {
        assertEquals("\"source\"", JsonUtils.getJsonString("source"));
    }
}
