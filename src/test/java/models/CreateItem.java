package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateItem {

    public Integer sellerID;
    public String name;
    public Integer price;
    public Statistics statistics;
}
