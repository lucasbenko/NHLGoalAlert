package com.lucasbenko.NHLGoalAlert;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;

import java.io.FileInputStream;
import java.io.*;


public class GoveeEffect {
    private static OkHttpClient client = new OkHttpClient();
    public static void flashLights(Team t, String macAddressLight) {
        String model = Main.GOVEE_MODEL;

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");

            long effectID = t.getEffectID();

            String requestString = "{\"requestId\":\"1\",\"payload\":{\"sku\":\"" + model + "\",\"device\":\"" + macAddressLight + "\",\"capability\":{\"type\":\"devices.capabilities.dynamic_scene\",\"instance\":\"diyScene\",\"value\":" + effectID + "}}}";

            RequestBody body = RequestBody.create(mediaType, requestString);
            Request request = new Request.Builder()
                    .url("https://openapi.api.govee.com/router/api/v1/device/control")  // Correct URL
                    .post(body)  // Use POST
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Govee-API-Key", Main.GOVEE_API_KEY)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                response.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }

    public static void toggleDevice(String macAddress, String model, int state) {
        MediaType mediaType = MediaType.parse("application/json");

        String requestString = "{\"requestId\":\"" + "uuid" + "\",\"payload\":{\"sku\":\"" + model + "\",\"device\":\"" + macAddress + "\",\"capability\":{\"type\":\"devices.capabilities.on_off\",\"instance\":\"powerSwitch\",\"value\":" + state + "}}}";
        RequestBody body = RequestBody.create(mediaType, requestString);
        Request request = new Request.Builder()
                .url("https://openapi.api.govee.com/router/api/v1/device/control")
                .post(body)  // Use POST
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Govee-API-Key", Main.GOVEE_API_KEY)
                .build();

        try {
            Response response = client.newCall(request).execute();
            response.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void PlayHorn(Team t, int option) {
        // 1 = away team 0 = home team
        Team team = t;

        new Thread(() -> {
            String filePath = "";
            if (option == 0){
                filePath = "res/2023-24/" + team.getName().replace(" ", "_") + "_Short.mp3";
            }else {
                filePath = "res/2023-24/" + team.getName().replace(" ", "_") + ".mp3";
            }
            try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
                Player player = new Player(fileInputStream);
                player.play();
            } catch (JavaLayerException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static long getTeamEffect(String name) {
        String macAddressLight = Main.MAC_ADDRESS_LIGHT;
        String model = Main.GOVEE_MODEL;

        MediaType mediaType = MediaType.parse("application/json");
        String requestString = "{\"requestId\":\"1\",\"payload\":{\"sku\":\"" + model + "\",\"device\":\"" + macAddressLight + "\"}}";

        RequestBody body = RequestBody.create(mediaType, requestString);
        Request request = new Request.Builder()
                .url("https://openapi.api.govee.com/router/api/v1/device/diy-scenes")  // Correct URL
                .post(body)  // Use POST
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Govee-API-Key", Main.GOVEE_API_KEY)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                String errorBody = response.body().string();
                System.out.println("Error: " + errorBody);
            } else {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                JsonObject payload = jsonObject.getAsJsonObject("payload");
                JsonArray capabilitiesArray = payload.getAsJsonArray("capabilities");

                for (JsonElement capabilityElem : capabilitiesArray) {
                    JsonObject capabilities = capabilityElem.getAsJsonObject();
                    JsonObject parameters = capabilities.getAsJsonObject("parameters");
                    JsonArray optionsArray = parameters.getAsJsonArray("options");

                    for (JsonElement optionElem : optionsArray) {
                        JsonObject effectObj = optionElem.getAsJsonObject();
                        String teamName = effectObj.get("name").getAsString();
                        if (teamName.equalsIgnoreCase(name.replace(" ", "_"))) {
                            long effectID = effectObj.get("value").getAsLong();
                            return effectID;
                        }
                    }
                }
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void listDevices() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://openapi.api.govee.com/router/api/v1/user/devices")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Govee-API-Key", Main.GOVEE_API_KEY)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println("Devices: " + responseBody);
            } else {
                System.out.println("Failed to get devices. Code: " + response.code());
            }
            response.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
