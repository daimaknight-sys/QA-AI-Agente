package generator.pageobjects;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

public class TataPage {

    private final Page page;

    private final Locator buscarInput;
    private final Locator buscarInput2;
    private final Locator buscarInput3;
    private final Locator buscarInput4;
    private final Locator newsletterEmailInput;
    private final Locator botonSinTexto0Button;
    private final Locator botonSinTexto1Button;
    private final Locator botonSinTexto2Button;
    private final Locator botonSinTexto3Button;
    private final Locator tataButton;
    private final Locator botonSinTexto5Button;
    private final Locator tataButton2;
    private final Locator botonSinTexto7Button;
    private final Locator botonSinTexto8Button;
    private final Locator botonSinTexto9Button;
    private final Locator categoríasButton;
    private final Locator botonSinTexto11Button;
    private final Locator botonSinTexto12Button;
    private final Locator tataButton3;
    private final Locator botonSinTexto14Button;
    private final Locator tataButton4;
    private final Locator botonSinTexto16Button;
    private final Locator botonSinTexto17Button;
    private final Locator botonSinTexto18Button;
    private final Locator categoríasButton2;
    private final Locator botonSinTexto20Button;
    private final Locator botonSinTexto21Button;
    private final Locator botonSinTexto22Button;
    private final Locator botonSinTexto23Button;
    private final Locator botonSinTexto24Button;
    private final Locator botonSinTexto25Button;
    private final Locator botonSinTexto26Button;
    private final Locator botonSinTexto27Button;
    private final Locator botonSinTexto28Button;
    private final Locator tataButton5;
    private final Locator beneficiosButton;
    private final Locator misComprasButton;

    public TataPage(Page page) {
        this.page = page;
        this.buscarInput = page.locator("[placeholder='Buscar']");
        this.buscarInput2 = page.locator("[placeholder='Buscar']");
        this.buscarInput3 = page.locator("[placeholder='Buscar']");
        this.buscarInput4 = page.locator("[placeholder='Buscar']");
        this.newsletterEmailInput = page.locator("[name='newsletter-email']");
        this.botonSinTexto0Button = page.locator("[aria-label='previous']");
        this.botonSinTexto1Button = page.locator("[aria-label='next']");
        this.botonSinTexto2Button = page.locator("[aria-label='Open Menu']");
        this.botonSinTexto3Button = page.locator("[aria-label='Submit Search']");
        this.tataButton = page.locator("[aria-label='Geolocate Button']");
        this.botonSinTexto5Button = page.locator("[aria-label='Cart with 0 items']");
        this.tataButton2 = page.locator("[aria-label='Geolocate Button']");
        this.botonSinTexto7Button = page.locator("[aria-label='Cart with 0 items']");
        this.botonSinTexto8Button = page.locator("[aria-label='Submit Search']");
        this.botonSinTexto9Button = page.locator("[aria-label='Open Menu']");
        this.categoríasButton = page.locator("[aria-label='Categorías']");
        this.botonSinTexto11Button = page.locator("[aria-label='Open Menu']");
        this.botonSinTexto12Button = page.locator("[aria-label='Submit Search']");
        this.tataButton3 = page.locator("[aria-label='Geolocate Button']");
        this.botonSinTexto14Button = page.locator("[aria-label='Cart with 0 items']");
        this.tataButton4 = page.locator("[aria-label='Geolocate Button']");
        this.botonSinTexto16Button = page.locator("[aria-label='Cart with 0 items']");
        this.botonSinTexto17Button = page.locator("[aria-label='Submit Search']");
        this.botonSinTexto18Button = page.locator("[aria-label='Open Menu']");
        this.categoríasButton2 = page.locator("[aria-label='Categorías']");
        this.botonSinTexto20Button = page.locator("[aria-label='previous']");
        this.botonSinTexto21Button = page.locator("[aria-label='next']");
        this.botonSinTexto22Button = page.locator("[aria-label='Current page']");
        this.botonSinTexto23Button = page.locator("[aria-label='Go to page 2']");
        this.botonSinTexto24Button = page.locator("[aria-label='Go to page 3']");
        this.botonSinTexto25Button = page.locator("[aria-label='Go to page 4']");
        this.botonSinTexto26Button = page.locator("[aria-label='Go to page 5']");
        this.botonSinTexto27Button = page.locator("[aria-label='Go to page 6']");
        this.botonSinTexto28Button = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("boton_sin_texto_28"));
        this.tataButton5 = page.locator("#footer-links-0-button");
        this.beneficiosButton = page.locator("#footer-links-1-button");
        this.misComprasButton = page.locator("#footer-links-2-button");
    }

    public void fillBuscar(String value) {
        buscarInput.fill(value);
    }

    public void fillBuscar2(String value) {
        buscarInput2.fill(value);
    }

    public void fillBuscar3(String value) {
        buscarInput3.fill(value);
    }

    public void fillBuscar4(String value) {
        buscarInput4.fill(value);
    }

    public void fillNewsletterEmail(String value) {
        newsletterEmailInput.fill(value);
    }

    public void clickBotonSinTexto0() {
        botonSinTexto0Button.click();
    }

    public void clickBotonSinTexto1() {
        botonSinTexto1Button.click();
    }

    public void clickBotonSinTexto2() {
        botonSinTexto2Button.click();
    }

    public void clickBotonSinTexto3() {
        botonSinTexto3Button.click();
    }

    public void clickTata() {
        tataButton.click();
    }

    public void clickBotonSinTexto5() {
        botonSinTexto5Button.click();
    }

    public void clickTata2() {
        tataButton2.click();
    }

    public void clickBotonSinTexto7() {
        botonSinTexto7Button.click();
    }

    public void clickBotonSinTexto8() {
        botonSinTexto8Button.click();
    }

    public void clickBotonSinTexto9() {
        botonSinTexto9Button.click();
    }

    public void clickCategorías() {
        categoríasButton.click();
    }

    public void clickBotonSinTexto11() {
        botonSinTexto11Button.click();
    }

    public void clickBotonSinTexto12() {
        botonSinTexto12Button.click();
    }

    public void clickTata3() {
        tataButton3.click();
    }

    public void clickBotonSinTexto14() {
        botonSinTexto14Button.click();
    }

    public void clickTata4() {
        tataButton4.click();
    }

    public void clickBotonSinTexto16() {
        botonSinTexto16Button.click();
    }

    public void clickBotonSinTexto17() {
        botonSinTexto17Button.click();
    }

    public void clickBotonSinTexto18() {
        botonSinTexto18Button.click();
    }

    public void clickCategorías2() {
        categoríasButton2.click();
    }

    public void clickBotonSinTexto20() {
        botonSinTexto20Button.click();
    }

    public void clickBotonSinTexto21() {
        botonSinTexto21Button.click();
    }

    public void clickBotonSinTexto22() {
        botonSinTexto22Button.click();
    }

    public void clickBotonSinTexto23() {
        botonSinTexto23Button.click();
    }

    public void clickBotonSinTexto24() {
        botonSinTexto24Button.click();
    }

    public void clickBotonSinTexto25() {
        botonSinTexto25Button.click();
    }

    public void clickBotonSinTexto26() {
        botonSinTexto26Button.click();
    }

    public void clickBotonSinTexto27() {
        botonSinTexto27Button.click();
    }

    public void clickBotonSinTexto28() {
        botonSinTexto28Button.click();
    }

    public void clickTata5() {
        tataButton5.click();
    }

    public void clickBeneficios() {
        beneficiosButton.click();
    }

    public void clickMisCompras() {
        misComprasButton.click();
    }

}
