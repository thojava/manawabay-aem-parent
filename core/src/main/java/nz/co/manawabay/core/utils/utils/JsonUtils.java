package nz.co.manawabay.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.servlets.post.JSONResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class JsonUtils {
    /**
     * Returns a JSON string representation of the given object.
     */
    public static String getJsonString(Object source) {
        ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String jsonString =null;
        try {
            JsonNode node = mapper.valueToTree(source);
            jsonString = mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            e.getMessage();
        }
        return jsonString;
    }

    public static <T> void writeObjAsJsonResponse(HttpServletResponse response,
                                                  int status, T obj) {
        try {
            response.setContentType(JSONResponse.RESPONSE_CONTENT_TYPE);
            response.setStatus(status);
            String str = obj instanceof String ?
                    (String) obj :
                    getStringFromObject(obj);
            response.getWriter().write(str);
        } catch (IOException e) {
            log.error("Unable to get Response Writer : ", e);
        }
    }

    public static <T> String getStringFromObject(T obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    public static <T> T getObjectFromString(String str, Class<T> type) throws JsonProcessingException {
        return new ObjectMapper().readValue(str, type);
    }
}
