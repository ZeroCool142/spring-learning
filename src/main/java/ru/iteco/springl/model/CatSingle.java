package ru.iteco.springl.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("cat")
public class CatSingle extends AnimalSingle {

    String furColor;
    Boolean canSwim;

    public String getFurColor() {
        return furColor;
    }

    public CatSingle setFurColor(String furColor) {
        this.furColor = furColor;
        return this;
    }

    public Boolean getCanSwim() {
        return canSwim;
    }

    public CatSingle setCanSwim(Boolean canSwim) {
        this.canSwim = canSwim;
        return this;
    }
}
