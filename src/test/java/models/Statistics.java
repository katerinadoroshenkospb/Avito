package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO получаемой из ответа Statistics
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Statistics {

    public Integer likes;
    public Integer viewCount;
    public Integer contacts;
}
