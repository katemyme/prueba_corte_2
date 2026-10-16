package ni.edu.uam.prueba_corte_2.Model;

import java.math.BigDecimal;

public class Producto {
    private Integer idProducto;
    private String codigo;
    private String nombre;
    private String categoria;
    private BigDecimal precio;
    private int stock;

    public Producto() {
    }

    // Para productos nuevos (el ID lo genera PostgreSQL)
    public Producto(String codigo, String nombre, String categoria, BigDecimal precio, int stock) {
        this(null, codigo, nombre, categoria, precio, stock);
    }

    // Para productos leídos de la base (ResultSet -> Producto)
    public Producto(Integer idProducto, String codigo, String nombre, String categoria, BigDecimal precio, int stock) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}