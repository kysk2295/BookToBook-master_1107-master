package com.example.booktobook;

class MyBookData {
    public String book_image;
    public String title;
    public String author;
    public String publisher;

    public MyBookData(){

    }

    public MyBookData(String book_image, String title, String author, String publisher){
        this.book_image = book_image;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }

    public String getBook_image() {
        return book_image;
    }

    public void setBook_image(String book_image) {
        this.book_image = book_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
