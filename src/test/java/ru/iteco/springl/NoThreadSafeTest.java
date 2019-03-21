package ru.iteco.springl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.springl.service.NoThreadSafeService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("circle")
public class NoThreadSafeTest {

    private static final int INCREMENT_COUNT = 10000;

    @Autowired
    private NoThreadSafeService noThreadSafeService;

    @Test(expected = TimeoutException.class)
    public void deadLock() throws Exception {
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(noThreadSafeService::method1);
        CompletableFuture<String> stringCompletableFuture2 = CompletableFuture.supplyAsync(noThreadSafeService::method2);

        stringCompletableFuture.get(5000, TimeUnit.MILLISECONDS);
        stringCompletableFuture2.get(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void noSafeIncrement() throws ExecutionException, InterruptedException {
        Runnable r = () -> {
            for (int i = 0; i < INCREMENT_COUNT; ++i) {
                noThreadSafeService.increment();
            }
        };

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(r);
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(r);
        voidCompletableFuture.get();
        voidCompletableFuture1.get();

        Assert.assertNotEquals(INCREMENT_COUNT * 2, noThreadSafeService.getIncrement());
    }
}
