package com.slava.dao;

import com.slava.entities.Location;
import com.slava.model.Coordinates;
import com.slava.model.OpenWeatherAPI;
import com.slava.model.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class WeatherDao implements IWeatherDao<Weather> {

    private final OpenWeatherAPI openWeatherAPI;
    private final RestTemplate restTemplate;

    @Autowired
    public WeatherDao(OpenWeatherAPI openWeatherAPI, RestTemplate restTemplate) {
        this.openWeatherAPI = openWeatherAPI;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Weather> getWeather(String latitude, String longitude) {

        String urlencoded = UriComponentsBuilder.fromHttpUrl(openWeatherAPI.getApiCurrentWeatherService())
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", openWeatherAPI.getAPP_ID())
                .queryParam("units", "metric")
                .encode()
                .toUriString();

        return fetchWeather(urlencoded);
    }

    @Override
    public List<Optional<Weather>> searchWeather(String locationName) {
        String urlencoded = UriComponentsBuilder.fromHttpUrl(openWeatherAPI.getApiGeocoding())
                .queryParam("q", locationName)
                .queryParam("limit", "5")
                .queryParam("appid", openWeatherAPI.getAPP_ID())
                .encode()
                .toUriString();

        ResponseEntity<Coordinates[]> geoResponse = restTemplate.getForEntity(urlencoded, Coordinates[].class);
        List<Coordinates> coordinates = List.of(geoResponse.getBody());
        List<Optional<Weather>> weathers = coordinates.stream()
                .map(c -> getWeather(c.getLat().toString(), c.getLon().toString()))
                .collect(Collectors.toList());

        return weathers;
    }

    private Optional<Weather> fetchWeather(String url) {
        try {
            Weather weather = restTemplate.getForObject(url, Weather.class);
            return Optional.ofNullable(weather);
        } catch (Exception e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
            return Optional.empty();
        }
    }

}
