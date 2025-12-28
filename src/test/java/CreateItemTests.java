import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import models.CreateItem;
import models.CreateItemResponse;
import models.Error;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.RandomUtils.getRandomInteger;

@Slf4j
@DisplayName("Создать объявления: POST /api/1/item")
public class CreateItemTests {

    @Test
    @DisplayName("Успешное создание 200 OK")
    void successCreateItem() {
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
        log.info("Для запроса используется: %s".formatted(item));
        ValidatableResponse response = ItemProvider.sendPostRequest(item);
        response.statusCode(HttpStatus.SC_OK);
        try {
            CreateItemResponse itemResponse = response.extract().response().as(new TypeRef<>() {
            });
            assertEquals(0, itemResponse.getSellerId());
            assertEquals(item.getName(), itemResponse.getName());
            assertEquals(0, itemResponse.getPrice());
            Assertions.assertNotNull(itemResponse.getCreatedAt());
            assertEquals(0, itemResponse.getStatistics().getLikes());
            assertEquals(0, itemResponse.getStatistics().getViewCount());
            assertEquals(0, itemResponse.getStatistics().getContacts());
        } catch (Exception e) {
            log.error("Ответ не соответствует описанному контракту");
            log.error("Полученный ответ: %s".formatted(response.extract().asPrettyString()));
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Повторное создание Item при одинаковом sellerId, 400 Bad Request")
    void sameSellerId() {
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
        log.info("Для запроса используется: %s".formatted(item));
        ValidatableResponse responseFirst = ItemProvider.sendPostRequest(item);
        responseFirst.statusCode(HttpStatus.SC_OK);
        ValidatableResponse response = ItemProvider.sendPostRequest(item);
        response.statusCode(HttpStatus.SC_BAD_REQUEST);
        Error itemError = response.extract().response().as(new TypeRef<>() {
        });
        log.info("Полученная ошибка при повторном создании с тем же sellerId: %s".formatted(itemError));
        assertEquals(HttpStatus.SC_BAD_REQUEST, itemError.getStatus());
        assertEquals("передан некорректный идентификатор объявления", itemError.getResult().getMessage());
    }

    @TestFactory
    @DisplayName("Не успешное создание")
    Stream<DynamicTest> unsuccessfulCreateItem() {
        return createItemSourceForUnsuccessfull().map(arguments -> {
                    CreateItem item = (CreateItem) arguments.get()[0];
                    Error error = (Error) arguments.get()[1];
                    String testCaseName = (String) arguments.get()[2];
                    return DynamicTest.dynamicTest(
                            testCaseName,
                            () -> {
                                log.info("Для запроса используется: %s".formatted(item));
                                log.info("Ожидаемая ошибка: %s".formatted(error));
                                ValidatableResponse response = ItemProvider.sendPostRequest(item);
                                log.info("Полученный ответ: %s".formatted(response.extract().asPrettyString()));
                                response.statusCode(HttpStatus.SC_BAD_REQUEST);
                                Error itemError = response.extract().response().as(new TypeRef<>() {
                                });
                                log.info("Полученная ошибка: {}", itemError);
                                assertEquals(
                                        error.getResult().getMessage(),
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

    private static Stream<Arguments> createItemSourceForUnsuccessfull() {
        return Stream.of(
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(getRandomInteger(0, 99999))
                                .name("obyavlenie")
                                .price(getRandomInteger(0, 999999))
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("передан некорректный идентификатор объявления").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче в sellerID 5 целых чисел"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(0)
                                .name("obyavlenie")
                                .price(getRandomInteger(0, 999999))
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("поле sellerID обязательно").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче sellerID = 0"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(null)
                                .name("obyavlenie")
                                .price(getRandomInteger(0, 999999))
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("поле sellerID обязательно").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче sellerID = null"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(getRandomInteger(-999999, -111111))
                                .name("obyavlenie")
                                .price(getRandomInteger(0, 999999))
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("передан некорректный идентификатор объявления").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче в sellerID отрицательного числа"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(getRandomInteger(111111, 999999))
                                .name("obyavlenieobyavlenieobyavlenieobyavlenieobyavlenieobyavlenieobyavlenieobyavlenieobyavlenieobyavlenieobyavlenieobyavlenie")
                                .price(getRandomInteger(0, 999999))
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("передан некорректный идентификатор объявления").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче в name очень длинного значения"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(getRandomInteger(111111, 999999))
                                .name(null)
                                .price(getRandomInteger(0, 999999))
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("поле name обязательно").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче в name пустого значения null"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(getRandomInteger(111111, 999999))
                                .name("true")
                                .price(getRandomInteger(0, 999999))
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("передан некорректный идентификатор объявления").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче в name = true"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(getRandomInteger(111111, 999999))
                                .name(Integer.toString(getRandomInteger(111111, 999999)))
                                .price(getRandomInteger(0, 999999))
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("передан некорректный идентификатор объявления").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче name в формате цифр"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(getRandomInteger(111111, 999999))
                                .name("obyavlenie")
                                .price(0)
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("поле price обязательно").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче в price 0"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(getRandomInteger(111111, 999999))
                                .name("obyavlenie")
                                .price(null)
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("поле price обязательно").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче в price пустого значения null"
                ),
                Arguments.of(
                        CreateItem.builder()
                                .sellerID(getRandomInteger(111111, 999999))
                                .name("obyavlenie")
                                .price(getRandomInteger(-999999, -2))
                                .statistics(Statistics.builder()
                                        .likes(getRandomInteger(1, 999))
                                        .viewCount(getRandomInteger(1, 999))
                                        .contacts(getRandomInteger(1, 999))
                                        .build())
                                .build(),
                        Error.builder()
                                .result(Result.builder().message("передан некорректный идентификатор объявления").build())
                                .build(),
                        "Неуспешное создание 400 Bad Request при передаче в price отрицательного числа"
                )
        );
    }
}
