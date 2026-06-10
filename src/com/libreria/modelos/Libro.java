package com.libreria.modelos;

public class Libro {
    private int idLibro;
    private String titulo;
    private String autor;
    private double precio;
    private int stock;
    private int idCategoria;

    public Libro() {
    }

    public Libro(int idLibro, String titulo, String autor, double precio, int stock, int idCategoria) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    public Libro(String titulo, String autor, double precio, int stock, int idCategoria) {
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public double getPrecio() { return precio; }
    public double getPrecio(double precio) { return this.precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
}