package home.com.epd42nkcu;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {

    public static String token_head = "Bearer ";
    public static String demo_for_epd42_token = token_head + "gvLPUGu4~)V.@(UE![2D]AQK><p|+@[T%)vPDW*v!nkdln850.l({D#H#A*>+@<{";

    public static int status_code = 0;

    public static String excutePostJson(String targetURL, JSONObject jsonObject) {

        String result_sb = "";

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(targetURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", demo_for_epd42_token);

            String input = jsonObject.toString();
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            status_code = conn.getResponseCode();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result_sb = sb.toString();
        return result_sb;
    }
}
