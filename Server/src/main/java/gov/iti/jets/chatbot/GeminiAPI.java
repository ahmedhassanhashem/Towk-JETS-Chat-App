package gov.iti.jets.chatbot;
import java.io.IOException;
import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import gov.iti.jets.config.APIConfig;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import okhttp3.*;
public class GeminiAPI {
    private static String url;
    private static OkHttpClient client = new OkHttpClient();
    static {
        try(InputStream apiConfigStream = GeminiAPI.class.getResourceAsStream("/gemini-api.xml")) {
            Unmarshaller unmarshaller = JAXBContext.newInstance(APIConfig.class).createUnmarshaller();
            APIConfig apiConfig = (APIConfig) unmarshaller.unmarshal(apiConfigStream);
            url = apiConfig.getUrl().concat(apiConfig.getKey());
        } catch (Exception e) {e.printStackTrace();}
    }
    public static String getBotResponse(String userMessage) {
        try {
            JSONObject content = new JSONObject();
            content.put("role", "user");
            content.put("parts", new JSONArray().put(new JSONObject().put("text", userMessage)));
            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", new JSONArray().put(content));
            Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.getJSONArray("candidates").getJSONObject(0)
                        .getJSONObject("content").getJSONArray("parts")
                        .getJSONObject(0).getString("text");
            } else {
                System.err.println("Error response: " + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Sorry, I couldn't process your request.";
    }

}