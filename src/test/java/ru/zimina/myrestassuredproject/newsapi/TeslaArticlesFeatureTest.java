package ru.zimina.myrestassuredproject.newsapi;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class TeslaArticlesFeatureTest {

    public static final String API_KEY = "fb5580894b474875a5e16d38cecbbd92";
    public static final String BASE_URI = "http://newsapi.org/";
    public static final String endPointEverything = "v2/everything?";
    public static final String themeTesla = "q=tesla";
    public static String endPointTeslaArticles;
    public static String todayDate;
    public static RequestSpecification spec; //переменная для подготовки первоначального состояния запросов


    @BeforeAll
    static void setUp(){
        spec = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .log(LogDetail.ALL)
                .setAccept(ContentType.JSON)
                .build();

    }

    @BeforeEach
    void getTodayUrl(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime today = LocalDateTime.now();
        todayDate = formatter.format(today);

        endPointTeslaArticles = String.format("%s&from=%s&sortBy=publishedAt&apiKey=",themeTesla, todayDate);

    }

    @DisplayName("TAFT-1 Тест проверяет что в результате GET-запроса возвращается код 200 в течение 3 секунд")
    @Timeout(3)
    @Test
    void responseCodeTest(){

        given().spec(spec)
                .when().get(endPointEverything + endPointTeslaArticles + API_KEY)
                .then().statusCode(200); //проверяем код ответа от сервера
    }

    @DisplayName("TAFT-2 Проверяем, что ответ содержит заголовок Connection со значением keep-alive")
    @Test
    void isHeaderConnectionCorrectTest() {
        given().spec(spec)
                .when().get(endPointEverything + endPointTeslaArticles + API_KEY)
                .then().header("Connection", "keep-alive");
    }

    @DisplayName("TAFT-3 Проверяем, что поле totalResults не null и содержит числовое значение")
    @Test
    void isTotalResultsNotNull() {
        given().spec(spec)
                .when().get(endPointEverything + endPointTeslaArticles + API_KEY)
                .then().body("totalResults", Matchers.notNullValue())
                .and().body("totalResults", Matchers.any(Integer.class));

    }

    @DisplayName("TAFT-4 Проверяем, что первые 10 обьектов ответа содержат не null ссылки на статьи")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void isArticleUrlNotNull(int index) {
        given().spec(spec)
                .when().get(endPointEverything + endPointTeslaArticles + API_KEY)
                .then().body("articles[" + index + "].url", Matchers.notNullValue());
    }

    /*

    В этом тесте так и не смогла преобразовать обьект, скорее всего неверно составлен класс
    TeslaArticlesClass, но самой исправить не получилось

    @DisplayName("TAFT-5 Тест проверяет возможность преобразовать тело запроса" +
            "в массив объектов класса TeslaArticlesClass")
    @Test
    void getResponseBodyAsObjectTest(){

        TeslaArticlesClass[] teslaArticles =
                given().spec(spec)
                        .when().get(endPointEverything + endPointTeslaArticles + API_KEY)
                        .then().extract().body().as(TeslaArticlesClass[].class);

    }

     */


}
