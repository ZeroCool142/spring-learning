package ru.iteco.springl.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("dog")
public class DogSingle extends AnimalSingle {

    String furColor;

    public String getFurColor() {
        return furColor;
    }

    public DogSingle setFurColor(String furColor) {
        this.furColor = furColor;
        return this;
    }
}
