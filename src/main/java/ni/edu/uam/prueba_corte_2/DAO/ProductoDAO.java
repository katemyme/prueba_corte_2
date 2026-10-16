package ni.edu.uam.prueba_corte_2.DAO;

import ni.edu.uam.prueba_corte_2.Model.Producto;
import ni.edu.uam.prueba_corte_2.Connection.database_connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductoDAO {

    private static final Logger LOG = Logger.getLogger(ProductoDAO.class.getName());

    public List<Producto> buscar(String texto, String categoria, boolean soloExistencias) throws SQLException {

        StringBuilder sql = new StringBuilder("""
                SELECT id_producto, codigo, nombre, categoria, precio, stock
                FROM catalogos.producto
                WHERE 1 = 1
                """);

        List<Object> parametros = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("""
                    AND (codigo ILIKE ? OR nombre ILIKE ?)
                    """);
            parametros.add("%" + texto + "%");
            parametros.add("%" + texto + "%");
        }

        if (categoria != null) {
            sql.append("""
                    AND categoria = ?
                    """);
            parametros.add(categoria);
        }

        if (soloExistencias) {
            sql.append("""
                    AND stock > 0
                    """);
        }
        sql.append("""
                ORDER BY codigo ASC
                """);

        List<Producto> productos = new ArrayList<>();

        try (
                Connection conexion = database_connection.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql.toString())
        ) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(crearProductoDesdeResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw manejarError("SELECT buscar", e);
        }

        return productos;
    }
    public boolean existeCodigo(String codigo, Integer idExcluir) throws SQLException {

        String sql = """
                SELECT 1
                FROM catalogos.producto
                WHERE codigo = ?
                  AND (?::integer IS NULL OR id_producto <> ?)
                """;

        try (
                Connection conexion = database_connection.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setString(1, codigo);
            ps.setObject(2, idExcluir, Types.INTEGER);
            ps.setObject(3, idExcluir, Types.INTEGER);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw manejarError("SELECT existeCodigo", e);
        }
    }
    public int insertar(Producto p) throws SQLException {

        String sql = """
                INSERT INTO catalogos.producto (codigo, nombre, categoria, precio, stock)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion = database_connection.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setBigDecimal(4, p.getPrecio());
            ps.setInt(5, p.getStock());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw manejarError("INSERT", e);
        }
    }

    public int actualizar(Producto p) throws SQLException {

        String sql = """
                UPDATE catalogos.producto
                SET codigo = ?,
                    nombre = ?,
                    categoria = ?,
                    precio = ?,
                    stock = ?
                WHERE id_producto = ?
                """;

        try (
                Connection conexion = database_connection.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setBigDecimal(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            ps.setInt(6, p.getIdProducto());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw manejarError("UPDATE", e);
        }
    }

    public int eliminar(int idProducto) throws SQLException {

        String sql = """
                DELETE FROM catalogos.producto
                WHERE id_producto = ?
                """;

        try (
                Connection conexion = database_connection.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw manejarError("DELETE", e);
        }
    }

    private Producto crearProductoDesdeResultSet(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getBigDecimal("precio"),
                rs.getInt("stock")
        );
    }

    private SQLException manejarError(String operacion, SQLException e) {
        LOG.log(Level.SEVERE, "Operación: {0} | SQLState: {1} | Detalle: {2}",
                new Object[]{operacion, e.getSQLState(), e.getMessage()});

        return new SQLException(mensajeSeguro(e), e.getSQLState(), e);
    }

    private String mensajeSeguro(SQLException e) {
        String estado = e.getSQLState();

        if (estado == null) {
            return "Ocurrió un error al acceder a la base de datos.";
        }
        if (estado.equals("23505")) {          // UNIQUE
            return "Ya existe un producto con ese código.";
        }
        if (estado.equals("23514")) {          // CHECK
            return "Los datos no cumplen las reglas de la base de datos.";
        }
        if (estado.startsWith("08") || estado.equals("28P01")) {   // conexión / autenticación
            return "No se pudo conectar con la base de datos. Verifique la configuración.";
        }
        return "Ocurrió un error al acceder a la base de datos.";
    }
}