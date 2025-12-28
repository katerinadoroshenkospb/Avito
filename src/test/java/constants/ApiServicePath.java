package constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiServicePath {

    public static final String BASE_URL = "https://qa-internship.avito.com";

    public static final String CREATE_ITEM = "/api/1/item";

    public static final String GET_ITEM_BY_ID = "/api/1/item/{id}";

    public static final String GET_ITEM_BY_SELLER_ID = " /api/1/{sellerID}/item";

    public static final String GET_STATISTIC_BY_ID = "/api/1/statistic/{id}";
}
