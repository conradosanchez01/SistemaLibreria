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
}