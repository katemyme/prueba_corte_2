# Catálogo de productos – Papelería Campus

Aplicación de escritorio en **JavaFX** que administra el catálogo de productos de la papelería sobre **PostgreSQL** mediante **JDBC**. Permite:

- Registrar, consultar, actualizar y eliminar productos (CRUD).
- Buscar por código o nombre (texto parcial, sin distinguir mayúsculas).
- Filtrar por categoría y mostrar solo los productos con existencias. Los filtros se pueden combinar.
- Validar los datos del formulario antes de enviarlos a la base. La base, además, tiene restricciones `CHECK` y `UNIQUE`.

## Autor

Víctor Mateo Alcocer López

## Versiones

| Componente | Versión |
|---|---|
| JDK | Eclipse Temurin 21.0.10 (nivel de lenguaje Java 21) |
| JavaFX | 21.0.6 |
| PostgreSQL (servidor) | 18.4 |
| Driver JDBC de PostgreSQL | 42.7.13 |
| Maven (wrapper incluido) | 3.8.5 |
| IDE | IntelliJ IDEA 2025.3.3 |
| Sistema operativo | Windows 11 |

## Dependencias

Declaradas en `pom.xml`:

| Dependencia | Versión | Alcance | Uso |
|---|---|---|---|
| `org.openjfx:javafx-controls` | 21.0.6 | compile | Controles de la interfaz (tabla, campos, botones, alertas) |
| `org.openjfx:javafx-fxml` | 21.0.6 | compile | Carga de la vista `productos-view.fxml` |
| `org.postgresql:postgresql` | 42.7.13 | compile | Driver JDBC para conectarse a PostgreSQL |
| `org.junit.jupiter:junit-jupiter-api` / `-engine` | 5.12.1 | test | Declaradas por la plantilla del proyecto. Por ahora no hay pruebas automatizadas |

Plugins de Maven:

- `maven-compiler-plugin` 3.13.0 (Java 21)
- `javafx-maven-plugin` 0.0.8 (para `javafx:run`)

## Configuración

### 1. Base de datos

Los scripts están en `src/main/java/ni/edu/uam/prueba_corte_2/sql/`:

| Script | Contenido |
|---|---|
| `Script.sql` | Crea la base `papeleria_campus`, el esquema `catalogos` y la tabla `catalogos.producto` con sus restricciones |
| `Datos.sql` | Inserta los 12 productos obligatorios |

Pasos (por ejemplo, desde el Query Tool de pgAdmin):

1. Conectado a la base `postgres`, ejecute la primera sentencia de `Script.sql`: `CREATE DATABASE papeleria_campus;`.
2. Conéctese a la base `papeleria_campus` y ejecute el resto de `Script.sql` (esquema y tabla).
3. En la misma base, ejecute `Datos.sql`.

Estructura de la tabla `catalogos.producto`:

| Columna | Tipo | Restricción |
|---|---|---|
| `id_producto` | `SERIAL` | Llave primaria |
| `codigo` | `VARCHAR(10)` | `NOT NULL`, `UNIQUE`; en mayúsculas y sin espacios laterales, de 1 a 10 caracteres |
| `nombre` | `VARCHAR(80)` | `NOT NULL`; de 3 a 80 caracteres |
| `categoria` | `VARCHAR(20)` | Solo `Papelería`, `Tecnología` o `Accesorios` |
| `precio` | `NUMERIC(10,2)` | Mayor que 0 |
| `stock` | `INTEGER` | Mayor o igual que 0 |
| `estado` | `SMALLINT` | 0 o 1 (por defecto 1) |

### 2. Conexión de la aplicación

Las credenciales **no están en el código fuente**. La clase `database_connection` las lee de dos lugares, en este orden de prioridad:

1. Variables de entorno.
2. El archivo `db.properties` en la carpeta raíz del proyecto.

| Variable de entorno | Clave en `db.properties` | Ejemplo |
|---|---|---|
| `DB_URL` | `db.url` | `jdbc:postgresql://localhost:5432/papeleria_campus` |
| `DB_USER` | `db.user` | `postgres` |
| `DB_PASSWORD` | `db.password` | *(su contraseña)* |

Para configurarla con el archivo, copie el ejemplo y edite sus valores:

```bat
copy db.properties.example db.properties
```

`db.properties` contiene la contraseña real y está excluido en `.gitignore`. En el repositorio solo se incluye `db.properties.example`, que no tiene secretos.

Si falta algún valor o la conexión falla, la aplicación muestra el mensaje "No se pudo conectar con la base de datos. Verifique la configuración." y registra el detalle en la consola.

## Ejecución

Requisitos: JDK 21 (con `JAVA_HOME` configurado), PostgreSQL en ejecución y la base creada según la sección anterior.

**Opción A: Maven Wrapper** (desde la carpeta raíz del proyecto):

```bat
mvnw.cmd clean javafx:run
```

En Linux/macOS: `./mvnw clean javafx:run`.

**Opción B: IntelliJ IDEA**

1. Abra la carpeta del proyecto y recargue el proyecto Maven (*Reload All Maven Projects*).
2. Ejecute la clase `ni.edu.uam.prueba_corte_2.Launcher`.

El directorio de trabajo debe ser la raíz del proyecto (es el valor por defecto) para que se encuentre `db.properties`.

## Organización de clases y fuentes

```
prueba_corte_2/
├── pom.xml
├── db.properties.example        Configuración de ejemplo (sin secretos)
├── db.properties                Configuración real (ignorada por git)
├── README.md
├── pruebas.md                   Matriz de resultados de las pruebas
├── evidencias/                  Capturas de pantalla de las pruebas
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── ni/edu/uam/prueba_corte_2/
    │       ├── Launcher.java
    │       ├── Application/Producto_Application.java
    │       ├── Controller/Producto_Controller.java
    │       ├── DAO/ProductoDAO.java
    │       ├── Connection/database_connection.java
    │       ├── Model/Producto.java
    │       └── sql/
    │           ├── Script.sql
    │           └── Datos.sql
    └── resources/ni/edu/uam/prueba_corte_2/
        └── productos-view.fxml
```

| Clase / archivo | Paquete | Responsabilidad |
|---|---|---|
| `Launcher` | `ni.edu.uam.prueba_corte_2` | Punto de entrada (`main`); lanza la aplicación JavaFX |
| `Producto_Application` | `…Application` | Carga `productos-view.fxml` y muestra la ventana principal |
| `Producto_Controller` | `…Controller` | Controlador de la vista: eventos de botones, validación del formulario, tabla, filtros y mensajes al usuario |
| `ProductoDAO` | `…DAO` | Todo el SQL de `catalogos.producto` con `PreparedStatement`: búsqueda con filtros, verificación de código duplicado, insertar, actualizar y eliminar. Traduce el SQLState a mensajes seguros y lo registra en la consola |
| `database_connection` | `…Connection` | Abre la conexión JDBC con la configuración externa (`db.properties` o variables de entorno) |
| `Producto` | `…Model` | Modelo de datos de un producto |
| `productos-view.fxml` | recursos | Vista: formulario, botones, filtros y tabla |
| `module-info.java` | — | Módulo de la aplicación; requiere `javafx.controls`, `javafx.fxml`, `java.sql` y `org.postgresql.jdbc` |

Flujo de una operación:

```
productos-view.fxml → Producto_Controller → ProductoDAO → database_connection → PostgreSQL
```

## Pruebas

La matriz de resultados está en [`pruebas.md`](pruebas.md) y las capturas en [`evidencias/`](evidencias/).
