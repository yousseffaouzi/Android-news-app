package fr.isep.news.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {

    private String CategoryName;

    public Category(String categoryName) {
        CategoryName = categoryName;
    }

}
