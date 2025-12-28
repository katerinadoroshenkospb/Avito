package tests;

import services.ItemService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.CreateItem;
import models.Error;
import models.Item;
import models.Statistics;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import providers.StatisticProvider;

import static utils.RandomUtils.getRandomInteger;

@Slf4j
@DisplayName("Получить статистику по айтем GET /api/1/statistic/{id}")
public class GetStatisticsById {

    @Getter
    private static List<Item> itemList = new ArrayList<>();

    @BeforeAll
    static void addItem() {
        CreateItem item = CreateItem.builder()
                .sellerID(getRandomInteger(111111, 999999))
                .name("obyavlenie")
                .price(getRandomInteger(0, 999999))
                .statistics(Statistics.builder()
                        .likes(getRandomInteger(1, 999))
                        .viewCount(getRandomInteger(1, 999))
                        .contacts(getRandomInteger(1, 999))
                        .build())
                .build();
        itemList.add(ItemService.addItem(item));
    }

    @Test
    @DisplayName("Успешное получение 200 OK")
    void getStatistic() {
        ValidatableResponse response = StatisticProvider.sendGetRequestId(itemList.get(0).getId());
        response.statusCode(HttpStatus.SC_OK);
        List<Statistics> receiveStatistic = response.extract().response().as(new TypeRef<List<Statistics>>() {});

        Assertions.assertEquals(1, receiveStatistic.size());
        Assertions.assertEquals(itemList.get(0).getStatistics(), receiveStatistic.get(0));
    }

    @Test
    @DisplayName("Неуспешное получение 4404 Not Found при передаче в id = 00000000-0000-0000-0000-000")
    void getStatisticBy0() {
        ValidatableResponse response = StatisticProvider.sendGetRequestId("00000000-0000-0000-0000-000");
        response.statusCode(HttpStatus.SC_BAD_REQUEST);
        Error error = response.extract().response().as(new TypeRef<>() {});
        log.info("Полученная ошибка: %s".formatted(error.toString()));

        Assertions.assertEquals("передан некорректный идентификатор объявления", error.getResult().getMessage());
        Assertions.assertEquals("400", error.getStatus());
    }

    @Test
    @DisplayName("Неуспешное получение 404 Not Found при передаче несуществующего id")
    void getStatisticByNull() {
        String uuid = UUID.randomUUID().toString();
        ValidatableResponse response = StatisticProvider.sendGetRequestId(uuid);
        response.statusCode(HttpStatus.SC_NOT_FOUND);
        Error error = response.extract().response().as(new TypeRef<>() {});
        log.info("Полученная ошибка: %s".formatted(error.toString()));

        Assertions.assertEquals("statistic %s not found".formatted(uuid), error.getResult().getMessage());
        Assertions.assertEquals("404", error.getStatus());
    }
}
