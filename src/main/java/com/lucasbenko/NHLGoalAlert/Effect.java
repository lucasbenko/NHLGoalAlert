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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.*;


public class Effect {
    private static OkHttpClient client = new OkHttpClient();
    public static void flashLights(Team t) {
        String macAddress = Main.MAC_ADDRESS;
        String model = Main.GOVEE_MODEL;

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        long effectID = t.getEffectID();

        String requestString = "{\"requestId\":\"1\",\"payload\":{\"sku\":\"" + model + "\",\"device\":\"" + macAddress + "\",\"capability\":{\"type\":\"devices.capabilities.dynamic_scene\",\"instance\":\"diyScene\",\"value\":" + effectID + "}}}";

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


    public static void PlayHorn(Team t) {
        Team team = t;
        String filePath = "res/2023-24/" + team.getName().replace(" ", "_") + ".mp3";
        new Thread(() -> {
            try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
                Player player = new Player(fileInputStream);
                player.play();
            } catch (JavaLayerException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void powerToggle(int value){
        String macAddress = Main.MAC_ADDRESS;
        String model = Main.GOVEE_MODEL;

        MediaType mediaType = MediaType.parse("application/json");

        String requestString = "{\"requestId\":\"" + "uuid" + "\",\"payload\":{\"sku\":\"" + model + "\",\"device\":\"" + macAddress + "\",\"capability\":{\"type\":\"devices.capabilities.on_off\",\"instance\":\"powerSwitch\",\"value\":" + value + "}}}";

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

    public static long getTeamEffect(String name) {
        String macAddress = Main.MAC_ADDRESS;
        String model = Main.GOVEE_MODEL;
        MediaType mediaType = MediaType.parse("application/json");
        String requestString = "{\"requestId\":\"1\",\"payload\":{\"sku\":\"" + model + "\",\"device\":\"" + macAddress + "\"}}";

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

}
