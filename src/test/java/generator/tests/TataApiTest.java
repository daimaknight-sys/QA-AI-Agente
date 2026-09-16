package generator.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class TataApiTest {

    Playwright playwright;
    APIRequestContext request;

    @BeforeClass
    public void setUp() {
        playwright = Playwright.create();
        request = playwright.request().newContext();
    }

    @AfterClass
    public void tearDown() {
        request.dispose();
        playwright.close();
    }

    @Test(description = "POST https://www.tata.com.uy/api/graphql")
    public void testEndpoint1_StatusYTiempo() {
        RequestOptions options = RequestOptions.create().setMethod("POST");
        // Content-Type real capturado en el sniffing (o application/json por defecto).
        // Sin esto, Playwright manda el body como string plano sin declarar su tipo,
        // y algunos backends (ej. gateways GraphQL) pueden colgarse en vez de rechazarlo.
        options.setHeader("Content-Type", "application/json");
        options.setData("{\"operationName\":\"ValidateSession\",\"variables\":{\"session\":{\"currency\":{\"code\":\"UYU\",\"symbol\":\"$\"},\"locale\":\"es-uy\",\"channel\":\"{\\\"salesChannel\\\":\\\"4\\\",\\\"regionId\\\":\\\"\\\"}\",\"country\":\"URY\",\"postalCode\":null,\"person\":null},\"search\":\"\"}}");
        long inicio = System.currentTimeMillis();
        APIResponse response = request.fetch("https://www.tata.com.uy/api/graphql?operationName=ValidateSession", options);
        long duracionMs = System.currentTimeMillis() - inicio;

        assertTrue(response.status() >= 200 && response.status() < 300,
                "Se esperaba un status 2xx, se obtuvo " + response.status());
        assertTrue(duracionMs < 3000, "Response time " + duracionMs + "ms superó el umbral de 3000ms");
    }

    @Test(description = "GET https://www.tata.com.uy/api/graphql")
    public void testEndpoint2_StatusYTiempo() {
        RequestOptions options = RequestOptions.create().setMethod("GET");
        long inicio = System.currentTimeMillis();
        APIResponse response = request.fetch("https://www.tata.com.uy/api/graphql?operationName=ProductsQuery&variables=%7B%22first%22%3A18%2C%22after%22%3A%2218%22%2C%22sort%22%3A%22score_desc%22%2C%22term%22%3A%22%22%2C%22selectedFacets%22%3A%5B%7B%22key%22%3A%22productclusterids%22%2C%22value%22%3A%228688%22%7D%2C%7B%22key%22%3A%22channel%22%2C%22value%22%3A%22%7B%5C%22salesChannel%5C%22%3A%5C%224%5C%22%2C%5C%22regionId%5C%22%3A%5C%22%5C%22%7D%22%7D%2C%7B%22key%22%3A%22locale%22%2C%22value%22%3A%22es-uy%22%7D%5D%7D", options);
        long duracionMs = System.currentTimeMillis() - inicio;

        assertTrue(response.status() >= 200 && response.status() < 300,
                "Se esperaba un status 2xx, se obtuvo " + response.status());
        assertTrue(duracionMs < 3000, "Response time " + duracionMs + "ms superó el umbral de 3000ms");
    }

    @Test(description = "GET https://www.tata.com.uy/api/getSession")
    public void testEndpoint3_StatusYTiempo() {
        RequestOptions options = RequestOptions.create().setMethod("GET");
        long inicio = System.currentTimeMillis();
        APIResponse response = request.fetch("https://www.tata.com.uy/api/getSession", options);
        long duracionMs = System.currentTimeMillis() - inicio;

        assertTrue(response.status() >= 200 && response.status() < 300,
                "Se esperaba un status 2xx, se obtuvo " + response.status());
        assertTrue(duracionMs < 3000, "Response time " + duracionMs + "ms superó el umbral de 3000ms");
    }

    @Test(description = "GET https://tatauy.myvtex.com/_v/get-oca-promotion-details")
    public void testEndpoint4_StatusYTiempo() {
        RequestOptions options = RequestOptions.create().setMethod("GET");
        long inicio = System.currentTimeMillis();
        APIResponse response = request.fetch("https://tatauy.myvtex.com/_v/get-oca-promotion-details", options);
        long duracionMs = System.currentTimeMillis() - inicio;

        assertTrue(response.status() >= 200 && response.status() < 300,
                "Se esperaba un status 2xx, se obtuvo " + response.status());
        assertTrue(duracionMs < 3000, "Response time " + duracionMs + "ms superó el umbral de 3000ms");
    }

}
