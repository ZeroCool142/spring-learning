package ru.iteco.springl;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.springl.components.RandomCircle;
import ru.iteco.springl.service.ShapeService;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("circle")
public class PrimaryTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void primaryTest() {
        ShapeService service = applicationContext.getBean(ShapeService.class);

        Assert.assertEquals(RandomCircle.class, service.getShape().getClass());
    }
}
