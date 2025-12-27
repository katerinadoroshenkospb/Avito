package models;

import lombok.Data;

@Data
public class CreateItemResponse {
    private String id;
    private Integer SellerId;
    private String name;
    private Integer price;
    private String createdAt;
    private Statistics statistics;
}
