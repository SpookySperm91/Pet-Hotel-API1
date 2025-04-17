package john.api1.application.components.annotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.IOException;

public class XSSSanitizeDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getText();
        // You can tweak the safelist or behavior as needed
        return Jsoup.clean(raw, Safelist.basic());
    }
}

