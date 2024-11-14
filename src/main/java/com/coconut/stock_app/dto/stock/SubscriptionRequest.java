package com.coconut.stock_app.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriptionRequest {
    @JsonProperty("header")
    private Header header;
    @JsonProperty("body")
    private Body body;

    @Data
    @AllArgsConstructor
    public static class Header {
        @JsonProperty("approval_key")
        private String approvalKey;
        @JsonProperty("custtype")
        private String custtype;
        @JsonProperty("tr_type")
        private String trType;
        @JsonProperty("content-type")
        private String contentType;
    }

    @Data
    @AllArgsConstructor
    public static class Body {
        @JsonProperty("input")
        private Input input;
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        @JsonProperty("tr_id")
        private String trId;
        @JsonProperty("tr_key")
        private String trKey;
    }
}
