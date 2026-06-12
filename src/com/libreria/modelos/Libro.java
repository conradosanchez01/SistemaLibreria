package com.libreria.modelos;

public class Libro {
    private int idLibro;
    private String isbn; 
    private String titulo;
    private String autor;
    private double precio;
    private int stock;
    private int idCategoria;

    public Libro() {
    }
// para leer, porque el libro ya existe
    public Libro(int idLibro, String isbn, String titulo, String autor, double precio, int stock, int idCategoria) {
        this.idLibro = idLibro;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }
//para insertar gracias al auto_increment
    public Libro(String isbn, String titulo, String autor, double precio, int stock, int idCategoria) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
}