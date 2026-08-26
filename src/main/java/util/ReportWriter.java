package util;

import java.io.FileWriter;
import java.io.IOException;

public class ReportWriter {

    private FileWriter writer;

    public ReportWriter(String rutaArchivo) throws IOException {
        writer = new FileWriter(rutaArchivo);
    }

    public void escribirLinea(String texto) throws IOException {
        writer.write(texto + "\n");
    }

    public void cerrar() throws IOException {
        writer.close();
    }
}