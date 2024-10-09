package com.lucasbenko.NHLGoalAlert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class GoveeController {
    private String API_KEY = null;

    public GoveeController(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public JSONObject getConnectedDevices() {
        if (!this.checkAPI()) {
            System.out.println("API Key not Set");
            return null;
        } else {
            HttpURLConnection con = null;

            try {
                URL url = new URL("https://developer-api.govee.com/v1/devices");
                con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Govee-API-Key", this.API_KEY);
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                return new JSONObject(this.getJSONResponse(con.getInputStream()));
            } catch (IOException var6) {
                IOException e = var6;

                try {
                    if (con.getResponseCode() == 401) {
                        System.out.println("Invalid API Key!");
                    } else {
                        e.printStackTrace();
                    }
                } catch (IOException var5) {
                    var5.printStackTrace();
                }

                return null;
            }
        }
    }

    public Map<String, List<String>> getRateLimit() throws MalformedURLException {
        if (!this.checkAPI()) {
            System.out.println("API Key not Set");
            return null;
        } else {
            URL url = new URL("https://developer-api.govee.com/v1/devices");
            HttpURLConnection con = null;

            try {
                con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Govee-API-Key", this.API_KEY);
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                return con.getHeaderFields();
            } catch (IOException var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    public int setBrightness(int brightness, String deviceMAC, String deviceModel) {
        if (brightness >= 0 && brightness <= 100) {
            if (!this.checkAPI()) {
                System.out.println("API Key not Set");
                return 403;
            } else {
                URL url = null;

                try {
                    url = new URL("https://developer-api.govee.com/v1/devices/control");
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("PUT");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Govee-API-Key", this.API_KEY);
                    con.setDoOutput(true);
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("device", deviceMAC);
                    requestBody.put("model", deviceModel);
                    JSONObject command = new JSONObject();
                    command.put("name", "brightness");
                    command.put("value", brightness);
                    requestBody.put("cmd", command);
                    byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                    OutputStream os = con.getOutputStream();
                    os.write(input, 0, input.length);
                    return con.getResponseCode();
                } catch (IOException var10) {
                    var10.printStackTrace();
                    return 400;
                }
            }
        } else {
            System.out.println("Invalid Brightness! Must be between 0-100");
            return 400;
        }
    }

    public int turnDeviceOnOff(Device onOff, String deviceMAC, String deviceModel) {
        if (!this.checkAPI()) {
            System.out.println("API Key not Set");
            return 403;
        } else {
            URL url = null;

            try {
                url = new URL("https://developer-api.govee.com/v1/devices/control");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("PUT");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Govee-API-Key", this.API_KEY);
                con.setDoOutput(true);
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                JSONObject requestBody = new JSONObject();
                requestBody.put("device", deviceMAC);
                requestBody.put("model", deviceModel);
                JSONObject command = new JSONObject();
                command.put("name", "turn");
                if (onOff == Device.OFF) {
                    command.put("value", "off");
                } else {
                    command.put("value", "on");
                }

                requestBody.put("cmd", command);
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                OutputStream os = con.getOutputStream();
                os.write(input, 0, input.length);
                return con.getResponseCode();
            } catch (IOException var10) {
                var10.printStackTrace();
                return 400;
            }
        }
    }

    public int setColorTem(int colorTem, String deviceMAC, String deviceModel) {
        if (colorTem >= 2000 && colorTem <= 9000) {
            if (!this.checkAPI()) {
                System.out.println("API Key not Set");
                return 403;
            } else {
                URL url = null;

                try {
                    url = new URL("https://developer-api.govee.com/v1/devices/control");
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("PUT");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Govee-API-Key", this.API_KEY);
                    con.setDoOutput(true);
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("device", deviceMAC);
                    requestBody.put("model", deviceModel);
                    JSONObject command = new JSONObject();
                    command.put("name", "colorTem");
                    command.put("value", colorTem);
                    requestBody.put("cmd", command);
                    byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                    OutputStream os = con.getOutputStream();
                    os.write(input, 0, input.length);
                    return con.getResponseCode();
                } catch (IOException var10) {
                    var10.printStackTrace();
                    return 400;
                }
            }
        } else {
            System.out.println("Invalid ColorTem! Must be between 2000-9000");
            return 400;
        }
    }

    public int setRGB(int r, int g, int b, String deviceMAC, String deviceModel) {
        if (!this.checkAPI()) {
            System.out.println("API Key not Set");
            return 403;
        } else if (r >= 0 && r <= 255) {
            if (g >= 0 && g <= 255) {
                if (b >= 0 && b <= 255) {
                    URL url = null;

                    try {
                        url = new URL("https://developer-api.govee.com/v1/devices/control");
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setRequestMethod("PUT");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setRequestProperty("Govee-API-Key", this.API_KEY);
                        con.setDoOutput(true);
                        con.setConnectTimeout(5000);
                        con.setReadTimeout(5000);
                        JSONObject requestBody = new JSONObject();
                        requestBody.put("device", deviceMAC);
                        requestBody.put("model", deviceModel);
                        JSONObject command = new JSONObject();
                        command.put("name", "color");
                        JSONObject rgb = new JSONObject();
                        rgb.put("r", r);
                        rgb.put("g", g);
                        rgb.put("b", b);
                        command.put("value", rgb);
                        requestBody.put("cmd", command);
                        byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                        OutputStream os = con.getOutputStream();
                        os.write(input, 0, input.length);
                        return con.getResponseCode();
                    } catch (IOException var13) {
                        var13.printStackTrace();
                        return 400;
                    }
                } else {
                    System.out.println("Invalid r value! Must be between 0-255");
                    return 400;
                }
            } else {
                System.out.println("Invalid r value! Must be between 0-255");
                return 400;
            }
        } else {
            System.out.println("Invalid r value! Must be between 0-255");
            return 400;
        }
    }

    public JSONObject getDeviceState(String deviceMAC, String deviceModel) {
        if (!this.checkAPI()) {
            System.out.println("API Key not Set");
            return null;
        } else {
            HttpURLConnection con = null;

            try {
                URL url = new URL("https://developer-api.govee.com/v1/devices/state?device=" + deviceMAC + "&model=" + deviceModel);
                con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Govee-API-Key", this.API_KEY);
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                return new JSONObject(this.getJSONResponse(con.getInputStream()));
            } catch (IOException var8) {
                IOException e = var8;

                try {
                    if (con.getResponseCode() == 401) {
                        System.out.println("Invalid API Key!");
                    } else {
                        e.printStackTrace();
                    }
                } catch (IOException var7) {
                    var7.printStackTrace();
                }

                return null;
            }
        }
    }

    private String getJSONResponse(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String output;
        try {
            while((output = br.readLine()) != null) {
                sb.append(output);
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return sb.toString();
    }

    private boolean checkAPI() {
        return this.API_KEY != null;
    }

    public void updateAPIKey(String API_KEY) {
        this.API_KEY = API_KEY;
    }
}
