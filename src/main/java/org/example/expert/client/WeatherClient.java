package org.example.expert.client;

import org.example.expert.client.dto.WeatherDto;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.ServerException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class WeatherClient {

    private final RestTemplate restTemplate;

    public WeatherClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String getTodayWeather() {
        ResponseEntity<List<WeatherDto>> responseEntity =
                restTemplate.exchange(
                        buildWeatherApiUri(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<WeatherDto>>() {
                        }
                );
        List<WeatherDto> weatherList = responseEntity.getBody();
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new ServerException(ErrorCode.FAILED_TO_GET_WEATHER_DATA);
        }
        if (weatherList == null || weatherList.isEmpty()) {
            throw new ServerException(ErrorCode.TOTAL_WEATHER_DATA_NOT_FOUND);
        }
        String today = getCurrentDate();
        for (WeatherDto weatherDto : weatherList) {
            if (today.equals(weatherDto.getDate())) {

                return weatherDto.getWeather();
            }
        }
        throw new ServerException(ErrorCode.TODAY_WEATHER_DATA_NOT_FOUND);
    }

    private URI buildWeatherApiUri() {
        return UriComponentsBuilder
                .fromUriString("https://f-api.github.io")
                .path("/f-api/weather.json")
                .encode()
                .build()
                .toUri();
    }

    private String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        return LocalDate.now().format(formatter);
    }
}
