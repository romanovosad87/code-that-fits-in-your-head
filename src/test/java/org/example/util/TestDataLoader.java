package org.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class TestDataLoader {

    private final ObjectMapper objectMapper;

    public <T> T loadJson(String filePath, Class<T> clazz) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IllegalArgumentException("JSON file not found: " + filePath);
            }
            return objectMapper.readValue(is, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON file: " + filePath, e);
        }
    }

    private <T> List<T> loadJsonList(String filePath, Class<T> clazz) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IllegalArgumentException("JSON file not found on classpath: " + filePath);
            }
            return objectMapper.readValue(is, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON list: " + filePath, e);
        }
    }

    public <T> String getContentByString(String filePath, Class<T> clazz) {
        T entity = loadJson(filePath, clazz);
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void loadAndSave(String filePath, Class<T> clazz, DynamoDbTable<T> table) {
        log.info("📥 Loading JSON file '{}' into class {}", filePath, clazz.getSimpleName());
        try {
            T item = loadJson(filePath, clazz);
            log.debug("Successfully parsed JSON into object: {}", item);
            table.putItem(item);
            log.info("Saved {} item to DynamoDB table '{}'", clazz.getSimpleName(), table.tableName());
        } catch (Exception e) {
            log.error("Failed to load and save JSON file '{}' for class {}", filePath, clazz.getSimpleName(), e);
            throw e;
        }
    }

    public <T> void loadAndSaveList(String filePath, Class<T> clazz, DynamoDbTable<T> table) {
        log.info("Loading JSON array '{}' into list of {}", filePath, clazz.getSimpleName());
        try {
            List<T> items = loadJsonList(filePath, clazz);
            log.info("Loaded {} {} items from JSON", items.size(), clazz.getSimpleName());
            for (T item : items) {
                table.putItem(item);
                log.debug("Saved item: {}", item);
            }
            log.info("Successfully saved {} {} items into DynamoDB", items.size(), clazz.getSimpleName());
        } catch (Exception e) {
            log.error("Failed to load and save JSON list '{}' for class {}", filePath, clazz.getSimpleName(), e);
            throw new RuntimeException("Failed to load JSON list", e);
        }
    }
}
