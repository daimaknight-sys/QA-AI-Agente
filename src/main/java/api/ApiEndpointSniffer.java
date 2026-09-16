package api;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Detecta endpoints de API "escuchando" el tráfico de red de una Page mientras
 * se navega o crawlea. No requiere documentación: agarra cualquier request
 * xhr/fetch, lo agrupa por método + patrón de URL (normalizando ids), y guarda
 * un ejemplo de request/response para inferir schema y validaciones más adelante.
 *
 * Uso: instanciar UNA vez por análisis de sitio, llamar attach(page) antes de
 * navegar/crawlear, y leer getEndpoints() al final.
 */
public class ApiEndpointSniffer {

    private final Map<String, ApiEndpointInfo> endpoints = new LinkedHashMap<>();

    public void attach(Page page) {
        page.onResponse(this::registrar);
    }

    private void registrar(Response response) {
        try {
            Request request = response.request();
            String resourceType = request.resourceType();

            // Solo nos interesan llamadas de API, no HTML/CSS/JS/imágenes/fuentes
            if (!"xhr".equals(resourceType) && !"fetch".equals(resourceType)) {
                return;
            }

            String method = request.method();
            String pathPattern = normalizarPath(request.url());
            String key = method + " " + pathPattern;

            ApiEndpointInfo info = endpoints.computeIfAbsent(key, k -> new ApiEndpointInfo());
            info.method = method;
            info.pathPattern = pathPattern;
            info.urlEjemplo = request.url();

            try {
                info.statusCode = response.status();
            } catch (Exception ignored) {}

            try {
                info.requestHeaders = new LinkedHashMap<>(request.headers());
            } catch (Exception ignored) {}

            try {
                info.responseHeaders = new LinkedHashMap<>(response.headers());
            } catch (Exception ignored) {}

            info.requiereAuth = info.requestHeaders.containsKey("authorization")
                    || info.requestHeaders.containsKey("cookie");

            try {
                info.requestBodySample = request.postData();
            } catch (Exception ignored) {}

            try {
                // Solo guardamos si parece texto/JSON; evitamos volcar binarios grandes
                String contentType = info.responseHeaders.getOrDefault("content-type", "");
                if (contentType.contains("json") || contentType.contains("text")) {
                    String body = response.text();
                    info.responseBodySample = body.length() > 5000 ? body.substring(0, 5000) + "..." : body;
                }
            } catch (Exception ignored) {}

            try {
                info.responseTimeMs = (long) request.timing().responseEnd;
            } catch (Exception ignored) {}

        } catch (Exception e) {
            // Un request individual fallando no debe tumbar el crawl completo
        }
    }

    /** Reemplaza segmentos tipo id (numéricos o UUID-like) por {id}, para agrupar variantes del mismo endpoint. */
    private String normalizarPath(String url) {
        try {
            URI uri = URI.create(url);
            String path = uri.getPath();
            if (path == null) path = "";
            String[] partes = path.split("/");
            StringBuilder sb = new StringBuilder();
            for (String parte : partes) {
                if (parte.isEmpty()) continue;
                sb.append("/");
                if (parte.matches("\\d+") || parte.matches("[0-9a-fA-F-]{8,}")) {
                    sb.append("{id}");
                } else {
                    sb.append(parte);
                }
            }
            String scheme = uri.getScheme() != null ? uri.getScheme() : "https";
            String host = uri.getHost() != null ? uri.getHost() : "";
            String port = uri.getPort() > 0 ? ":" + uri.getPort() : "";
            return scheme + "://" + host + port + sb;
        } catch (Exception e) {
            return url;
        }
    }

    public List<ApiEndpointInfo> getEndpoints() {
        return new ArrayList<>(endpoints.values());
    }

    // Patrones que identifican endpoints de API "reales" del sitio, para descartar
    // tracking de terceros (Google Analytics, DoubleClick, useinsider, etc.) y
    // los page-data.json estáticos que Gatsby genera por cada página.
    private static final List<String> PATRONES_API_REAL = List.of("/api/", "/_v/", "/graphql");

    /** Igual que getEndpoints(), pero solo devuelve los que matchean un patrón de API real. */
    public List<ApiEndpointInfo> getEndpointsFiltrados() {
        List<ApiEndpointInfo> filtrados = new ArrayList<>();
        for (ApiEndpointInfo ep : endpoints.values()) {
            if (esEndpointReal(ep.pathPattern)) {
                filtrados.add(ep);
            }
        }
        return filtrados;
    }

    private boolean esEndpointReal(String pathPattern) {
        if (pathPattern == null) return false;
        boolean matcheaPatron = false;
        for (String patron : PATRONES_API_REAL) {
            if (pathPattern.contains(patron)) {
                matcheaPatron = true;
                break;
            }
        }
        if (!matcheaPatron) return false;
        return !esDominioDeTrackingTercero(pathPattern);
    }

    // Dominios conocidos de tracking/analytics/publicidad de terceros. Aparecen en la
    // mayoría de los sitios de e-commerce sin importar la plataforma, y suelen exponer
    // rutas que matchean /api/ (ej. falcon.useinsider.com/api/v2/...) sin ser parte de
    // la API real del sitio que se está analizando.
    private static final List<String> DOMINIOS_TRACKING_TERCEROS = List.of(
            "google-analytics.com", "analytics.google.com", "doubleclick.net",
            "googletagmanager.com", "google.com", "useinsider.com", "facebook.com",
            "facebook.net", "hotjar.com", "segment.io", "segment.com", "clarity.ms",
            "criteo.com", "adservice.google.com", "gstatic.com"
    );

    private boolean esDominioDeTrackingTercero(String pathPattern) {
        try {
            URI uri = URI.create(pathPattern);
            String host = uri.getHost();
            if (host == null) return false;
            for (String dominio : DOMINIOS_TRACKING_TERCEROS) {
                if (host.equals(dominio) || host.endsWith("." + dominio)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}