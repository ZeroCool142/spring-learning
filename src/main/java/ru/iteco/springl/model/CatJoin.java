package ru.iteco.springl.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cat_join")
public class CatJoin extends AnimalJoin {

    String furColor;
    Boolean canSwim;

    public String getFurColor() {
        return furColor;
    }

    public CatJoin setFurColor(String furColor) {
        this.furColor = furColor;
        return this;
    }

    public Boolean getCanSwim() {
        return canSwim;
    }

    public CatJoin setCanSwim(Boolean canSwim) {
        this.canSwim = canSwim;
        return this;
    }
}
