package com.example.weatherapp;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class MainController {

    @FXML
    private TextField searchBar;
    @FXML
    private ListView<String> locationList;
    @FXML
    private Text locationText;
    @FXML
    private Text currentTemperatureText;
    @FXML
    private Text dailyTemperatureText;
    @FXML
    private Text sunriseText;
    @FXML
    private Text sunsetText;
    @FXML
    private ImageView weatherIcon;
    @FXML
    private ImageView sunriseIcon;
    @FXML
    private ImageView sunsetIcon;
    @FXML
    private ImageView arrowIcon;
    public static String locationName = "New York";
    String apiKey = "enter your api key here";

    public void initialize() throws URISyntaxException, IOException, InterruptedException {
        Parent container = locationList.getParent();
        Platform.runLater(container::requestFocus);
        locationList.getItems().clear();
        locationList.setVisible(false);
        arrowIcon.setImage(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/arrows-rotate-solid.png"))));
        sunriseIcon.setImage(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/sunrise.png"))));
        sunsetIcon.setImage(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/sunset.png"))));
        locationText.setText(locationName);
        locationList.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                locationName = locationList.getSelectionModel().getSelectedItem();
                locationList.setVisible(false);
                try {
                    updateWeather();
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        container.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (!locationList.getBoundsInParent().contains(event.getX(), event.getY())){
                locationList.setVisible(false);
            }
            if (!searchBar.getBoundsInParent().contains(event.getX(), event.getY())){
                Platform.runLater(container::requestFocus);
            }
        });
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest getWeatherInfo = HttpRequest.newBuilder()
                .uri(new URI("http://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + locationName.replace(" ", "%20") + "&aqi=no"))
                .GET()
                .build();
        HttpResponse<String> weatherInfoResponse = httpClient.send(getWeatherInfo, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonObject = new JSONObject(weatherInfoResponse.body());
        locationName = jsonObject.getJSONObject("location").getString("name");
        locationText.setText(locationName);
        currentTemperatureText.setText(jsonObject.getJSONObject("current").getDouble("temp_c") + "°C");
        weatherIcon.setImage(new Image("https:" + jsonObject.getJSONObject("current").getJSONObject("condition").getString("icon")));
        dailyTemperatureText.setText(
                jsonObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getDouble("mintemp_c")
                + "° · "
                + jsonObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getDouble("maxtemp_c")
                + "°");
        sunriseText.setText(jsonObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("astro").getString("sunrise"));
        sunsetText.setText(jsonObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("astro").getString("sunset"));
    }

    public void updateWeather() throws IOException, InterruptedException, URISyntaxException {
        System.out.println(locationName);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest getWeatherInfo = HttpRequest.newBuilder()
                .uri(new URI("http://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + locationName.replace(" ", "%20") + "&aqi=no"))
                .GET()
                .build();
        System.out.println(getWeatherInfo.toString());
        HttpResponse<String> weatherInfoResponse = httpClient.send(getWeatherInfo, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonObject = new JSONObject(weatherInfoResponse.body());
        locationName = jsonObject.getJSONObject("location").getString("name");
        locationText.setText(locationName);
        currentTemperatureText.setText(jsonObject.getJSONObject("current").getDouble("temp_c") + "°C");
        weatherIcon.setImage(new Image("https:" + jsonObject.getJSONObject("current").getJSONObject("condition").getString("icon")));
        dailyTemperatureText.setText(
                jsonObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getDouble("mintemp_c")
                        + "° · "
                        + jsonObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getDouble("maxtemp_c")
                        + "°");
        sunriseText.setText(jsonObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("astro").getString("sunrise"));
        sunsetText.setText(jsonObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("astro").getString("sunset"));
    }

    public void rotate() throws IOException, URISyntaxException, InterruptedException {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), arrowIcon);
        rotateTransition.setByAngle(180);
        rotateTransition.setCycleCount(1);
        rotateTransition.setAutoReverse(false);
        rotateTransition.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        rotateTransition.play();
        updateWeather();
    }

    public void filterLocations() throws IOException, InterruptedException, URISyntaxException {
        locationList.getItems().clear();
        ObservableList<String> locations = FXCollections.observableArrayList();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest getFilteredLocations = HttpRequest.newBuilder()
                .uri(new URI("http://api.weatherapi.com/v1/search.json?key=" + apiKey + "&q=" + searchBar.getText().replace(" ", "_")))
                .GET()
                .build();
        HttpResponse<String> filteredLocationsResponse = httpClient.send(getFilteredLocations, HttpResponse.BodyHandlers.ofString());
        if (!searchBar.getText().isBlank()) {
            JSONArray jsonArray = new JSONArray(filteredLocationsResponse.body());
            if (jsonArray.isEmpty()) {
                locationList.setVisible(false);
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    locations.add(jsonObject.getString("name"));
                    locationList.setStyle("-fx-control-inner-background: #8bd6f1;");
                    locationList.getItems().add(jsonObject.getString("name"));
                    locationList.setPrefHeight(jsonArray.length() * 24.5);
                    locationList.setVisible(true);
                }
            }
        }
        else{
            locationList.setVisible(false);
        }
    }
}