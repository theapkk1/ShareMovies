package com.SMAPAppProjectGroup13.sharemovies;

public class Movie {
    private String title;
    private String genre;
    private String description;
    private String imdbRate;
    private String note;
    private String image;

    public Movie(String title, String genre, String description, String imdbRate, String personalRate, String note, String image) {
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.imdbRate = imdbRate;
        this.personalRate = personalRate;
        this.note = note;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getImdbRate() {
        return imdbRate;
    }

    public void setImdbRate(String imdbRate) {
        this.imdbRate = imdbRate;
    }
    private String personalRate;

    public String getPersonalRate() {
        return personalRate;
    }

    public void setPersonalRate(String personalRate) {
        this.personalRate = personalRate;
    }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
