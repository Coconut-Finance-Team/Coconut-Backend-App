package com.coconut.stock_app.entity.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "stocks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDocument {
    @Id
    private String stockCode;

    @Field(type = FieldType.Text, analyzer = "custom_korean")
    private String stockName;
}
