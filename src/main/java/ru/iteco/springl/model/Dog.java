package ru.iteco.springl.model;

import javax.persistence.*;

@Entity
@Table(name = "dog")
public class Dog extends Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String furColor;

    public Long getId() {
        return id;
    }

    public Dog setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFurColor() {
        return furColor;
    }

    public Dog setFurColor(String furColor) {
        this.furColor = furColor;
        return this;
    }
}
