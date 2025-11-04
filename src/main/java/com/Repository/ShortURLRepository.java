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

    public ShortURLRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        shortURLTable = dynamoDbEnhancedClient.table("ShortURLs", TableSchema.fromBean(ShortURL.class));
        shortCodeIndex = shortURLTable.index("shortCode-index");
    }

    public void save(ShortURL shortURL) {
        shortURLTable.putItem(shortURL);
    }

    public Optional<ShortURL> findByShortCode(String shortCode) {
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(shortCode).build());

        return shortCodeIndex.query(query)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }
}
