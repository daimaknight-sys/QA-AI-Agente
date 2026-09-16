package execution;

import analyzer.PageInfo;
import com.microsoft.playwright.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class PlaywrightTestExecutor {

    private Page page;

    public PlaywrightTestExecutor(Page page) {
        this.page = page;
    }

    public String ejecutarTests(PageInfo info) {
        StringBuilder resultados = new StringBuilder();

        // --- Inputs: filtramos los no identificables ANTES de armar la lista a probar ---
        List<String> inputsCandidatos = new ArrayList<>(new LinkedHashSet<>(info.inputNames));
        List<String> inputsOmitidos = new ArrayList<>();
        List<String> inputsAProbar = new ArrayList<>();
        for (String nombre : inputsCandidatos) {
            if (nombre.startsWith("input_tipo_") || nombre.startsWith("input_sin_")) {
                inputsOmitidos.add(nombre);
            } else {
                inputsAProbar.add(nombre);
            }
        }

        // --- Botones: mismo criterio, filtramos los sin texto antes de armar la lista a probar ---
        List<String> botonesCandidatos = new ArrayList<>(new LinkedHashSet<>(info.buttonTexts));
        List<String> botonesOmitidos = new ArrayList<>();
        List<String> botonesAProbar = new ArrayList<>();
        for (String texto : botonesCandidatos) {
            if (texto.startsWith("boton_sin_texto_")) {
                botonesOmitidos.add(texto);
            } else {
                botonesAProbar.add(texto);
            }
        }

        // Ya no truncamos a 5 arbitrariamente: probamos todos los elementos identificables.
        // Si en algún sitio esto resulta demasiado lento, conviene paginar/paralelizar
        // en vez de descartar candidatos en silencio.

        if (!inputsOmitidos.isEmpty()) {
            resultados.append("⚠️ SKIP: ").append(inputsOmitidos.size())
                    .append(" input(s) sin identificar ignorados (")
                    .append(String.join(", ", inputsOmitidos)).append(")\n");
        }
        if (!botonesOmitidos.isEmpty()) {
            resultados.append("⚠️ SKIP: ").append(botonesOmitidos.size())
                    .append(" botón(es) sin texto ignorados (sin aria-label/texto visible para localizarlos)\n");
        }

        // Testear inputs
        for (String nombre : inputsAProbar) {
            try {
                Locator input = page.locator(
                        "input[placeholder='" + nombre + "'], input[name='" + nombre + "'], input[id='" + nombre + "']"
                ).first();

                // Test 1: acepta texto
                input.fill("test");
                resultados.append("✅ PASS: Campo '").append(nombre).append("' acepta texto\n");

                // Test 2: validación nativa HTML5
                boolean tieneRequired = (Boolean) input.evaluate("el => el.hasAttribute('required')");
                boolean tienePattern = (Boolean) input.evaluate("el => el.hasAttribute('pattern')");
                boolean tieneMinLength = (Boolean) input.evaluate("el => el.hasAttribute('minlength')");
                boolean tieneTipoEmail = (Boolean) input.evaluate("el => el.type === 'email'");

                if (tieneRequired || tienePattern || tieneMinLength || tieneTipoEmail) {
                    resultados.append("✅ PASS: Campo '").append(nombre).append("' tiene validación nativa (")
                            .append(tieneRequired ? "required " : "")
                            .append(tienePattern ? "pattern " : "")
                            .append(tieneMinLength ? "minlength " : "")
                            .append(tieneTipoEmail ? "type=email" : "")
                            .append(")\n");
                } else {
                    // Test 3: envío vacío y detección DOM
                    input.fill("");
                    input.press("Enter");
                    page.waitForTimeout(1000);
                    boolean hayErrorDOM = page.locator("[class*='error'], .form-item--error, [aria-invalid='true']").count() > 0;
                    if (hayErrorDOM) {
                        resultados.append("✅ PASS: Campo '").append(nombre).append("' muestra error en el DOM al enviar vacío\n");
                    } else {
                        resultados.append("❌ FAIL: Campo '").append(nombre).append("' NO tiene validación — acepta envío vacío sin error\n");
                    }
                    input.fill("test");
                }

                // Test 4: validación de email
                if (nombre.toLowerCase().contains("email") || nombre.toLowerCase().contains("correo")) {
                    input.fill("esto-no-es-un-email");
                    input.press("Tab");
                    boolean hayError = page.locator("[class*='error'], [class*='invalid'], [aria-invalid='true']").count() > 0;
                    if (hayError || tieneTipoEmail) {
                        resultados.append("✅ PASS: Campo '").append(nombre).append("' valida formato de email\n");
                    } else {
                        resultados.append("❌ FAIL: Campo '").append(nombre).append("' NO valida formato de email\n");
                    }
                }

                // Test 5: numéricos
                if (nombre.toLowerCase().contains("cedula") || nombre.toLowerCase().contains("documento")
                        || nombre.toLowerCase().contains("telefono") || nombre.toLowerCase().contains("phone")) {
                    input.fill("abc!@#invalido");
                    input.press("Tab");
                    boolean hayError = page.locator("[class*='error'], [class*='invalid'], [aria-invalid='true']").count() > 0;
                    if (hayError) {
                        resultados.append("✅ PASS: Campo '").append(nombre).append("' valida formato numérico\n");
                    } else {
                        resultados.append("❌ FAIL: Campo '").append(nombre).append("' NO valida formato numérico\n");
                    }
                }

            } catch (Exception e) {
                resultados.append("❌ FAIL: Campo '").append(nombre).append("' → ").append(e.getMessage().split("\n")[0]).append("\n");
            }
        }

        // Testear botones
        for (String texto : botonesAProbar) {
            try {
                Locator boton = page.locator("button").filter(new Locator.FilterOptions().setHasText(texto)).first();
                boton.waitFor(new Locator.WaitForOptions().setTimeout(1500));
                if (boton.isVisible() && boton.isEnabled()) {
                    resultados.append("✅ PASS: Botón '").append(texto).append("' es visible y clickeable\n");
                } else {
                    resultados.append("❌ FAIL: Botón '").append(texto).append("' no está disponible\n");
                }
            } catch (Exception e) {
                resultados.append("❌ FAIL: Botón '").append(texto).append("' → no encontrado\n");
            }
        }

        // Testear formulario
        if (info.hasForm) {
            try {
                int forms = page.locator("form").count();
                resultados.append(forms > 0
                        ? "✅ PASS: Formulario presente (" + forms + " forms)\n"
                        : "❌ FAIL: No se encontró formulario\n");
            } catch (Exception e) {
                resultados.append("❌ FAIL: Error verificando formulario\n");
            }
        }

        return resultados.toString();
    }
}