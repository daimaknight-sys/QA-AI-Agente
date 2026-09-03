package crawler;

import ai.AIAnalyzer;
import analyzer.PageAnalyzer;
import analyzer.PageInfo;
import execution.PlaywrightTestExecutor;
import generator.TestCaseGenerator;
import util.ReportWriter;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import java.util.HashSet;
import com.microsoft.playwright.options.LoadState;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class WebCrawler {

    public Set<String> crawl(String url) {

        Set<String> links = new HashSet<>();

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false));

            Page page = browser.newPage();

            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            PageAnalyzer analyzer = new PageAnalyzer();
            PageInfo info = analyzer.analyze(page);

            TestCaseGenerator generator = new TestCaseGenerator();
            String casosGenerados = generator.generar(info);

            System.out.println("\n===== EJECUTANDO TESTS REALES =====");
            PlaywrightTestExecutor executor = new PlaywrightTestExecutor(page);
            String resultadosReales = executor.ejecutarTests(info);
            System.out.println(resultadosReales);

            System.out.println("\n===== ENVIANDO RESULTADOS A LA IA =====");
            AIAnalyzer aiAnalyzer = new AIAnalyzer();
            String analisis = aiAnalyzer.analizarResultados(resultadosReales);
            System.out.println("\n===== ANÁLISIS DE LA IA =====");
            System.out.println(analisis);

            // Guardar reporte
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String rutaReporte = "reporte_qa_" + timestamp + ".txt";

            try {
                ReportWriter reportWriter = new ReportWriter(rutaReporte);
                reportWriter.escribirLinea("===== REPORTE QA AGENT =====");
                reportWriter.escribirLinea("URL analizada: " + url);
                reportWriter.escribirLinea("Fecha: " + timestamp);
                reportWriter.escribirLinea("");
                reportWriter.escribirLinea("===== ANÁLISIS DE LA PÁGINA =====");
                reportWriter.escribirLinea("Botones: " + info.buttonTexts.size());
                reportWriter.escribirLinea("Inputs: " + info.inputNames.size());
                reportWriter.escribirLinea("Forms: " + (info.hasForm ? "Sí" : "No"));
                reportWriter.escribirLinea("");
                reportWriter.escribirLinea("===== CASOS GENERADOS =====");
                reportWriter.escribirLinea(casosGenerados);
                reportWriter.escribirLinea("===== RESULTADOS REALES =====");
                reportWriter.escribirLinea(resultadosReales);
                reportWriter.escribirLinea("===== ANÁLISIS DE LA IA =====");
                reportWriter.escribirLinea(analisis);
                reportWriter.cerrar();
                System.out.println("\n✅ Reporte guardado en: " + rutaReporte);
            } catch (IOException e) {
                System.out.println("Error al guardar reporte: " + e.getMessage());
            }

            List<ElementHandle> elements = page.querySelectorAll("a");
            for (ElementHandle element : elements) {
                String href = element.getAttribute("href");
                if (href != null && !href.isEmpty()) {
                    links.add(href);
                }
            }

            browser.close();
        }


        return links;
    }

    public List<PaginaResultado> crawlMultiple(String urlInicial, int maxPaginas) {
        List<PaginaResultado> resultados = new ArrayList<>();
        Set<String> visitadas = new HashSet<>();
        Queue<String> porVisitar = new LinkedList<>();
        porVisitar.add(normalizarUrl(urlInicial));

        String dominio = obtenerDominio(urlInicial);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true));

            while (!porVisitar.isEmpty() && visitadas.size() < maxPaginas) {
                String urlActual = porVisitar.poll();

                if (visitadas.contains(urlActual)) continue;
                visitadas.add(urlActual);

                try {
                    Page page = browser.newPage();
                    page.navigate(urlActual, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                    page.waitForLoadState(LoadState.DOMCONTENTLOADED);

                    PageAnalyzer analyzer = new PageAnalyzer();
                    PageInfo info = analyzer.analyze(page);

                    resultados.add(new PaginaResultado(urlActual, info));
                    System.out.println("✅ Analizada (" + visitadas.size() + "/" + maxPaginas + "): " + urlActual);

                    // Recolectar links nuevos del mismo dominio
                    List<ElementHandle> elements = page.querySelectorAll("a");
                    for (ElementHandle element : elements) {
                        String href = element.getAttribute("href");
                        if (href != null && !href.isEmpty()) {
                            String absoluta = resolverUrl(urlActual, href);
                            if (absoluta != null && absoluta.contains("#")) {
                                absoluta = absoluta.substring(0, absoluta.indexOf("#"));
                            }
                            absoluta = normalizarUrl(absoluta);
                            if (absoluta != null && obtenerDominio(absoluta).equals(dominio)
                                    && !visitadas.contains(absoluta)) {
                                porVisitar.add(absoluta);
                            }
                        }
                    }

                    page.close();
                } catch (Exception e) {
                    System.out.println("⚠ Error analizando " + urlActual + ": " + e.getMessage());
                }
            }

            browser.close();
        }

        return resultados;

    }

    private String obtenerDominio(String url) {
        try {
            String host = new java.net.URI(url).getHost();
            return host != null ? host : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String resolverUrl(String base, String href) {
        try {
            return new java.net.URI(base).resolve(href).toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String normalizarUrl(String url) {
        if (url == null) return null;
        if (url.endsWith("/") && url.length() > 1) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static class PaginaResultado {
        public String url;
        public PageInfo info;

        public PaginaResultado(String url, PageInfo info) {
            this.url = url;
            this.info = info;
        }
    }
}