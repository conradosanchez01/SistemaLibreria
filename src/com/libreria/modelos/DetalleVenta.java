/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.modelos;

public class DetalleVenta {
    private int idLibro;
    private int cantidad;
    private double precioUnitario;

    // Constructor vacío
    public DetalleVenta() {
    }

    // CONSTRUCTOR CON PARÁMETROS (Soluciona el Error 2)
    public DetalleVenta(int idLibro, int cantidad, double precioUnitario) {
        this.idLibro = idLibro;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // --- GETTERS Y SETTERS (Para que VentaDAO pueda leer los datos) ---
    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}