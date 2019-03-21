package ru.iteco.springl;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.springl.components.RandomPolygon;
import ru.iteco.springl.components.Shape;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ConditionalTest {

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeClass
    public static void setUp() {
        System.setProperty("project.polygonal", "true");
    }

    @Test
    public void conditionalTest() {
        Assert.assertEquals(RandomPolygon.class, applicationContext.getBean(Shape.class).getClass());
    }

    @AfterClass
    public static void tearDown() {
        System.setProperty("project.polygonal", "false");
    }
}
