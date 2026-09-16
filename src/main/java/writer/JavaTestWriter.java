package writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JavaTestWriter {

    public static void escribir(String rutaCarpeta, String nombreClase, String contenido) {
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String ruta = rutaCarpeta + "/" + nombreClase + ".java";

        try (FileWriter writer = new FileWriter(ruta)) {
            writer.write(contenido);
            System.out.println(nombreClase + " generado correctamente en " + ruta);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void escribirPageObject(String nombreClase, String contenido) {
        escribir("src/main/java/generator/pageobjects", nombreClase, contenido);
    }

    public static void escribirTest(String nombreClase, String contenido) {
        escribir("src/test/java/generator/tests", nombreClase, contenido);
    }
}