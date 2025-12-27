package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Statistics {

    public Integer likes;
    public Integer viewCount;
    public Integer contacts;
}
