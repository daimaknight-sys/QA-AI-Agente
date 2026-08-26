package generator;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ButtonValidationTest {

    @Test
    public void botonesSonClickeables() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            page.navigate("https://www.tata.com.uy/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            Locator buttons = page.locator("button");
            int cantidad = buttons.count();

            for (int i = 0; i < cantidad; i++) {
                Locator button = buttons.nth(i);

                if (!button.isVisible()) {
                    System.out.println("Botón #" + i + " → omitido (no visible)");
                    continue;
                }

                boolean habilitado = button.isEnabled();
                System.out.println("Botón #" + i + " → visible, habilitado: " + habilitado);

                Assert.assertTrue(habilitado, "Botón #" + i + " está deshabilitado");
            }

            browser.close();
        }
    }
}