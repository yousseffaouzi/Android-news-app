package fr.isep.news.Model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class News {
    private String status;
    private int totalResults;
    private ArrayList<Newsdetail> articles;

    public News(String status, int totalResults, ArrayList<Newsdetail> news) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = news;
    }

}
