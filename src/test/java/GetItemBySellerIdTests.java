import Services.ItemService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.CreateItem;
import models.Error;
import models.Item;
import models.Statistics;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import providers.ItemProvider;

import static utils.RandomUtils.getRandomInteger;

@Slf4j
@DisplayName("Получить все объявления по идентификатору продавца: GET /api/1/{sellerID}/item")
public class GetItemBySellerIdTests {

    @Getter
    private static List<Item> itemList = new ArrayList<>();

    @BeforeEach
    void addItem() {
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

    @AfterEach
    void resetItemList() {
        itemList.clear();
    }

    @Test
    @DisplayName("Успешное получение 200 OK")
    void successGetOneItem() {
        ValidatableResponse response = ItemProvider.sendGetRequestSellerId(itemList.get(0).getSellerId());
        response.statusCode(HttpStatus.SC_OK);
        List<Item> listItem = response.extract().response().as(new TypeRef<List<Item>>() {});
        Assertions.assertEquals(1, listItem.size());
        Assertions.assertEquals(itemList.get(0), listItem.get(0));
    }

    @Test
    @DisplayName("Успешное получение 200 OK нескольких Item'ов")
    void successGetSomeItems() {
        CreateItem item = CreateItem.builder()
                .sellerID(itemList.get(0).getSellerId())
                .name("secondItem")
                .price(getRandomInteger(0, 999999))
                .statistics(Statistics.builder()
                        .likes(getRandomInteger(1, 999))
                        .viewCount(getRandomInteger(1, 999))
                        .contacts(getRandomInteger(1, 999))
                        .build())
                .build();
        itemList.add(ItemService.addItem(item));
        ValidatableResponse response = ItemProvider.sendGetRequestSellerId(itemList.get(0).getSellerId());
        response.statusCode(HttpStatus.SC_OK);
        List<Item> listItem = response.extract().response().as(new TypeRef<List<Item>>() {});
        Assertions.assertEquals(2, listItem.size());
        Assertions.assertEquals(itemList.get(0), listItem.get(0));
        Assertions.assertEquals(itemList.get(1), listItem.get(1));
    }

    @Test
    @DisplayName("Неуспешное получение 200 ОК и пустого списка при передаче несуществующего корректного sellerId")
    void sellerIdNotPresent() {
        ValidatableResponse response = ItemProvider.sendGetRequestSellerId(itemList.get(0).getSellerId() + 1);
        response.statusCode(HttpStatus.SC_OK);
        List<Item> listItem = response.extract().response().as(new TypeRef<List<Item>>() {});
        Assertions.assertEquals(0, listItem.size());
    }

    @Test
    @DisplayName("Неуспешное получение 400 Bad Request и пустого списка при передаче sellerId = 0")
    void sellerIdIs0() {
        ValidatableResponse response = ItemProvider.sendGetRequestSellerId(0);
        response.statusCode(HttpStatus.SC_BAD_REQUEST);
        Error itemError = response.extract().response().as(new TypeRef<>() {
        });
        //TODO доделать ожидания, когда будет приходить корректный статус
    }
}
