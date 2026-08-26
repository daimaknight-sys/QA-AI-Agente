package ai;

public class AIAnalyzerTest {
    public static void main(String[] args) {
        AIAnalyzer analyzer = new AIAnalyzer();

        String resultadosFalsos = """
                Test 1: Login con usuario válido - PASSED
                Test 2: Login con contraseña incorrecta - FAILED (esperaba error 401, recibió 200)
                Test 3: Formulario de registro - PASSED
                Test 4: Botón de checkout - FAILED (elemento no encontrado)
                """;

        System.out.println("Enviando resultados a la IA...\n");
        String analisis = analyzer.analizarResultados(resultadosFalsos);
        System.out.println("=== ANÁLISIS DE LA IA ===");
        System.out.println(analisis);
    }
}