package fr.isep.news.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String userName;
    private String email;

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

}
