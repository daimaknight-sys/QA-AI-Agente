package generator;

import analyzer.PageInfo;

public class TestCaseGenerator {

    public String generar(PageInfo info) {
        StringBuilder sb = new StringBuilder();

        sb.append("=== CASOS DE PRUEBA GENERADOS ===\n\n");

        // Happy path
        sb.append("1. HAPPY PATH\n");
        sb.append("   Completar todos los campos con datos válidos");
        if (!info.inputNames.isEmpty()) {
            sb.append(" (").append(String.join(", ", info.inputNames)).append(")");
        }
        sb.append(" y enviar el formulario.\n\n");

        // Negative: campos vacíos
        sb.append("2. NEGATIVE - CAMPOS VACÍOS\n");
        sb.append("   Enviar el formulario sin completar ningún campo y verificar mensajes de validación.\n\n");

        // Negative/boundary por cada input
        int n = 3;
        for (String input : info.inputNames) {
            sb.append(n++).append(". BOUNDARY/NEGATIVE - ").append(input.toUpperCase()).append("\n");
            sb.append("   Probar el campo '").append(input).append("' con datos inválidos, ")
                    .append("valores límite y caracteres especiales.\n\n");
        }

        // Casos por botón
        for (String boton : info.buttonTexts) {
            sb.append(n++).append(". INTERACCIÓN - BOTÓN '").append(boton).append("'\n");
            sb.append("   Verificar que el botón '").append(boton)
                    .append("' realiza la acción esperada y maneja errores correctamente.\n\n");
        }

        if (!info.hasForm) {
            sb.append(n++).append(". SIN FORMULARIO DETECTADO\n");
            sb.append("   La página no contiene formularios; enfocar pruebas en navegación y contenido.\n\n");
        }

        return sb.toString();
    }
}