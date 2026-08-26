package generator;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FormValidationTest {

    @Test
    public void formulariosTienenBotonDeEnvio() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            page.navigate("https://www.tata.com.uy/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            Locator forms = page.locator("form");
            int cantidad = forms.count();

            for (int i = 0; i < cantidad; i++) {
                Locator form = forms.nth(i);
                Locator botonEnvio = form.locator("button[type='submit'], input[type='submit']");
                int cantidadBotones = botonEnvio.count();

                System.out.println("Form #" + i + " → botones de envío encontrados: " + cantidadBotones);

                Assert.assertTrue(cantidadBotones > 0, "Form #" + i + " no tiene botón de envío");
            }

            browser.close();
        }
    }
}