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
import ru.iteco.springl.components.RandomSquare;
import ru.iteco.springl.service.ShapeService;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("square")
public class ProfileTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void squareProfile() {
        ShapeService service = applicationContext.getBean(ShapeService.class);

        Assert.assertEquals(RandomSquare.class, service.getShape().getClass());
    }
}
