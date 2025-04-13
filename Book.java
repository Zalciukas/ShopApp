package com.example.shopapp.model;

// Concrete Products
public class Book extends Product {
    private String author;
    private int pages;

    public Book(String id, String name, double price, String author, int pages, int quantity) {
        super(id, name, price, "Book", quantity);
        this.author = author;
        this.pages = pages;
    }

    public String getAuthor() {
        return author;
    }

    public int getPages() {
        return pages;
    }
}
