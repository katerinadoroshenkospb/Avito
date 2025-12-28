package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO ответа от сервиса при получении Item
 */
@Data
@NoArgsConstructor
public class Item {

    @JsonProperty("id")
    private String id;

    @JsonProperty("sellerId")
    private Integer sellerId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("statistics")
    private Statistics statistics;
}
