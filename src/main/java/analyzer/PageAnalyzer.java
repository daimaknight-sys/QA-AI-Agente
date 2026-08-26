package analyzer;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import java.util.List;
import java.util.ArrayList;

public class PageAnalyzer {

    public PageInfo analyze(Page page) {

        Locator buttonsLocator = page.locator("button");
        List<String> buttonTexts = new ArrayList<>();
        for (int i = 0; i < buttonsLocator.count(); i++) {
            String texto = buttonsLocator.nth(i).textContent();
            if (texto == null || texto.trim().isEmpty()) {
                texto = "boton_sin_texto_" + i;
            }
            buttonTexts.add(texto.trim());
        }
        int buttons = buttonTexts.size();
        Locator inputsLocator = page.locator("input");
        int inputs = page.locator("input").count();
        List<String> inputNames = new ArrayList<>();
        for (int i = 0; i < inputsLocator.count(); i++) {
            String nombre = inputsLocator.nth(i).getAttribute("name");
            if (nombre == null || nombre.isEmpty()) {
                nombre = inputsLocator.nth(i).getAttribute("id");
            }
            if (nombre == null || nombre.isEmpty()) {
                nombre = inputsLocator.nth(i).getAttribute("placeholder");
            }
            if (nombre == null || nombre.isEmpty()) {
                String type = inputsLocator.nth(i).getAttribute("type");
                nombre = (type != null && !type.isEmpty()) ? "input_tipo_" + type + "_" + i : "input_sin_identificar_" + i;
            }
            System.out.println("Input #" + i + " → name: " + nombre);
            inputNames.add(nombre);
        }
        int forms = page.locator("form").count();
        boolean hasForm = forms > 0;
        int links = page.locator("a").count();
        int images = page.locator("img").count();

        System.out.println("\n===== ANÁLISIS DE LA PÁGINA =====");
        System.out.println("Botones : " + buttons);
        System.out.println("Inputs : " + inputs);
        System.out.println("Forms : " + forms);
        System.out.println("Links : " + links);
        System.out.println("Imágenes: " + images);
        return new PageInfo(inputNames, buttonTexts, hasForm);
    }

}