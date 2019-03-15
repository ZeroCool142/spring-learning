package ru.iteco.springl.model;

import javax.persistence.*;

@Entity
@Table(name = "cat_union")
public class CatUnion extends AnimalUnion {

    String furColor;
    Boolean canSwim;

    public String getFurColor() {
        return furColor;
    }

    public CatUnion setFurColor(String furColor) {
        this.furColor = furColor;
        return this;
    }

    public Boolean getCanSwim() {
        return canSwim;
    }

    public CatUnion setCanSwim(Boolean canSwim) {
        this.canSwim = canSwim;
        return this;
    }
}
