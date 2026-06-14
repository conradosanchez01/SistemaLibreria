/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.modelos;

import com.libreria.modelos.DetalleVenta;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Venta {
    private int idVenta;
    private int idCliente;
    private Date fecha;
    private double total;
    private List<DetalleVenta> detalles;

    // Constructor vacío (por seguridad)
    public Venta() {
        this.detalles = new ArrayList<>();
    }

    // CONSTRUCTOR CON PARÁMETROS 
    public Venta(int idCliente, Date fecha, double total) {
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
        this.detalles = new ArrayList<>();
    }

    // MÉTODO PARA AGREGAR DETALLES 
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
    }

    // --- GETTERS Y SETTERS (Para que VentaDAO pueda leer los datos) ---
    
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }
    
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}