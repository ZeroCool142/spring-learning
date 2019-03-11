package ru.iteco.springl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.springl.components.Shape;
import ru.iteco.springl.service.ShapeService;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("circle")
public class ScopeTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void prototypeTest() {
        Shape shape1 = applicationContext.getBean(Shape.class);
        Shape shape2 = applicationContext.getBean(Shape.class);
        Shape shape3 = applicationContext.getBean(Shape.class);

        Assert.assertNotSame(shape1, shape2);
        Assert.assertNotSame(shape1, shape3);
        Assert.assertNotSame(shape2, shape3);
    }

    @Test
    public void singletonTest() {
        ShapeService shapeService1 = applicationContext.getBean(ShapeService.class);
        ShapeService shapeService2 = applicationContext.getBean(ShapeService.class);
        ShapeService shapeService3 = applicationContext.getBean(ShapeService.class);

        Assert.assertSame(shapeService1.getShape(), shapeService2.getShape());
        Assert.assertSame(shapeService1.getShape(), shapeService3.getShape());
        Assert.assertSame(shapeService2.getShape(), shapeService3.getShape());
    }

    @Test
    public void singletonRandomShape() {
        ShapeService shapeService = applicationContext.getBean(ShapeService.class);
        Shape randomShape1 = shapeService.getRandomShape();
        Shape randomShape2 = shapeService.getRandomShape();
        Shape randomShape3 = shapeService.getRandomShape();

        Assert.assertNotSame(randomShape1, randomShape2);
        Assert.assertNotSame(randomShape1, randomShape3);
        Assert.assertNotSame(randomShape2, randomShape3);
    }
}
