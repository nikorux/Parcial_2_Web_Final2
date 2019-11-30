package edu.pucmm.parcial2.Encapsulation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String name;
    private String password;
    private boolean admin;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Url> urls;

    public User() {
    }

    public User(String username, String name, String password, boolean admin, Set<Url> urls) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.admin = admin;
        this.urls = urls;
    }

    public User(String username, String name, String password, boolean admin) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.admin = admin;
        this.urls = new HashSet<>();
    }
}
