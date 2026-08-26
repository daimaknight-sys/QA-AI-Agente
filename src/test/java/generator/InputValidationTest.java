package generator;
import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.microsoft.playwright.options.WaitUntilState;
import util.ReportWriter;
import java.io.IOException;

public class InputValidationTest {

    @Test
    public void inputAceptaTexto() throws IOException {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            ReportWriter reporte = new ReportWriter("reporte_qa.txt");
            page.navigate("https://www.tata.com.uy/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            Locator inputs = page.locator("input");
            int cantidad = inputs.count();

            for (int i = 0; i < cantidad; i++) {
                Locator input = inputs.nth(i);

                if (!input.isVisible()) {
                    System.out.println("Input #" + i + " → omitido (no visible)");
                    continue;
                }

                String valorEscrito = "test" + i;
                input.fill(valorEscrito);
                String valorLeido = input.inputValue();
                System.out.println("Input #" + i + " → escribí '" + valorEscrito + "', quedó '" + valorLeido + "'");
                Assert.assertEquals(valorLeido, valorEscrito,
                        "Input #" + i + " no guardó el valor esperado");
                reporte.escribirLinea("Input #" + i + " → escribí '" + valorEscrito + "', quedó '" + valorLeido + "'");
            }
            reporte.cerrar();
            browser.close();
        }
    }
}