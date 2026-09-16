package generator;

import analyzer.ButtonInfo;
import analyzer.InputInfo;
import analyzer.PageInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageObjectGenerator {

    /**
     * Resultado de la generación: el código de la clase Page Object,
     * más los nombres de método reales (ya deduplicados) para cada input/botón,
     * en el mismo orden que pageInfo.inputs / pageInfo.buttons.
     * TestClassGenerator debe usar estas listas en vez de recalcular nombres.
     */
    public static class Result {
        public final String code;
        public final List<String> inputFillMethods;   // paralela a pageInfo.inputs
        public final List<String> buttonClickMethods; // paralela a pageInfo.buttons

        public Result(String code, List<String> inputFillMethods, List<String> buttonClickMethods) {
            this.code = code;
            this.inputFillMethods = inputFillMethods;
            this.buttonClickMethods = buttonClickMethods;
        }
    }

    public Result generate(String className, PageInfo pageInfo) {
        StringBuilder sb = new StringBuilder();

        // Nombres de campo, calculados una sola vez y deduplicados
        Set<String> usedFieldNames = new HashSet<>();
        List<String> inputFieldNames = new ArrayList<>();
        for (InputInfo input : pageInfo.inputs) {
            String base = toCamelCase(input.identifierValue) + "Input";
            inputFieldNames.add(uniqueName(base, usedFieldNames));
        }
        List<String> buttonFieldNames = new ArrayList<>();
        for (ButtonInfo button : pageInfo.buttons) {
            String base = toCamelCase(button.text) + "Button";
            buttonFieldNames.add(uniqueName(base, usedFieldNames));
        }

        sb.append("package generator.pageobjects;\n\n");
        sb.append("import com.microsoft.playwright.Page;\n");
        sb.append("import com.microsoft.playwright.Locator;\n");
        sb.append("import com.microsoft.playwright.options.AriaRole;\n\n");
        sb.append("public class ").append(className).append(" {\n\n");
        sb.append("    private final Page page;\n\n");

        for (int i = 0; i < pageInfo.inputs.size(); i++) {
            sb.append("    private final Locator ").append(inputFieldNames.get(i)).append(";\n");
        }
        for (int i = 0; i < pageInfo.buttons.size(); i++) {
            sb.append("    private final Locator ").append(buttonFieldNames.get(i)).append(";\n");
        }

        sb.append("\n    public ").append(className).append("(Page page) {\n");
        sb.append("        this.page = page;\n");

        for (int i = 0; i < pageInfo.inputs.size(); i++) {
            InputInfo input = pageInfo.inputs.get(i);
            String fieldName = inputFieldNames.get(i);
            String selector = buildSelector(input);
            sb.append("        this.").append(fieldName)
                    .append(" = page.locator(\"").append(escapeJavaString(selector)).append("\");\n");
        }
        for (int i = 0; i < pageInfo.buttons.size(); i++) {
            ButtonInfo button = pageInfo.buttons.get(i);
            String fieldName = buttonFieldNames.get(i);
            String selector = buildButtonSelector(button);
            if (selector != null) {
                sb.append("        this.").append(fieldName)
                        .append(" = page.locator(\"").append(escapeJavaString(selector)).append("\");\n");
            } else {
                sb.append("        this.").append(fieldName)
                        .append(" = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(\"")
                        .append(escapeJavaString(button.text)).append("\"));\n");
            }
        }

        sb.append("    }\n\n");

        // Métodos "fill" — nombres deduplicados, guardados para devolver en el Result
        Set<String> usedMethodNames = new HashSet<>();
        List<String> inputFillMethods = new ArrayList<>();
        for (int i = 0; i < pageInfo.inputs.size(); i++) {
            InputInfo input = pageInfo.inputs.get(i);
            String fieldName = inputFieldNames.get(i);
            String methodBase = "fill" + capitalize(toCamelCase(input.identifierValue));
            String methodName = uniqueName(methodBase, usedMethodNames);
            inputFillMethods.add(methodName);
            sb.append("    public void ").append(methodName).append("(String value) {\n");
            sb.append("        ").append(fieldName).append(".fill(value);\n");
            sb.append("    }\n\n");
        }

        // Métodos "click" — mismo criterio
        List<String> buttonClickMethods = new ArrayList<>();
        for (int i = 0; i < pageInfo.buttons.size(); i++) {
            ButtonInfo button = pageInfo.buttons.get(i);
            String fieldName = buttonFieldNames.get(i);
            String methodBase = "click" + capitalize(toCamelCase(button.text));
            String methodName = uniqueName(methodBase, usedMethodNames);
            buttonClickMethods.add(methodName);
            sb.append("    public void ").append(methodName).append("() {\n");
            sb.append("        ").append(fieldName).append(".click();\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");
        return new Result(sb.toString(), inputFillMethods, buttonClickMethods);
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

    private String buildSelector(InputInfo input) {
        switch (input.identifierType) {
            case "name": return "[name='" + input.identifierValue + "']";
            case "id": return "#" + input.identifierValue;
            case "placeholder": return "[placeholder='" + input.identifierValue + "']";
            default: return "input";
        }
    }

    private String buildButtonSelector(ButtonInfo button) {
        if (button.id != null && !button.id.isEmpty()) {
            return "#" + button.id;
        }
        if (button.ariaLabel != null && !button.ariaLabel.isEmpty()) {
            return "[aria-label='" + button.ariaLabel + "']";
        }
        if (button.title != null && !button.title.isEmpty()) {
            return "[title='" + button.title + "']";
        }
        return null;
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