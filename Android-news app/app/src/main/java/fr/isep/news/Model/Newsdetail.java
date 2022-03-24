package fr.isep.news.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Newsdetail {
    private String title;
    private String author;
    private String publishedAt;
    private String description;
    private String url;
    private String urlToImage;
    private String content;

    public Newsdetail(String title, String author, String publishedAt, String description, String url, String urlToImage, String content) {
        this.title = title;
        this.author = author;
        this.publishedAt = publishedAt;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.content = content;
    }


}
