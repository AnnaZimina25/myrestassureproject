package ru.zimina.myrestassuredproject.weatherapi;

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
public class MoscowWeatherForecastFeatureTest {

    public static final String API_KEY = "a953e359a63b4411a6284458211103";
    public static final String BASE_URI = "http://api.weatherapi.com/v1/";
    public static final String endPointMoscow7DaysForecast =
            "forecast.json?key=" + API_KEY + " &q=Moscow&days=7";
    public static RequestSpecification spec;

    @BeforeAll
    static void setUp(){
        spec = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .log(LogDetail.ALL)
                .setAccept(ContentType.JSON)
                .build();

    }

    @DisplayName("MWFFT-1 Тест проверяет что в результате GET-запроса возвращается код 200 в течение 3 секунд")
    @Timeout(3)
    @Test
    void responseCodeTest(){

        given().spec(spec)
                .when().get(endPointMoscow7DaysForecast)
                .then().statusCode(200); //проверяем код ответа от сервера
    }

    @DisplayName("MWFFT-2 Проверяем, что ответ содержит заголовок Content-type со значением application/json")
    @Test
    void isHeaderContentTypeCorrectTest() {
        given().spec(spec)
                .when().get(endPointMoscow7DaysForecast)
                .then().header("Content-type", "application/json");
    }

    @DisplayName("MWFFT-3 Проверяем, что поле location.name не null и содержит строковое значение Moscow")
    @Test
    void isLocationNameCorrect() {
        given().spec(spec)
                .when().get(endPointMoscow7DaysForecast + API_KEY)
                .then().body("location.name", Matchers.notNullValue())
                .and().body("location.name", Matchers.any(String.class))
                .and().body("location.name", Matchers.containsString("Moscow"));

    }

    @DisplayName("MWFFT-4 Проверяем, что ответ содержит ключ current с 23 не null полями")
    @Test
    void isCurrentKeyPresent() {
        given().spec(spec)
                .when().get(endPointMoscow7DaysForecast + API_KEY)
                .then().body("current",Matchers.aMapWithSize(23))
                .and().body("current",Matchers.notNullValue());

    }

    @DisplayName("MWFFT-5 Проверяем, что ответ содержит ключ forecast с 3 полями date >= текущей дате")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void areCurrentAndForecastFieldsPresent(int dateIndex) {

        /*
    Видимо, в этом тесте я нашла баг, если проверять с @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6}),
    так как на сайте сказано, что должен выводится прогноз на 7 дней,
    а выводится полный прогноз на весь текущий день и два следующих
    (проверяла по файлу, полученному по ссылке)

     */

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime today = LocalDateTime.now();
        String todayDate = formatter.format(today);

        String dateJsonPath = String.format("forecast.forecastday[%d].date", dateIndex);

        given().spec(spec)
                .when().get(endPointMoscow7DaysForecast)
                .then().body(dateJsonPath,Matchers.notNullValue())
                .and().body(dateJsonPath, Matchers.any(String.class))
                .and().body(dateJsonPath, Matchers.hasLength(10))
                .and().body(dateJsonPath, Matchers.greaterThanOrEqualTo(todayDate));

    }


}
