package api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Información recolectada de un endpoint de API, ya sea por sniffing de red
 * durante el crawl, o completada/verificada contra un spec OpenAPI.
 */
public class ApiEndpointInfo {

    public String method;          // GET, POST, PUT, DELETE, ...
    public String pathPattern;     // URL con segmentos tipo id normalizados, ej: https://site.com/api/users/{id}
    public String urlEjemplo;      // una URL real de ejemplo tal como se vio en el sniffing

    public int statusCode;         // último status code observado
    public long responseTimeMs;    // tiempo de respuesta observado (ms)

    public Map<String, String> requestHeaders = new LinkedHashMap<>();
    public Map<String, String> responseHeaders = new LinkedHashMap<>();

    public String requestBodySample;   // cuerpo de un request real, si lo hubo (POST/PUT)
    public String responseBodySample;  // cuerpo de la respuesta, para inferir el schema

    public boolean requiereAuth;   // true si se vio header Authorization o Cookie en el request

    // Se completa después, si hay spec OpenAPI disponible para este endpoint
    public boolean confirmadoPorSpec = false;
    public String schemaEsperado;  // ej: definición JSON Schema tomada del spec, si existe

    @Override
    public String toString() {
        return method + " " + pathPattern + " → " + statusCode
                + (requiereAuth ? " [auth]" : "")
                + (confirmadoPorSpec ? " [spec]" : "");
    }
}