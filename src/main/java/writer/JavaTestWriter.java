package writer;

import java.io.FileWriter;
import java.io.IOException;

public class JavaTestWriter {

    public static void escribir(String nombreClase, String contenido) {

        String ruta = "src/test/java/generator/" + nombreClase + ".java";

        try (FileWriter writer = new FileWriter(ruta)) {

            writer.write(contenido);

            System.out.println(nombreClase + " generado correctamente.");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}