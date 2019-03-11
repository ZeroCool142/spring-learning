package ru.iteco.springl.components;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.iteco.springl.ShapeQualifier;
import ru.iteco.springl.ShapeType;
import ru.iteco.springl.components.condition.PolygonCondition;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Objects;

@Component
@Scope("prototype")
@Conditional(PolygonCondition.class)
@ShapeQualifier(ShapeType.POLYGON)
public class RandomPolygon implements Shape {
    private BigDecimal r;
    private String name;

    public RandomPolygon() {
        System.out.println("Calling constructor of RandomPolygon");
        r = new BigDecimal(random.nextDouble());
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Calling @PostConstruct method in " + name + " bean");
    }

    @Override
    public BigDecimal getArea() {
        return r.multiply(r).multiply(BigDecimal.valueOf(Math.PI));
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RandomPolygon that = (RandomPolygon) o;
        return Objects.equals(r, that.r) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, name);
    }
}
