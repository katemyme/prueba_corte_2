package ni.edu.uam.prueba_corte_2.Controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.prueba_corte_2.DAO.ProductoDAO;
import ni.edu.uam.prueba_corte_2.Model.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Producto_Controller {

    private static final String TODAS = "Todas";
    private static final List<String> CATEGORIAS = List.of("Papelería", "Tecnología", "Accesorios");
    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    @FXML private TextField txtId;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroCategoria;
    @FXML private CheckBox chkConExistencias;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiarFiltros;
    @FXML private Button btnRecargar;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private Label lblContador;
    @FXML private Label lblEstado;

    @FXML
    public void initialize() {
        configurarTableView();
        configurarSeleccionTableView();
        cargarCategorias();
        cargarProductos();
    }
    @FXML
    private void Nuevo_producto(ActionEvent event) {
        limpiarFormulario();
    }

    @FXML
    private void guardar_producto(ActionEvent event) {
        guardarProducto();
    }

    @FXML
    private void Actualizar_producto(ActionEvent event) {
        actualizarProducto();
    }

    @FXML
    private void Eliminar_producto(ActionEvent event) {
        eliminarProducto();
    }

    @FXML
    private void Buscar_producto(ActionEvent event) {
        boolean exito = cargarProductos();
        if (exito && listaProductos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sin coincidencias",
                    "No existen productos que coincidan con los filtros aplicados.");
        }
    }

    @FXML
    private void limpiarFiltros(ActionEvent event) {
        txtBuscar.clear();
        cmbFiltroCategoria.setValue(TODAS);
        chkConExistencias.setSelected(false);
        cargarProductos();
    }

    @FXML
    private void recargar(ActionEvent event) {
        cargarProductos();
    }


    private void configurarTableView() {
        colId.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getIdProducto()));
        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCodigo()));
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));
        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategoria()));
        colPrecio.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getPrecio()));
        colStock.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getStock()).asObject());

        tablaProductos.setItems(listaProductos);
    }

    private void configurarSeleccionTableView() {
        tablaProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        cargarProductoEnFormulario(newValue);
                    }
                });
    }

    private void cargarProductoEnFormulario(Producto p) {
        txtId.setText(String.valueOf(p.getIdProducto()));   // No editable (FXML)
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        cmbCategoria.setValue(p.getCategoria());
        txtPrecio.setText(p.getPrecio().toPlainString());
        txtStock.setText(String.valueOf(p.getStock()));
    }

    private void cargarCategorias() {
        cmbCategoria.getItems().setAll(CATEGORIAS);

        cmbFiltroCategoria.getItems().clear();
        cmbFiltroCategoria.getItems().add(TODAS);
        cmbFiltroCategoria.getItems().addAll(CATEGORIAS);
        cmbFiltroCategoria.setValue(TODAS);
    }
    private boolean cargarProductos() {
        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();

        String categoria = cmbFiltroCategoria.getValue();
        if (categoria == null || TODAS.equals(categoria)) {
            categoria = null;
        }

        boolean soloExistencias = chkConExistencias.isSelected();

        try {
            List<Producto> productos = productoDAO.buscar(texto, categoria, soloExistencias);

            listaProductos.setAll(productos);
            tablaProductos.setPlaceholder(new Label("Sin coincidencias"));
            lblContador.setText("Registros: " + productos.size());
            lblEstado.setText(productos.isEmpty() ? "Sin coincidencias" : "");
            return true;

        } catch (SQLException e) {
            listaProductos.clear();
            tablaProductos.setPlaceholder(new Label("No se pudo cargar la información"));
            lblContador.setText("Registros: -");
            lblEstado.setText("Error de conexión con la base de datos");
            mostrarErrorBaseDatos("No se pudieron cargar los productos.", e);
            return false;
        }
    }

    private void guardarProducto() {
        Producto producto = leerFormulario(null);   // null: producto nuevo, sin id
        if (producto == null) {
            return;   // Alguna validación falló; ya se mostró el mensaje.
        }

        try {

            if (productoDAO.existeCodigo(producto.getCodigo(), null)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Código duplicado",
                        "El código " + producto.getCodigo() + " ya pertenece a otro producto.");
                txtCodigo.requestFocus();
                return;
            }
            int filasAfectadas = productoDAO.insertar(producto);
            if (filasAfectadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro guardado",
                        "El producto fue registrado correctamente.");
                limpiarFormulario();
                cargarProductos();   // Recarga respetando filtros vigentes.
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Sin cambios",
                        "No se pudo registrar el producto.");
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudo guardar el producto.", e);
        }
    }
    private void actualizarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        // V10: sin fila seleccionada.
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Producto no seleccionado",
                    "Debe seleccionar un producto de la tabla antes de actualizar.");
            return;
        }

        Producto producto = leerFormulario(seleccionado.getIdProducto());
        if (producto == null) {
            return;
        }

        try {
            if (productoDAO.existeCodigo(producto.getCodigo(), producto.getIdProducto())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Código duplicado",
                        "El código ingresado pertenece a otro producto.");
                txtCodigo.requestFocus();
                return;
            }

            int filasAfectadas = productoDAO.actualizar(producto);

            if (filasAfectadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro actualizado",
                        "El producto fue actualizado correctamente.");
                limpiarFormulario();
                cargarProductos();
            } else {
                // E3: 0 filas afectadas, el producto ya no existe.
                mostrarAlerta(Alert.AlertType.WARNING, "Producto no encontrado",
                        "El producto seleccionado ya no existe en la base de datos. La lista se recargará.");
                limpiarFormulario();
                cargarProductos();
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudo actualizar el producto.", e);
        }
    }
    private void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        // V10: sin fila seleccionada.
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Producto no seleccionado",
                    "Debe seleccionar un producto de la tabla antes de eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar producto");
        confirmacion.setContentText("¿Desea eliminar el producto "
                + seleccionado.getCodigo() + " - " + seleccionado.getNombre() + "?");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            int filasAfectadas = productoDAO.eliminar(seleccionado.getIdProducto());

            if (filasAfectadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Producto eliminado",
                        "El producto fue eliminado correctamente.");
            } else {
                // E3: 0 filas afectadas.
                mostrarAlerta(Alert.AlertType.WARNING, "Producto no encontrado",
                        "El producto seleccionado ya no existe en la base de datos. La lista se recargará.");
            }
            limpiarFormulario();
            cargarProductos();

        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudo eliminar el producto.", e);
        }
    }
    private Producto leerFormulario(Integer idProducto) {

        // V1: código, trim + mayúsculas, de 1 a 10 caracteres.
        String codigo = txtCodigo.getText() == null ? "" : txtCodigo.getText().trim().toUpperCase();
        if (codigo.isEmpty() || codigo.length() > 10) {
            return rechazar(txtCodigo, "Código inválido",
                    "El código debe tener de 1 a 10 caracteres (sin contar espacios laterales).");
        }

        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (nombre.length() < 3 || nombre.length() > 80) {
            return rechazar(txtNombre, "Nombre inválido",
                    "El nombre debe tener de 3 a 80 caracteres (sin contar espacios laterales).");
        }
        String categoria = cmbCategoria.getValue();
        if (categoria == null || !CATEGORIAS.contains(categoria)) {
            return rechazar(cmbCategoria, "Categoría inválida",
                    "Seleccione una categoría: Papelería, Tecnología o Accesorios.");
        }
        String textoPrecio = txtPrecio.getText() == null ? "" : txtPrecio.getText().trim();
        BigDecimal precio;
        try {
            precio = new BigDecimal(textoPrecio);
        } catch (NumberFormatException e) {
            return rechazar(txtPrecio, "Precio inválido",
                    "El precio debe ser un número. Use punto como separador decimal (ej. 65.00).");
        }
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            return rechazar(txtPrecio, "Precio inválido", "El precio debe ser mayor que 0.");
        }
        if (precio.stripTrailingZeros().scale() > 2) {
            return rechazar(txtPrecio, "Precio inválido", "El precio admite como máximo 2 decimales.");
        }
        if (precio.compareTo(PRECIO_MAXIMO) > 0) {
            return rechazar(txtPrecio, "Precio inválido",
                    "El precio debe estar entre 0.01 y 99999999.99.");
        }
        precio = precio.setScale(2);
        String textoStock = txtStock.getText() == null ? "" : txtStock.getText().trim();
        int stock;
        try {
            stock = Integer.parseInt(textoStock);   // Rechaza "abc", "2.5" y 2147483648
        } catch (NumberFormatException e) {
            return rechazar(txtStock, "Stock inválido",
                    "El stock debe ser un número entero entre 0 y 2147483647.");
        }
        if (stock < 0) {
            return rechazar(txtStock, "Stock inválido", "El stock no puede ser negativo.");
        }

        return new Producto(idProducto, codigo, nombre, categoria, precio, stock);
    }

    private Producto rechazar(Control campo, String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.WARNING, titulo, mensaje);
        campo.requestFocus();
        return null;
    }
    private void limpiarFormulario() {
        txtId.clear();
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear();
        txtStock.clear();
        tablaProductos.getSelectionModel().clearSelection();
        txtCodigo.requestFocus();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    private void mostrarErrorBaseDatos(String encabezado, SQLException e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de base de datos");
        alerta.setHeaderText(encabezado);
        alerta.setContentText(e.getMessage());
        alerta.showAndWait();
    }
}