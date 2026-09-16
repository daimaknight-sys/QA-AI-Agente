package generator;

import api.ApiEndpointInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiTestGenerator {

    private static final long UMBRAL_RESPONSE_TIME_MS = 3000;

    public String generate(String testClassName, List<ApiEndpointInfo> endpoints) {
        StringBuilder sb = new StringBuilder();

        sb.append("package generator.tests;\n\n");
        sb.append("import com.microsoft.playwright.*;\n");
        sb.append("import com.microsoft.playwright.options.RequestOptions;\n");
        sb.append("import org.testng.annotations.*;\n");
        sb.append("import static org.testng.Assert.*;\n\n");
        sb.append("public class ").append(testClassName).append(" {\n\n");
        sb.append("    Playwright playwright;\n");
        sb.append("    APIRequestContext request;\n\n");

        sb.append("    @BeforeClass\n");
        sb.append("    public void setUp() {\n");
        sb.append("        playwright = Playwright.create();\n");
        sb.append("        request = playwright.request().newContext();\n");
        sb.append("    }\n\n");

        sb.append("    @AfterClass\n");
        sb.append("    public void tearDown() {\n");
        sb.append("        request.dispose();\n");
        sb.append("        playwright.close();\n");
        sb.append("    }\n\n");

        Set<String> usedNames = new HashSet<>();
        int indice = 0;
        for (ApiEndpointInfo ep : endpoints) {
            indice++;
            String sufijo = uniqueName("Endpoint" + indice, usedNames);

            // ---- Test positivo: status code + tiempo de respuesta ----
            sb.append("    @Test(description = \"").append(escapeJavaString(ep.method + " " + ep.pathPattern))
                    .append("\")\n");
            sb.append("    public void test").append(sufijo).append("_StatusYTiempo() {\n");
            sb.append("        RequestOptions options = RequestOptions.create().setMethod(\"")
                    .append(escapeJavaString(ep.method)).append("\");\n");

            if (ep.requiereAuth) {
                sb.append("        // TODO: este endpoint requería auth al momento del sniffing.\n");
                sb.append("        // Se replican los headers capturados, pero el token puede EXPIRAR:\n");
                sb.append("        // si este test empieza a fallar con 401/403, hay que renovar la sesión\n");
                sb.append("        // (login real) en vez de asumir que el código está roto.\n");
                for (Map.Entry<String, String> header : ep.requestHeaders.entrySet()) {
                    String h = header.getKey().toLowerCase();
                    if (h.equals("authorization") || h.equals("cookie")) {
                        sb.append("        options.setHeader(\"").append(escapeJavaString(header.getKey()))
                                .append("\", \"").append(escapeJavaString(header.getValue())).append("\");\n");
                    }
                }
            }

            if (ep.requestBodySample != null && !ep.requestBodySample.isBlank()) {
                String contentType = null;
                for (Map.Entry<String, String> header : ep.requestHeaders.entrySet()) {
                    if (header.getKey().equalsIgnoreCase("content-type")) {
                        contentType = header.getValue();
                        break;
                    }
                }
                if (contentType == null) {
                    contentType = "application/json";
                }
                sb.append("        // Content-Type real capturado en el sniffing (o application/json por defecto).\n");
                sb.append("        // Sin esto, Playwright manda el body como string plano sin declarar su tipo,\n");
                sb.append("        // y algunos backends (ej. gateways GraphQL) pueden colgarse en vez de rechazarlo.\n");
                sb.append("        options.setHeader(\"Content-Type\", \"").append(escapeJavaString(contentType)).append("\");\n");
                sb.append("        options.setData(\"").append(escapeJavaString(ep.requestBodySample)).append("\");\n");
            }

            sb.append("        long inicio = System.currentTimeMillis();\n");
            sb.append("        APIResponse response = request.fetch(\"").append(escapeJavaString(ep.urlEjemplo))
                    .append("\", options);\n");
            sb.append("        long duracionMs = System.currentTimeMillis() - inicio;\n\n");

            sb.append("        assertTrue(response.status() >= 200 && response.status() < 300,\n");
            sb.append("                \"Se esperaba un status 2xx, se obtuvo \" + response.status());\n");
            sb.append("        assertTrue(duracionMs < ").append(UMBRAL_RESPONSE_TIME_MS)
                    .append(", \"Response time \" + duracionMs + \"ms superó el umbral de ")
                    .append(UMBRAL_RESPONSE_TIME_MS).append("ms\");\n");
            sb.append("    }\n\n");

            // ---- Test negativo: si requiere auth, sin credenciales debe rechazar ----
            if (ep.requiereAuth) {
                sb.append("    @Test(description = \"").append(escapeJavaString(ep.method + " " + ep.pathPattern + " sin auth"))
                        .append("\")\n");
                sb.append("    public void test").append(sufijo).append("_SinAuthRechaza() {\n");
                sb.append("        RequestOptions options = RequestOptions.create().setMethod(\"")
                        .append(escapeJavaString(ep.method)).append("\");\n");
                sb.append("        // Sin Authorization/Cookie: se espera que el servidor lo rechace.\n");
                sb.append("        APIResponse response = request.fetch(\"").append(escapeJavaString(ep.urlEjemplo))
                        .append("\", options);\n");
                sb.append("        assertTrue(response.status() == 401 || response.status() == 403,\n");
                sb.append("                \"Se esperaba 401/403 sin credenciales, se obtuvo \" + response.status());\n");
                sb.append("    }\n\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String uniqueName(String base, Set<String> used) {
        String candidate = base;
        int counter = 2;
        while (used.contains(candidate)) {
            candidate = base + counter;
            counter++;
        }
        used.add(candidate);
        return candidate;
    }

    private String escapeJavaString(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}