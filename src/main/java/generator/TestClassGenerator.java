package generator;

import analyzer.InputInfo;
import analyzer.PageInfo;

public class TestClassGenerator {

    public String generate(String pageClassName, String testClassName, PageInfo pageInfo) {
        StringBuilder sb = new StringBuilder();

        sb.append("package generator.tests;\n\n");
        sb.append("import com.microsoft.playwright.*;\n");
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
        sb.append("        pageObject = new ").append(pageClassName).append("(page);\n");
        sb.append("    }\n\n");

        sb.append("    @AfterMethod\n");
        sb.append("    public void tearDown() {\n");
        sb.append("        browser.close();\n");
        sb.append("        playwright.close();\n");
        sb.append("    }\n\n");

        // Happy path: llena todos los campos con datos válidos y clickea el primer botón
        sb.append("    @Test(description = \"Happy path: completa todos los campos con datos válidos\")\n");
        sb.append("    public void testHappyPath() {\n");
        for (InputInfo input : pageInfo.inputs) {
            String methodName = "fill" + capitalize(toCamelCase(input.identifierValue));
            sb.append("        pageObject.").append(methodName).append("(\"valor_valido\");\n");
        }
        if (!pageInfo.buttonTexts.isEmpty()) {
            String methodName = "click" + capitalize(toCamelCase(pageInfo.buttonTexts.get(0)));
            sb.append("        pageObject.").append(methodName).append("();\n");
        }
        sb.append("        // TODO: agregar assert de éxito esperado (ej. redirección, mensaje)\n");
        sb.append("    }\n\n");

        // Negative case: campos vacíos
        sb.append("    @Test(description = \"Negative case: envía el formulario con campos vacíos\")\n");
        sb.append("    public void testCamposVacios() {\n");
        if (!pageInfo.buttonTexts.isEmpty()) {
            String methodName = "click" + capitalize(toCamelCase(pageInfo.buttonTexts.get(0)));
            sb.append("        pageObject.").append(methodName).append("();\n");
        }
        sb.append("        // TODO: agregar assert de validación esperada (mensaje de error)\n");
        sb.append("    }\n\n");

        // Boundary/negative por cada input individual con dato inválido
        for (InputInfo input : pageInfo.inputs) {
            String methodName = "fill" + capitalize(toCamelCase(input.identifierValue));
            String testName = "test" + capitalize(toCamelCase(input.identifierValue)) + "Invalido";
            sb.append("    @Test(description = \"Boundary/negative: ").append(input.identifierValue).append(" con dato inválido\")\n");
            sb.append("    public void ").append(testName).append("() {\n");
            sb.append("        pageObject.").append(methodName).append("(\"\");\n");
            sb.append("        // TODO: agregar assert de validación esperada para este campo\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");
        return sb.toString();
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
}