package org.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Utility class for validating JSON strings against JSON schemas loaded from resources.
 */
public class JsonSchemaValidatorUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaValidatorUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validates the provided JSON string against the schema file in the resources directory.
     *
     * @param schemaFileName The name of the schema file (e.g., "user-schema.json") in src/test/resources.
     * @param jsonString     The JSON string to validate.
     * @return true if the JSON matches the schema, false otherwise.
     * @throws IllegalArgumentException if schema file not found or invalid JSON.
     */
    public static boolean validateJsonAgainstSchema(String schemaFileName, String jsonString) {
        try {
            // Load schema from resources
            JsonNode schemaNode;
            try (InputStream schemaStream = JsonSchemaValidatorUtil.class.getClassLoader()
                    .getResourceAsStream(schemaFileName)) {
                if (schemaStream == null) {
                    throw new IllegalArgumentException("Schema file not found: " + schemaFileName);
                }
                schemaNode = objectMapper.readTree(schemaStream);
            }

            // Parse JSON string
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // Validate
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
            JsonSchema schema = factory.getSchema(schemaNode);
            Set<ValidationMessage> errors = schema.validate(jsonNode);

            if (!errors.isEmpty()) {
                logger.warn("Validation failed for {}: {}", schemaFileName, errors);
                return false;
            }

            logger.info("Validation passed for {}: {}", schemaFileName, jsonString.substring(0, Math.min(100, jsonString.length())) + "...");
            return true;

        } catch (IOException e) {
            logger.error("Error validating JSON against schema {}: {}", schemaFileName, e.getMessage());
            throw new IllegalArgumentException("Failed to load or parse schema/JSON: " + e.getMessage(), e);
        }
    }
}