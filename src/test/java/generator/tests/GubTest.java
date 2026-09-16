package generator.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import generator.pageobjects.GubPage;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class GubTest {

    Playwright playwright;
    Browser browser;
    Page page;
    GubPage pageObject;

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage();
        page.navigate("https://www.gub.uy", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        pageObject = new GubPage(page);
    }

    @AfterMethod
    public void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test(description = "Happy path: completa todos los campos con datos válidos")
    public void testHappyPath() {
        pageObject.clickAbrirBuscadorYBuscar(); // revela el campo antes de probarlo
        pageObject.fillSearchApiFulltext("valor_valido");
        pageObject.clickUser();
        // TODO: agregar assert de éxito esperado (ej. redirección, mensaje)
    }

    @Test(description = "Negative case: envía el formulario con campos vacíos")
    public void testCamposVacios() {
        pageObject.clickUser();
        // TODO: agregar assert de validación esperada (mensaje de error)
    }

    @Test(description = "Boundary/negative: search_api_fulltext con dato inválido")
    public void testSearchApiFulltextInvalido() {
        pageObject.clickAbrirBuscadorYBuscar(); // revela el campo antes de probarlo
        pageObject.fillSearchApiFulltext("");
        // TODO: agregar assert de validación esperada para este campo
    }

}
