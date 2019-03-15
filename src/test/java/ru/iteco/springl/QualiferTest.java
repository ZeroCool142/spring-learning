package ru.iteco.springl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.springl.components.PropQualifier;
import ru.iteco.springl.components.RandomCircle;
import ru.iteco.springl.components.RandomTriangle;
import ru.iteco.springl.components.Shape;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("circle")
public class QualiferTest {

    private Shape randomTriangle;
    private Shape randomCircle;

    @Autowired
    @Qualifier("randomCircle")
    public void setSetter(Shape randomCircle) {
        this.randomCircle = randomCircle;
    }

    @Autowired
    @ShapeQualifier(ShapeType.TRIANGLE)
    @PropQualifier("test")
    public void setTriangle(Shape triangle) {
        this.randomTriangle = triangle;
    }

    @Test
    public void qualifier() {
        Assert.assertEquals(RandomTriangle.class, randomTriangle.getClass());
    }

    @Test
    public void byNameCircle() {
        Assert.assertEquals(RandomCircle.class, randomCircle.getClass());
    }
}
