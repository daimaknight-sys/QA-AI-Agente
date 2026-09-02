package security;

import java.io.IOException;
import java.util.List;

public class XssScannerDemo {
    public static void main(String[] args) throws IOException {
        XssScanner scanner = new XssScanner();
        List<String> hallazgos = scanner.escanear("https://demo.owasp-juice.shop");

        System.out.println("\n--- Resumen ---");
        if (hallazgos.isEmpty()) {
            System.out.println("No se encontraron reflejos de XSS.");
        } else {
            hallazgos.forEach(System.out::println);
        }
    }
}