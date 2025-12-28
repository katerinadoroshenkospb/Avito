package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO получаемой ошибки
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Error {

    @JsonProperty("result")
    private Result result;

    @JsonProperty("status")
    private String status;
}
