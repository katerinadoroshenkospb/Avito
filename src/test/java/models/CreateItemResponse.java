package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemResponse {
    private String id;
    private Integer SellerId;
    private String name;
    private Integer price;
    private String createdAt;
    private Statistics statistics;
}
