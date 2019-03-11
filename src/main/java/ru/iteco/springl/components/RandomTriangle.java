package ru.iteco.springl.components;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.iteco.springl.ShapeQualifier;
import ru.iteco.springl.ShapeType;

import java.math.BigDecimal;
import java.util.Objects;

@Component
@Scope("prototype")
@ShapeQualifier(ShapeType.TRIANGLE)
@Profile("circle")
public class RandomTriangle implements Shape {

    private BigDecimal r;
    private String name;

    public RandomTriangle() {
        r = new BigDecimal(random.nextDouble());
    }

    @Override
    public BigDecimal getArea() {
        return r.multiply(r);
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
        RandomTriangle that = (RandomTriangle) o;
        return Objects.equals(r, that.r) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, name);
    }
}
