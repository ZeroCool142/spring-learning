package ru.iteco.springl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.springl.components.CircularA;
import ru.iteco.springl.components.CircularB;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("circle")
public class CircularTest {

    @Autowired
    private CircularA circularA;
    @Autowired
    private CircularB circularB;

    @Test
    public void circularDependency() {
        Assert.assertNotNull(circularA);
        Assert.assertNotNull(circularA.getCircularB());

        Assert.assertNotNull(circularB);
        Assert.assertNotNull(circularB.getCircularA());
    }
}
