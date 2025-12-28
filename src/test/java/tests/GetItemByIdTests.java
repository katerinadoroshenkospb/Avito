package tests;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import models.CreateItem;
import models.Error;
import models.Item;
import models.Result;
import models.Statistics;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.provider.Arguments;
import providers.ItemProvider;
import utils.ParseUtils;
import utils.RandomUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.RandomUtils.getRandomInteger;

@Slf4j
@DisplayName("Получить объявления по его идентификатору: GET /api/1/item/{id}")
public class GetItemByIdTests {

    @Test
    @DisplayName(" Успешное получение 200 OK")
    void successGetItem() {
        CreateItem newItem = CreateItem.builder()
                .sellerID(getRandomInteger(111111, 999999))
                .name("obyavlenie")
                .price(getRandomInteger(0, 999999))
                .statistics(Statistics.builder()
                        .likes(getRandomInteger(1, 999))
                        .viewCount(getRandomInteger(1, 999))
                        .contacts(getRandomInteger(1, 999))
                        .build())
                .build();
        ValidatableResponse createResponse = ItemProvider.sendPostRequest(newItem);
        createResponse.statusCode(HttpStatus.SC_OK);
        log.info("Созданый Item: %s".formatted(newItem));
        String response = createResponse.extract().path("status");
        String id = ParseUtils.extractUuidString(response);
        log.info("У созданного Item id = %s".formatted(id));
        ValidatableResponse responseItem = ItemProvider.sendGetRequest(id);
        log.info("Полученный ответ при запросе GET: %s".formatted(responseItem.extract().asPrettyString()));
        List<Item> items = responseItem.extract().as(new TypeRef<List<Item>>() {});
        Item item = items.get(0);

        //TODO После исправления формата времени в createdAt сделать преобразование + проверку поля, что время в прошлом
        Assertions.assertEquals(1, items.size());
        Assertions.assertFalse(item.getCreatedAt().isEmpty());
        Assertions.assertEquals(item.getId(), id);
        Assertions.assertEquals(item.getName(), newItem.getName());
        Assertions.assertEquals(item.getPrice(), newItem.getPrice());
        Assertions.assertEquals(item.getSellerId(), newItem.getSellerID());
        Assertions.assertEquals(item.getStatistics(), newItem.getStatistics());
    }

    @TestFactory
    @DisplayName("Не успешное создание")
    Stream<DynamicTest> unsuccessfulGetItem() {
        return getItemSourceForUnsuccessfull().map(arguments -> {
                    String id = (String) arguments.get()[0];
                    Error error = (Error) arguments.get()[1];
                    String testCaseName = (String) arguments.get()[2];
                    return DynamicTest.dynamicTest(
                            testCaseName,
                            () -> {
                                log.info("Для запроса используется: %s".formatted(id));
                                log.info("Ожидаемая ошибка: %s".formatted(error));
                                ValidatableResponse response = ItemProvider.sendGetRequest(id);
                                log.info("Полученный ответ: %s".formatted(response.extract().asPrettyString()));
                                response.statusCode(HttpStatus.SC_BAD_REQUEST);
                                Error itemError = response.extract().response().as(new TypeRef<>() {
                                });
                                log.info("Полученная ошибка: %s".formatted(itemError));

                                assertEquals(
                                        error.getResult().getMessage() + id,
                                        itemError.getResult().getMessage(),
                                        "Сообщение об ошибке не соответствует ожидаемому"
                                );
                                assertEquals(
                                        HttpStatus.SC_BAD_REQUEST,
                                        Integer.parseInt(itemError.getStatus()),
                                        "Статус-код не соответствует ожидаемому"
                                );
                            }
                    );
                }
        );
    }

    private static Stream<Arguments> getItemSourceForUnsuccessfull() {
        return Stream.of(
                Arguments.of(
                        RandomUtils.getRandomInteger(0, 999999).toString(),
                        Error.builder()
                                .result(Result.builder().message("ID айтема не UUID: ").build())
                                .build(),
                        "Неуспешное получение 400 Bad Request при передаче id в формате числа"
                ),
                Arguments.of(
                        "0",
                        Error.builder()
                                .result(Result.builder().message("ID айтема не UUID: ").build())
                                .build(),
                        "Неуспешное получение 400 Bad Request при передаче id в формате числа"
                )
        );
    }
}
