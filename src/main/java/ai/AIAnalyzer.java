package ai;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;

public class AIAnalyzer {

    private static final String API_KEY = System.getenv("OPENROUTER_API_KEY"); // reemplazá con tu key completa
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public String analizarResultados(String resultadosTest) {
        String prompt = "Eres un experto en QA. Analizá estos resultados de testing y respondé en español:\n"
                + "1. ¿Qué tests pasaron y cuáles fallaron?\n"
                + "2. ¿Cuál es la causa probable de cada fallo?\n"
                + "3. ¿Qué recomendás corregir primero?\n"
                + "Resultados:\n" + resultadosTest;

        String requestBody = "{"
                + "\"model\": \"nvidia/nemotron-3-nano-30b-a3b:free\","
                + "\"messages\": [{\"role\": \"user\", \"content\": " + gson.toJson(prompt) + "}]"
                + "}";

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("HTTP-Referer", "https://github.com/QA-AI-Agent")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            System.out.println("Respuesta cruda: " + responseBody);
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            if (json.getAsJsonArray("choices") == null) {
                return "Error de API: " + responseBody;
            }
            return json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject()
                    .get("content").getAsString();
        } catch (IOException e) {
            return "Error al conectar con la IA: " + e.getMessage();
        }
    }
}