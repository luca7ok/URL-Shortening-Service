package com.Repository;

import com.domain.ShortURL;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

@Repository
public class ShortURLRepository {
    private final DynamoDbTable<ShortURL> shortURLTable;
    private final DynamoDbIndex<ShortURL> shortCodeIndex;
    private final DynamoDbIndex<ShortURL> longURLIndex;

    public ShortURLRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        shortURLTable = dynamoDbEnhancedClient.table("shortURLs", TableSchema.fromBean(ShortURL.class));
        shortCodeIndex = shortURLTable.index("shortCode-index");
        longURLIndex = shortURLTable.index("longURL-index");
    }

    public void save(ShortURL shortURL) {
        shortURLTable.putItem(shortURL);
    }

    public Optional<ShortURL> findByLongURL(String longURL) {
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(longURL).build());

        return longURLIndex.query(query)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public Optional<ShortURL> findByShortCode(String shortCode) {
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(shortCode).build());

        return shortCodeIndex.query(query)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }
}
