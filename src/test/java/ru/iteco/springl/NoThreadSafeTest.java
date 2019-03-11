package ru.iteco.springl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.springl.service.NoThreadSafeService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("circle")
public class NoThreadSafeTest {

    @Autowired
    private NoThreadSafeService noThreadSafeService;

    @Test(expected = TimeoutException.class)
    public void deadLock() throws Exception {
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(noThreadSafeService::method1);
        CompletableFuture<String> stringCompletableFuture2 = CompletableFuture.supplyAsync(noThreadSafeService::method2);

        stringCompletableFuture.get(5000, TimeUnit.MILLISECONDS);
        stringCompletableFuture2.get(5000, TimeUnit.MILLISECONDS);
    }
}
