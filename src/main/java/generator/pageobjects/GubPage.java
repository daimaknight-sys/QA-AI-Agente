package generator.pageobjects;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

public class GubPage {

    private final Page page;

    private final Locator searchApiFulltextInput;
    private final Locator userButton;
    private final Locator abrirBuscadorYBuscarButton;
    private final Locator menúButton;
    private final Locator buscarButton;
    private final Locator abiertaCerrarDesplegarMenúButton;
    private final Locator abrirBuscadorCerrarBuscadorButton;

    public GubPage(Page page) {
        this.page = page;
        this.searchApiFulltextInput = page.locator("[name='search_api_fulltext']");
        this.userButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("User"));
        this.abrirBuscadorYBuscarButton = page.locator("[aria-label='Abrir buscador y buscar']");
        this.menúButton = page.locator("#navButtonWrap-md");
        this.buscarButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Buscar"));
        this.abiertaCerrarDesplegarMenúButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Abierta Cerrar Desplegar Menú"));
        this.abrirBuscadorCerrarBuscadorButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Abrir buscador Cerrar buscador"));
    }

    public void fillSearchApiFulltext(String value) {
        searchApiFulltextInput.fill(value);
    }

    public void clickUser() {
        userButton.click();
    }

    public void clickAbrirBuscadorYBuscar() {
        abrirBuscadorYBuscarButton.click();
    }

    public void clickMenú() {
        menúButton.click();
    }

    public void clickBuscar() {
        buscarButton.click();
    }

    public void clickAbiertaCerrarDesplegarMenú() {
        abiertaCerrarDesplegarMenúButton.click();
    }

    public void clickAbrirBuscadorCerrarBuscador() {
        abrirBuscadorCerrarBuscadorButton.click();
    }

}
