package generator;

import analyzer.InputInfo;
import analyzer.PageInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestClassGenerator {

    /**
     * @param inputFillMethods   nombres de método fillXxx ya deduplicados (paralela a pageInfo.inputs),
     *                           tal como los devolvió PageObjectGenerator.generate(...).
     * @param buttonClickMethods nombres de método clickXxx ya deduplicados (paralela a pageInfo.buttons),
     *                           tal como los devolvió PageObjectGenerator.generate(...).
     */
    public String generate(String pageClassName, String testClassName, PageInfo pageInfo, String url,
                           List<String> inputFillMethods, List<String> buttonClickMethods) {
        StringBuilder sb = new StringBuilder();

        sb.append("package generator.tests;\n\n");
        sb.append("import com.microsoft.playwright.*;\n");
        sb.append("import com.microsoft.playwright.options.WaitUntilState;\n");
        sb.append("import generator.pageobjects.").append(pageClassName).append(";\n");
        sb.append("import org.testng.annotations.*;\n");
        sb.append("import static org.testng.Assert.*;\n\n");
        sb.append("public class ").append(testClassName).append(" {\n\n");
        sb.append("    Playwright playwright;\n");
        sb.append("    Browser browser;\n");
        sb.append("    Page page;\n");
        sb.append("    ").append(pageClassName).append(" pageObject;\n\n");

        sb.append("    @BeforeMethod\n");
        sb.append("    public void setUp() {\n");
        sb.append("        playwright = Playwright.create();\n");
        sb.append("        browser = playwright.chromium().launch();\n");
        sb.append("        page = browser.newPage();\n");
        sb.append("        page.navigate(\"").append(escapeJavaString(url)).append("\", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));\n");
        sb.append("        pageObject = new ").append(pageClassName).append("(page);\n");
        sb.append("    }\n\n");

        sb.append("    @AfterMethod\n");
        sb.append("    public void tearDown() {\n");
        sb.append("        browser.close();\n");
        sb.append("        playwright.close();\n");
        sb.append("    }\n\n");

        // Happy path: revela campos ocultos si hace falta, llena todos con datos válidos, clickea el primer botón
        sb.append("    @Test(description = \"Happy path: completa todos los campos con datos válidos\")\n");
        sb.append("    public void testHappyPath() {\n");
        Set<String> abiertosHappy = new HashSet<>();
        for (int i = 0; i < pageInfo.inputs.size(); i++) {
            InputInfo input = pageInfo.inputs.get(i);
            agregarAperturaSiHaceFalta(sb, input, abiertosHappy, buttonClickMethods, pageInfo);
            sb.append("        pageObject.").append(inputFillMethods.get(i)).append("(\"valor_valido\");\n");
        }
        if (!buttonClickMethods.isEmpty()) {
            sb.append("        pageObject.").append(buttonClickMethods.get(0)).append("();\n");
        }
        sb.append("        // TODO: agregar assert de éxito esperado (ej. redirección, mensaje)\n");
        sb.append("    }\n\n");

        // Negative case: campos vacíos
        sb.append("    @Test(description = \"Negative case: envía el formulario con campos vacíos\")\n");
        sb.append("    public void testCamposVacios() {\n");
        if (!buttonClickMethods.isEmpty()) {
            sb.append("        pageObject.").append(buttonClickMethods.get(0)).append("();\n");
        }
        sb.append("        // TODO: agregar assert de validación esperada (mensaje de error)\n");
        sb.append("    }\n\n");

        // Boundary/negative por cada input individual con dato inválido
        Set<String> usedTestMethodNames = new HashSet<>();
        for (int i = 0; i < pageInfo.inputs.size(); i++) {
            InputInfo input = pageInfo.inputs.get(i);
            String testNameBase = "test" + capitalize(toCamelCase(input.identifierValue)) + "Invalido";
            String testName = uniqueName(testNameBase, usedTestMethodNames);
            sb.append("    @Test(description = \"Boundary/negative: ")
                    .append(escapeJavaString(input.identifierValue))
                    .append(" con dato inválido\")\n");
            sb.append("    public void ").append(testName).append("() {\n");
            if (input.revealedByButtonText != null) {
                Integer btnIndex = indiceDeBoton(pageInfo, input.revealedByButtonText);
                if (btnIndex != null) {
                    sb.append("        pageObject.").append(buttonClickMethods.get(btnIndex))
                            .append("(); // revela el campo antes de probarlo\n");
                }
            }
            sb.append("        pageObject.").append(inputFillMethods.get(i)).append("(\"\");\n");
            sb.append("        // TODO: agregar assert de validación esperada para este campo\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private void agregarAperturaSiHaceFalta(StringBuilder sb, InputInfo input, Set<String> abiertos,
                                            List<String> buttonClickMethods, PageInfo pageInfo) {
        if (input.revealedByButtonText != null && !abiertos.contains(input.revealedByButtonText)) {
            Integer btnIndex = indiceDeBoton(pageInfo, input.revealedByButtonText);
            if (btnIndex != null) {
                sb.append("        pageObject.").append(buttonClickMethods.get(btnIndex))
                        .append("(); // revela el campo antes de probarlo\n");
                abiertos.add(input.revealedByButtonText);
            }
        }
    }

    /** Busca el índice del primer botón cuyo texto coincide, para mapear a buttonClickMethods.get(indice). */
    private Integer indiceDeBoton(PageInfo pageInfo, String textoBoton) {
        for (int i = 0; i < pageInfo.buttons.size(); i++) {
            if (pageInfo.buttons.get(i).text.equals(textoBoton)) {
                return i;
            }
        }
        return null;
    }

    private String uniqueName(String base, Set<String> used) {
        String candidate = base;
        int counter = 2;
        while (used.contains(candidate)) {
            candidate = base + counter;
            counter++;
        }
        used.add(candidate);
        return candidate;
    }

    private String toCamelCase(String raw) {
        String[] parts = raw.trim().split("[\\s_\\-]+");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            result.append(Character.toUpperCase(parts[i].charAt(0)))
                    .append(parts[i].substring(1).toLowerCase());
        }
        return result.toString();
    }

    private String capitalize(String s) {
        if (s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String escapeJavaString(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}