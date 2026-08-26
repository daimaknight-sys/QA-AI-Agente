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
}