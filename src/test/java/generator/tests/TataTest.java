package generator.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import generator.pageobjects.TataPage;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class TataTest {

    Playwright playwright;
    Browser browser;
    Page page;
    TataPage pageObject;

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage();
        page.navigate("https://www.tata.com.uy/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        pageObject = new TataPage(page);
    }

    @AfterMethod
    public void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test(description = "Happy path: completa todos los campos con datos válidos")
    public void testHappyPath() {
        pageObject.fillBuscar("valor_valido");
        pageObject.clickBotonSinTexto0(); // revela el campo antes de probarlo
        pageObject.fillBuscar2("valor_valido");
        pageObject.fillBuscar3("valor_valido");
        pageObject.fillBuscar4("valor_valido");
        pageObject.fillNewsletterEmail("valor_valido");
        pageObject.clickBotonSinTexto0();
        // TODO: agregar assert de éxito esperado (ej. redirección, mensaje)
    }

    @Test(description = "Negative case: envía el formulario con campos vacíos")
    public void testCamposVacios() {
        pageObject.clickBotonSinTexto0();
        // TODO: agregar assert de validación esperada (mensaje de error)
    }

    @Test(description = "Boundary/negative: Buscar con dato inválido")
    public void testBuscarInvalido() {
        pageObject.fillBuscar("");
        // TODO: agregar assert de validación esperada para este campo
    }

    @Test(description = "Boundary/negative: Buscar con dato inválido")
    public void testBuscarInvalido2() {
        pageObject.clickBotonSinTexto0(); // revela el campo antes de probarlo
        pageObject.fillBuscar2("");
        // TODO: agregar assert de validación esperada para este campo
    }

    @Test(description = "Boundary/negative: Buscar con dato inválido")
    public void testBuscarInvalido3() {
        pageObject.clickBotonSinTexto0(); // revela el campo antes de probarlo
        pageObject.fillBuscar3("");
        // TODO: agregar assert de validación esperada para este campo
    }

    @Test(description = "Boundary/negative: Buscar con dato inválido")
    public void testBuscarInvalido4() {
        pageObject.clickBotonSinTexto0(); // revela el campo antes de probarlo
        pageObject.fillBuscar4("");
        // TODO: agregar assert de validación esperada para este campo
    }

    @Test(description = "Boundary/negative: newsletter-email con dato inválido")
    public void testNewsletterEmailInvalido() {
        pageObject.fillNewsletterEmail("");
        // TODO: agregar assert de validación esperada para este campo
    }

}
