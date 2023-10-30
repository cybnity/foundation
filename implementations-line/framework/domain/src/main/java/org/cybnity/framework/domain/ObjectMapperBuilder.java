package org.cybnity.framework.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.cybnity.framework.domain.model.DomainEntityDeserializer;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Builder pattern implementation regarding a customized mapper
 */
public class ObjectMapperBuilder {
    private boolean enableIndentation;
    private boolean preserveOrder;
    private DateFormat dateFormat;

    public ObjectMapperBuilder enableIndentation() {
        this.enableIndentation = true;
        return this;
    }

    public ObjectMapperBuilder dateFormat() {
        this.dateFormat = new SimpleDateFormat(SerializationFormat.DATE_FORMAT_PATTERN);
        return this;
    }

    public ObjectMapperBuilder preserveOrder(boolean order) {
        this.preserveOrder = order;
        return this;
    }

    /**
     * Get mapper with custom configuration defined.
     * See <a href="https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features">serialization</a> or <a href="https://github.com/FasterXML/jackson-databind/wiki/Deserialization-Features">deserialization</a> features documentation for more details about configuration options.
     *
     * @return A mapper instance.
     */
    public ObjectMapper build() {
        ObjectMapper mapper = new ObjectMapper();
        // auto-detect of contents based on attributes (not on functional/immutability methods)
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Support of OffsetDateTime attributes
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());

        // Serialization configuration
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, this.enableIndentation);
        mapper.setDateFormat(this.dateFormat);
        if (this.preserveOrder) {
            mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        }

        // -- add custom serializers
        SimpleModule module = new SimpleModule();
        module.addSerializer(Identifier.class, new IdentifierSerializer());
        mapper.registerModule(module);
        module = new SimpleModule();
        module.addSerializer(EntityReference.class, new EntityReferenceSerializer());
        mapper.registerModule(module);
        module = new SimpleModule();
        module.addSerializer(HistoryState.class, new HistoryStateSerializer());
        mapper.registerModule(module);

        // Deserialization configuration
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false /* disabled use Enum.name() */);
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        // Deserialize list of element as array
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true /* disabled provide java.util.List */);

        // --- add custom deserializers
        module = new SimpleModule();
        module.addDeserializer(Identifier.class, new IdentifierStringBasedDeserializer(IdentifierStringBased.class));
        mapper.registerModule(module);
        module = new SimpleModule();
        module.addDeserializer(EntityReference.class, new EntityReferenceDeserializer());
        mapper.registerModule(module);
        module = new SimpleModule();
        module.addDeserializer(Entity.class, new DomainEntityDeserializer());
        mapper.registerModule(module);
        module = new SimpleModule();
        module.addDeserializer(HistoryState.class, new HistoryStateDeserializer());
        mapper.registerModule(module);

        return mapper;
    }

}