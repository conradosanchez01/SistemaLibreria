package com.libreria.dao;

import com.libreria.conexion.ConexionDB;
import com.libreria.modelos.Venta;
import com.libreria.modelos.DetalleVenta;
import com.libreria.excepciones.StockInsuficienteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VentaDAO {

    public boolean registrarVentaCompleta(Venta venta) throws StockInsuficienteException, SQLException {
        Connection con = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psCheckStock = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;

        String sqlVenta = "INSERT INTO ventas (id_cliente, fecha, total) VALUES (?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_ventas (id_venta, id_libro, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        String sqlCheckStock = "SELECT stock, titulo FROM libros WHERE id_libro = ?";
        String sqlUpdateStock = "UPDATE libros SET stock = stock - ? WHERE id_libro = ?";

        try {
            con = ConexionDB.conectar();
            if (con == null) {
                throw new SQLException("No se pudo establecer conexión con la base de datos.");
            }
            
            con.setAutoCommit(false);

            // 1. Verificar stock disponible
            for (DetalleVenta detalle : venta.getDetalles()) {
                psCheckStock = con.prepareStatement(sqlCheckStock);
                psCheckStock.setInt(1, detalle.getIdLibro());
                rs = psCheckStock.executeQuery();
                
                if (rs.next()) {
                    int stockActual = rs.getInt("stock");
                    String tituloLibro = rs.getString("titulo");
                    
                    if (stockActual < detalle.getCantidad()) {
                        throw new StockInsuficienteException("Stock insuficiente para el libro: " 
                                + tituloLibro + " (Disponibles: " + stockActual + ", Solicitados: " + detalle.getCantidad() + ")");
                    }
                } else {
                    throw new SQLException("El libro con ID " + detalle.getIdLibro() + " no existe en el catálogo.");
                }
                rs.close();
                psCheckStock.close();
            }

            // 2. Insertar cabecera de la Venta
            psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setInt(1, venta.getIdCliente());
            psVenta.setTimestamp(2, new java.sql.Timestamp(venta.getFecha().getTime()));
            psVenta.setDouble(3, venta.getTotal());
            psVenta.executeUpdate();

            rs = psVenta.getGeneratedKeys();
            int idVentaGenerado = -1;
            if (rs.next()) {
                idVentaGenerado = rs.getInt(1);
            } else {
                throw new SQLException("Error crítico: No se pudo obtener el ID de la venta generada.");
            }
            rs.close();

            // 3. Insertar detalles y restar stock
            psDetalle = con.prepareStatement(sqlDetalle);
            psUpdateStock = con.prepareStatement(sqlUpdateStock);

            for (DetalleVenta detalle : venta.getDetalles()) {
                psDetalle.setInt(1, idVentaGenerado);
                psDetalle.setInt(2, detalle.getIdLibro());
                psDetalle.setInt(3, detalle.getCantidad());
                psDetalle.setDouble(4, detalle.getCantidad() * detalle.getPrecioUnitario());
                psDetalle.executeUpdate();

                psUpdateStock.setInt(1, detalle.getCantidad());
                psUpdateStock.setInt(2, detalle.getIdLibro());
                psUpdateStock.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error haciendo rollback: " + rollbackEx.getMessage());
                }
            }
            throw ex;
        } finally {
            try {
                if (rs != null) rs.close();
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (psUpdateStock != null) psUpdateStock.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos en VentaDAO: " + e.getMessage());
            }
        }
    }
    
    // ---------------------------------------------------------
    // MÓDULO REPORTES: Calcular total histórico
    // ---------------------------------------------------------
    public double obtenerGananciasTotales() {
        double total = 0;
        String sql = "SELECT SUM(total) as ganancia_total FROM ventas";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble("ganancia_total");
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular ganancias: " + e.getMessage());
        }
        return total;
    }

//    // ---------------------------------------------------------
//    // MÓDULO REPORTES: Filtrar ventas por mes desde SQL
//    // ---------------------------------------------------------
//    public java.util.List<Venta> obtenerVentasPorMes(int mes) {
//        java.util.List<Venta> lista = new java.util.ArrayList<>();
//        // Le pedimos a MySQL que filtre usando la función MONTH()
//        String sql = "SELECT id_venta, fecha, total FROM ventas WHERE MONTH(fecha) = ?";
//        try (Connection con = ConexionDB.conectar();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//            
//            ps.setInt(1, mes);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    Venta v = new Venta();
//                    v.setIdVenta(rs.getInt("id_venta"));
//                    v.setFecha(rs.getTimestamp("fecha"));
//                    v.setTotal(rs.getDouble("total"));
//                    lista.add(v);
//                }
//            }
//        } catch (SQLException e) {
//            System.err.println("Error al filtrar por mes: " + e.getMessage());
//        }
//        return lista;
//    }
   // ---------------------------------------------------------
    // MÓDULO REPORTES: Filtrar ventas por mes con Detalle de Libros
    // ---------------------------------------------------------
    public java.util.List<Object[]> obtenerVentasPorMesDetalladas(int mes) {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        
        // Magia SQL: GROUP_CONCAT une los títulos de los libros separados por coma
        String sql = "SELECT v.id_venta, GROUP_CONCAT(l.titulo SEPARATOR ', ') as libros_vendidos, v.fecha, v.total " +
                     "FROM ventas v " +
                     "JOIN detalles_ventas d ON v.id_venta = d.id_venta " +
                     "JOIN libros l ON d.id_libro = l.id_libro " +
                     "WHERE MONTH(v.fecha) = ? " +
                     "GROUP BY v.id_venta, v.fecha, v.total";
                     
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, mes);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[4];
                    fila[0] = rs.getInt("id_venta");
                    fila[1] = rs.getString("libros_vendidos"); // Acá viene el texto "Libro A, Libro B"
                    fila[2] = rs.getTimestamp("fecha");
                    fila[3] = rs.getDouble("total");
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar por mes con detalles: " + e.getMessage());
        }
        return lista;
    }
}