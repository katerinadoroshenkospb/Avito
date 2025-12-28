package providers;

import constants.ApiServicePath;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import models.Item;

import static io.restassured.RestAssured.given;

public class StatisticProvider {

    @Getter
    private static List<Item> itemList = new ArrayList<>();

    public static ValidatableResponse sendGetRequestId(String id) {
        return given()
                .baseUri(ApiServicePath.BASE_URL)
                .basePath(ApiServicePath.GET_STATISTIC_BY_ID)
                .pathParam("id", id)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then();
    }
}
