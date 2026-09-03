package agent;

import ai.AIAnalyzer;
import analyzer.PageAnalyzer;
import analyzer.PageInfo;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import com.microsoft.playwright.options.LoadState;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import execution.PlaywrightTestExecutor;
import generator.TestCaseGenerator;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import security.XssScanner;
import util.HtmlReportWriter;
import crawler.WebCrawler;

public class AgentServer {

    private static String contextoAnalisis = "";
    private static String urlAnalizada = "";

    private static void handleReporte(HttpExchange exchange) throws IOException {
        byte[] bytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("reporte_qa.html"));
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    public static void main(String[] args) throws Exception {

        String url = "https://www.gub.uy";

        System.out.println("🤖 Iniciando análisis de: " + url);

        // analyzer la página al inicio
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true).setTimeout(30000));
            Page page = browser.newPage();
            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            PageAnalyzer analyzer = new PageAnalyzer();
            PageInfo info = analyzer.analyze(page);

            WebCrawler crawler = new WebCrawler();
            java.util.List<WebCrawler.PaginaResultado> paginasSitio = crawler.crawlMultiple(url, 50);

            int totalInputsSitio = 0;
            int totalBotonesSitio = 0;
            int totalFormsSitio = 0;
            StringBuilder resumenPaginas = new StringBuilder();

            for (WebCrawler.PaginaResultado pagina : paginasSitio) {
                totalInputsSitio += pagina.info.inputNames.size();
                totalBotonesSitio += pagina.info.buttonTexts.size();
                if (pagina.info.hasForm) totalFormsSitio++;
                resumenPaginas.append("- ").append(pagina.url)
                        .append(" (inputs: ").append(pagina.info.inputNames.size())
                        .append(", botones: ").append(pagina.info.buttonTexts.size())
                        .append(", form: ").append(pagina.info.hasForm ? "sí" : "no")
                        .append(")\n");
            }

            String reporteSitio = "Páginas analizadas del sitio: " + paginasSitio.size() + "\n"
                    + "Total de inputs en el sitio: " + totalInputsSitio + "\n"
                    + "Total de botones en el sitio: " + totalBotonesSitio + "\n"
                    + "Páginas con formulario: " + totalFormsSitio + "\n\n"
                    + "Detalle por página:\n" + resumenPaginas;

            TestCaseGenerator generator = new TestCaseGenerator();
            String casos = generator.generar(info);

            PlaywrightTestExecutor executor = new PlaywrightTestExecutor(page);
            String resultados = executor.ejecutarTests(info);

            XssScanner xssScanner = new XssScanner();
            java.util.List<String> hallazgosXss = xssScanner.escanear(url);
            String reporteXss = hallazgosXss.isEmpty()
                    ? "No se detectaron reflejos de XSS en los inputs analizados."
                    : String.join("\n", hallazgosXss);

            contextoAnalisis = "URL analizada: " + url + "\n"
                    + "Botones encontrados: " + info.buttonTexts.size() + "\n"
                    + "Inputs encontrados: " + info.inputNames.size() + "\n"
                    + "Formularios: " + (info.hasForm ? "Sí" : "No") + "\n"
                    + "Nombres de inputs: " + info.inputNames + "\n"
                    + "Textos de botones: " + info.buttonTexts + "\n\n"
                    + "Casos de test generados:\n" + casos + "\n"
                    + "Resultados reales:\n" + resultados + "\n\n"
                    + "Análisis de seguridad (XSS reflejado):\n" + reporteXss + "\n\n"
                    + "Análisis del sitio completo (crawling multi-página):\n" + reporteSitio;

            HtmlReportWriter htmlReport = new HtmlReportWriter();
            htmlReport.generar("reporte_qa.html", url, info, casos, resultados, reporteXss);

            urlAnalizada = url;
            try { browser.close(); } catch (Exception ignored) {}
        }

        System.out.println("✅ Análisis completado. Iniciando servidor web...");

        // Iniciar servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", AgentServer::handleRoot);
        server.createContext("/chat", AgentServer::handleChat);
        server.createContext("/reporte", AgentServer::handleReporte);
        server.start();
        System.out.println("🌐 Servidor iniciado en: http://localhost:8080");
        System.out.println("Abrí tu navegador en http://localhost:8080");
        System.out.println("📄 Reporte QA: http://localhost:8080/reporte");
    }

    private static void handleRoot(HttpExchange exchange) throws IOException {
        String html = """
            <!DOCTYPE html>
            <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>QA AI Agent</title>
                    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
                    <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { font-family: 'Segoe UI', sans-serif; background: #0f0f1a; color: #e0e0e0; height: 100vh; display: flex; flex-direction: column; }
                    header { background: #1a1a2e; padding: 16px 24px; border-bottom: 1px solid #2a2a4a; display: flex; align-items: center; gap: 12px; }
                    header h1 { font-size: 20px; color: #7c6af7; }
                    header span { font-size: 13px; color: #888; }
                    #chat { flex: 1; overflow-y: auto; padding: 24px; display: flex; flex-direction: column; gap: 16px; }
                    .msg { max-width: 75%; padding: 12px 16px; border-radius: 12px; line-height: 1.6; font-size: 14px; white-space: pre-wrap; }
                    .msg.user { background: #7c6af7; color: white; align-self: flex-end; border-radius: 12px 12px 2px 12px; }
                    .msg.agent { background: #1a1a2e; color: #e0e0e0; align-self: flex-start; border-radius: 12px 12px 12px 2px; border: 1px solid #2a2a4a; }
                    .msg.system { background: #0d2d1f; color: #4caf7d; align-self: center; font-size: 12px; border-radius: 8px; padding: 8px 14px; }
                    #input-area { padding: 16px 24px; background: #1a1a2e; border-top: 1px solid #2a2a4a; display: flex; gap: 12px; }
                    #input-area input { flex: 1; padding: 12px 16px; background: #0f0f1a; border: 1px solid #2a2a4a; border-radius: 8px; color: #e0e0e0; font-size: 14px; outline: none; }
                    #input-area input:focus { border-color: #7c6af7; }
                    #input-area button { padding: 12px 20px; background: #7c6af7; color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; }
                    #input-area button:hover { background: #6a5adf; }
                    .typing { color: #888; font-size: 13px; font-style: italic; }
                </style>
            </head>
            <body>
                <header>
                    <h1>🤖 QA AI Agent</h1>
                    <span>Analizando:+\s""" + urlAnalizada + """
                    </span>
                </header>
                <div id="chat">
                    <div class="msg system">✅ Análisis completado. Podés hacerme preguntas sobre la página.</div>
                    <div class="msg agent">Hola! Soy tu agente QA con IA. Ya analicé <strong>""" + urlAnalizada + """
                    </strong>.<br><br>Podés preguntarme cosas como:<br>
                    • ¿Qué botones encontraste?<br>
                    • ¿Hay problemas de validación?<br>
                    • ¿Qué tests fallaron?<br>
                    • ¿Qué recomendás mejorar?</div>
                </div>
                <div id="input-area">
                    <input type="text" id="pregunta" placeholder="Preguntá algo sobre la página..." onkeypress="if(event.key==='Enter') enviar()">
                    <button onclick="enviar()">Enviar</button>
                </div>
                <script>
                    async function enviar() {
                        const input = document.getElementById('pregunta');
                        const pregunta = input.value.trim();
                        if (!pregunta) return;
                        input.value = '';
                        const chat = document.getElementById('chat');
                        chat.innerHTML += `<div class="msg user">${pregunta}</div>`;
                        chat.innerHTML += `<div class="msg agent typing" id="typing">Analizando...</div>`;
                        chat.scrollTop = chat.scrollHeight;
                        const res = await fetch('/chat', {
                            method: 'POST',
                            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                            body: 'pregunta=' + encodeURIComponent(pregunta)
                        });
                        const respuesta = await res.text();
                        document.getElementById('typing').remove();
                         chat.innerHTML += `<div class="msg agent">${marked.parse(respuesta)}</div>`;
                        chat.scrollTop = chat.scrollHeight;
                    }
                </script>
            </body>
            </html>
            """;

        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    private static void handleChat(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String pregunta = java.net.URLDecoder.decode(body.replace("pregunta=", ""), StandardCharsets.UTF_8);

        String prompt = "Eres un agente QA experto. Respondé en español de forma clara y concisa.\n\n"
                + "Contexto del análisis de la página:\n"
                + contextoAnalisis + "\n\n"
                + "Pregunta del usuario: " + pregunta;

        AIAnalyzer ai = new AIAnalyzer();
        String respuesta = ai.analizarResultados(prompt);

        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }
}