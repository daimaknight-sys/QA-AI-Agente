package analyzer;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import java.util.List;
import java.util.ArrayList;

public class PageAnalyzer {

    // Tiempo máximo total que se le permite a detectarBotonesQueRevelanInputs por página.
    // Sin este límite, una página con muchos botones donde ninguno revela el input oculto
    // recorre TODOS los botones (con sus timeouts individuales) sin ningún corte, pudiendo
    // colgar el crawl varios minutos en una sola página.
    private static final long PRESUPUESTO_DETECCION_MS = 15000;

    public PageInfo analyze(Page page) {

        Locator buttonsLocator = page.locator("button");
        List<String> buttonTexts = new ArrayList<>();
        List<ButtonInfo> buttons = new ArrayList<>();
        for (int i = 0; i < buttonsLocator.count(); i++) {
            Locator current = buttonsLocator.nth(i);
            String texto = current.textContent();
            if (texto != null) {
                texto = texto.replaceAll("\\s+", " ").trim();
            }
            if (texto == null || texto.isEmpty()) {
                texto = "boton_sin_texto_" + i;
            }
            String id = current.getAttribute("id");
            String ariaLabel = current.getAttribute("aria-label");
            String title = current.getAttribute("title");

            buttonTexts.add(texto);
            buttons.add(new ButtonInfo(texto, id, ariaLabel, title));
        }
        int buttonsCount = buttonTexts.size();

        Locator inputsLocator = page.locator("input");
        int inputsCount = inputsLocator.count();
        List<String> inputNames = new ArrayList<>();
        List<InputInfo> inputs = new ArrayList<>();
        for (int i = 0; i < inputsCount; i++) {
            Locator current = inputsLocator.nth(i);
            String nombre = current.getAttribute("name");
            String tipo = "name";
            if (nombre == null || nombre.isEmpty()) {
                nombre = current.getAttribute("id");
                tipo = "id";
            }
            if (nombre == null || nombre.isEmpty()) {
                nombre = current.getAttribute("placeholder");
                tipo = "placeholder";
            }
            if (nombre == null || nombre.isEmpty()) {
                String type = current.getAttribute("type");
                nombre = (type != null && !type.isEmpty()) ? "input_tipo_" + type + "_" + i : "input_sin_identificar_" + i;
                tipo = (type != null && !type.isEmpty()) ? "type" : "unknown";
            }
            boolean visible = current.isVisible();
            System.out.println("Input #" + i + " → " + tipo + ": " + nombre + " (visible: " + visible + ")");
            inputNames.add(nombre);
            inputs.add(new InputInfo(tipo, nombre, visible));
        }

        // ===== Detección de secuencia: ¿algún botón revela inputs ocultos? =====
        detectarBotonesQueRevelanInputs(page, inputs, buttons);

        int forms = page.locator("form").count();
        boolean hasForm = forms > 0;
        int links = page.locator("a").count();
        int images = page.locator("img").count();

        System.out.println("\n===== ANÁLISIS DE LA PÁGINA =====");
        System.out.println("Botones : " + buttonsCount);
        System.out.println("Inputs : " + inputsCount);
        System.out.println("Forms : " + forms);
        System.out.println("Links : " + links);
        System.out.println("Imágenes: " + images);
        return new PageInfo(inputNames, inputs, buttonTexts, buttons, hasForm);
    }

    private void detectarBotonesQueRevelanInputs(Page page, List<InputInfo> inputs, List<ButtonInfo> buttons) {
        boolean hayInputsOcultos = inputs.stream().anyMatch(i -> !i.visible);
        if (!hayInputsOcultos || buttons.isEmpty()) return;

        System.out.println("\n🔎 Probando botones para detectar campos ocultos revelados...");

        long inicio = System.currentTimeMillis();

        for (ButtonInfo button : buttons) {
            boolean quedaAlgunoOculto = inputs.stream().anyMatch(i -> !i.visible && i.revealedByButtonText == null);
            if (!quedaAlgunoOculto) break;

            if (System.currentTimeMillis() - inicio > PRESUPUESTO_DETECCION_MS) {
                System.out.println("   ⏱️ Presupuesto de tiempo agotado (" + PRESUPUESTO_DETECCION_MS
                        + "ms) probando botones — se sigue con el resto del análisis de esta página.");
                break;
            }

            try {
                Locator boton = buscarLocatorDeBoton(page, button);
                if (boton == null || boton.count() == 0) continue;

                boton.first().click(new Locator.ClickOptions().setTimeout(800));
                page.waitForTimeout(200);

                for (InputInfo input : inputs) {
                    if (input.visible || input.revealedByButtonText != null) continue;
                    Locator inputLocator = buscarLocatorDeInput(page, input);
                    if (inputLocator != null && inputLocator.count() > 0 && inputLocator.first().isVisible()) {
                        input.revealedByButtonText = button.text;
                        System.out.println("   ✅ '" + input.identifierValue + "' se revela con el botón '" + button.text + "'");
                    }
                }

                // Intentar cerrar de nuevo (muchos botones son toggles) para no dejar la página en un estado raro
                try {
                    boton.first().click(new Locator.ClickOptions().setTimeout(500));
                } catch (Exception ignored) {}

            } catch (Exception e) {
                // Botón no clickeable, oculto, o causó navegación — lo saltamos sin romper el análisis
            }
        }
    }

    private Locator buscarLocatorDeBoton(Page page, ButtonInfo button) {
        if (button.id != null && !button.id.isEmpty()) {
            return page.locator("#" + button.id);
        }
        if (button.ariaLabel != null && !button.ariaLabel.isEmpty()) {
            return page.locator("[aria-label='" + button.ariaLabel + "']");
        }
        return page.getByText(button.text);
    }

    private Locator buscarLocatorDeInput(Page page, InputInfo input) {
        switch (input.identifierType) {
            case "name": return page.locator("[name='" + input.identifierValue + "']");
            case "id": return page.locator("#" + input.identifierValue);
            case "placeholder": return page.locator("[placeholder='" + input.identifierValue + "']");
            default: return null;
        }
    }
}