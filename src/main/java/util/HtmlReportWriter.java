package util;

import analyzer.PageInfo;
import java.io.FileWriter;
import java.io.IOException;

public class HtmlReportWriter {

    public void generar(String rutaArchivo, String url, PageInfo info,
                        String casos, String resultados, String reporteXss) throws IOException {

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>");
        html.append("<title>Reporte QA - Agente IA</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; background:#f4f4f9; color:#222; padding:24px; }");
        html.append("h1 { color:#7c6af7; }");
        html.append("h2 { border-bottom:2px solid #7c6af7; padding-bottom:6px; margin-top:32px; }");
        html.append("section { background:white; border-radius:8px; padding:16px 24px; margin-bottom:16px; box-shadow:0 1px 4px rgba(0,0,0,0.1); }");
        html.append("pre { white-space:pre-wrap; font-family:Consolas,monospace; font-size:13px; }");
        html.append(".ok { color:#2e7d32; }");
        html.append(".warn { color:#c62828; font-weight:bold; }");
        html.append("table { border-collapse:collapse; width:100%; }");
        html.append("td, th { border:1px solid #ddd; padding:8px; text-align:left; }");
        html.append("</style></head><body>");

        html.append("<h1>Reporte de Análisis QA</h1>");
        html.append("<p><strong>URL analizada:</strong> ").append(url).append("</p>");

        html.append("<section><h2>Resumen de la página</h2>");
        html.append("<table>");
        html.append("<tr><th>Botones encontrados</th><td>").append(info.buttonTexts.size()).append("</td></tr>");
        html.append("<tr><th>Inputs encontrados</th><td>").append(info.inputNames.size()).append("</td></tr>");
        html.append("<tr><th>Formularios</th><td>").append(info.hasForm ? "Sí" : "No").append("</td></tr>");
        html.append("</table></section>");

        html.append("<section><h2>Inputs</h2><pre>").append(String.join("\n", info.inputNames)).append("</pre></section>");

        html.append("<section><h2>Botones</h2><pre>").append(String.join("\n", info.buttonTexts)).append("</pre></section>");

        html.append("<section><h2>Casos de test generados</h2><pre>").append(escapar(casos)).append("</pre></section>");

        html.append("<section><h2>Resultados de ejecución</h2><pre>").append(escapar(resultados)).append("</pre></section>");

        boolean sinHallazgos = reporteXss.contains("No se detectaron");
        html.append("<section><h2>Análisis de seguridad (XSS reflejado)</h2>");
        html.append("<pre class='").append(sinHallazgos ? "ok" : "warn").append("'>")
                .append(escapar(reporteXss)).append("</pre></section>");

        html.append("</body></html>");

        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write(html.toString());
        }
    }

    // Evita que el HTML de otros textos rompa el layout del reporte
    private String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}