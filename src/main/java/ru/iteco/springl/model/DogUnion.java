package ru.iteco.springl.model;

import javax.persistence.*;

@Entity
@Table(name = "dog_union")
public class DogUnion extends AnimalUnion {

    String furColor;

    public String getFurColor() {
        return furColor;
    }

    public DogUnion setFurColor(String furColor) {
        this.furColor = furColor;
        return this;
    }
}
