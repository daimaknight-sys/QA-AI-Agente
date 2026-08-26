package generator;

import analyzer.PageInfo;
import java.util.LinkedHashSet;
import java.util.Set;

public class TestCaseGenerator {

    public String generar(PageInfo info) {
        StringBuilder casos = new StringBuilder();
        Set<String> casosUnicos = new LinkedHashSet<>();

        for (String nombre : info.inputNames) {
            casosUnicos.add("Caso sugerido: Verificar que el campo '" + nombre + "' acepte texto");
        }

        for (String texto : info.buttonTexts) {
            if (!texto.startsWith("boton_sin_texto_")) {
                casosUnicos.add("Caso sugerido: Verificar que el botón '" + texto + "' sea clickeable");
            }
        }

        if (info.hasForm) {
            casosUnicos.add("Caso sugerido: Verificar el envío del formulario con datos válidos");
        }

        for (String caso : casosUnicos) {
            casos.append(caso).append("\n");
            System.out.println(caso);
        }

        return casos.toString();
    }
}