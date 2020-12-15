package org.open.medgen.dart.api.application;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import java.util.logging.Logger;

public class JacksonProvider extends ResteasyJackson2Provider {

    private static final Logger LOGGER = Logger.getLogger(JacksonProvider.class.getName());

    public JacksonProvider() {
        super();

        LOGGER.info("loading jackson configurator");

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new ISO8601DateFormat());
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        
        setMapper(mapper);
    }
}

