package ru.iteco.springl.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dog_join")
public class DogJoin extends AnimalJoin {

    String furColor;

    public String getFurColor() {
        return furColor;
    }

    public DogJoin setFurColor(String furColor) {
        this.furColor = furColor;
        return this;
    }
}
