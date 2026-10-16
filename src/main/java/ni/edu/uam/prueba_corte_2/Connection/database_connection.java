package ni.edu.uam.prueba_corte_2.Connection;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
 * La configuración real NO vive en el código: se lee de las variables de
 * entorno DB_URL, DB_USER y DB_PASSWORD o, si no existen, del archivo
 * db.properties en la carpeta del proyecto (excluido por .gitignore).
 * Ver db.properties.example.
 */
public class database_connection {
    private static final Path ARCHIVO_CONFIG = Path.of("db.properties");

    private database_connection() {
    }

    public static Connection getConnection() throws SQLException {
        Properties config = leerArchivoConfig();
        return DriverManager.getConnection(
                valor(config, "DB_URL", "db.url"),
                valor(config, "DB_USER", "db.user"),
                valor(config, "DB_PASSWORD", "db.password"));
    }

    private static Properties leerArchivoConfig() throws SQLException {
        Properties config = new Properties();
        if (Files.exists(ARCHIVO_CONFIG)) {
            try (Reader lector = Files.newBufferedReader(ARCHIVO_CONFIG, StandardCharsets.UTF_8)) {
                config.load(lector);
            } catch (IOException e) {
                throw new SQLException("No se pudo leer " + ARCHIVO_CONFIG.toAbsolutePath(), "08001", e);
            }
        }
        return config;
    }

    // La variable de entorno tiene prioridad sobre el archivo.
    private static String valor(Properties config, String variableEntorno, String clave) throws SQLException {
        String valor = System.getenv(variableEntorno);
        if (valor == null || valor.isBlank()) {
            valor = config.getProperty(clave);
        }
        if (valor == null || valor.isBlank()) {
            // SQLState 08001: el DAO lo muestra como error de conexión/configuración.
            throw new SQLException("Falta '" + clave + "' en db.properties o la variable de entorno "
                    + variableEntorno + ".", "08001");
        }
        return valor;
    }
}
