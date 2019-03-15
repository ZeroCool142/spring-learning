package ru.iteco.springl.model;

import org.springframework.cache.annotation.Cacheable;

import javax.persistence.*;

@Entity
@Table(name = "cat")
@Cacheable("test")
public class Cat extends Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String furColor;
    Boolean canSwim;

    public Long getId() {
        return id;
    }

    public Cat setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFurColor() {
        return furColor;
    }

    public Cat setFurColor(String furColor) {
        this.furColor = furColor;
        return this;
    }

    public Boolean getCanSwim() {
        return canSwim;
    }

    public Cat setCanSwim(Boolean canSwim) {
        this.canSwim = canSwim;
        return this;
    }
}
