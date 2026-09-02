package security;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import util.ReportWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XssScanner {

    private static final String[] PAYLOADS = {
            "<script>alert('XSS')</script>",
            "\"><img src=x onerror=alert(1)>"
    };

    public List<String> escanear(String url) throws IOException {
        List<String> hallazgos = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            ReportWriter reporte = new ReportWriter("reporte_xss.txt");

            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            Locator inputs = page.locator("input");
            int cantidad = inputs.count();
            System.out.println("Inputs totales encontrados en la página: " + cantidad);

            int visibles = 0;

            for (int i = 0; i < cantidad; i++) {
                Locator input = inputs.nth(i);

                if (!input.isVisible()) {
                    System.out.println("Input #" + i + " → omitido (no visible)");
                    continue;
                }

                visibles++;
                System.out.println("Input #" + i + " → visible, probando payloads...");

                for (String payload : PAYLOADS) {
                    input.fill(payload);

                    String html = page.content();
                    boolean encontrado = html.contains(payload);

                    System.out.println("  Payload: " + payload + " → reflejado: " + encontrado);

                    if (encontrado) {
                        String hallazgo = "⚠ Posible XSS reflejado en input #" + i +
                                " con payload: " + payload;
                        hallazgos.add(hallazgo);
                        reporte.escribirLinea(hallazgo);
                    }

                    input.fill("");
                }
            }

            System.out.println("Total de inputs visibles probados: " + visibles);

            reporte.cerrar();
            browser.close();
        }

        return hallazgos;
    }
}