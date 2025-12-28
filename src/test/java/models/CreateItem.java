package models;

import lombok.Builder;
import lombok.Data;

/**
 * POJO класса, используемого для запроса создания Item
 */
@Data
@Builder
public class CreateItem {

    public Integer sellerID;
    public String name;
    public Integer price;
    public Statistics statistics;
}
