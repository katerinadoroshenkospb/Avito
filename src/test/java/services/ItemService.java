package services;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import models.CreateItem;
import models.Item;
import org.apache.http.HttpStatus;
import providers.ItemProvider;
import utils.ParseUtils;

@Slf4j
public class ItemService {

    /**
     * Добавить объявление в сервис
     *
     * @param item добавляемое объявление
     * @return возвращает класс {@code Item} содержащий информацию о созданном объявлении
     */
    public static Item addItem(CreateItem item) {
        ValidatableResponse createResponse = ItemProvider.sendPostRequest(item);
        createResponse.statusCode(HttpStatus.SC_OK);
        String response = createResponse.extract().path("status");
        String id = ParseUtils.extractUuidString(response);
        ValidatableResponse responseItem = ItemProvider.sendGetRequest(id);
        responseItem.statusCode(HttpStatus.SC_OK);
        Item gettedItem = responseItem.extract().response().as(new TypeRef<List<Item>>() {}).get(0);
        log.info("Создан Item: %s".formatted(gettedItem));
        return gettedItem;
    }
}
