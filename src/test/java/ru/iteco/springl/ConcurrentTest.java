package ru.iteco.springl;

import net.bytebuddy.utility.RandomString;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.iteco.springl.concurrent.ConcurrentMapImpl;
import ru.iteco.springl.model.Employee;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(Parameterized.class)
public class ConcurrentTest {

    private Map<Object, Object> objectMap;
    private int anInt = 100000;

    public ConcurrentTest(Map<Object, Object> objectMap) {
        this.objectMap = objectMap;
    }

    @Parameterized.Parameters
    public static Iterable<? extends Object> data() {
        return Arrays.asList(new ConcurrentMapImpl<>(), new HashMap<>(), new ConcurrentHashMap<>());
    }

    @After
    public void clean() {
        objectMap.clear();
    }

    @Test
    public void normalMapPut() {
        objectMap.put("f", new Employee());
        objectMap.put("fs", new Employee());

        Assert.assertEquals(2, objectMap.size());
        Assert.assertNotNull(objectMap.get("f"));
    }


    @Test
    public void concurrentWrite() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            pool.submit(() -> {
                for (int j = 0; j < anInt; j++) {
                    objectMap.put(j, "wow");
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        Assert.assertEquals(anInt, objectMap.size());
    }

    @Test
    public void remove() {
        objectMap.put("1", "a");
        objectMap.put("2", "b");
        objectMap.put("3", "c");

        Assert.assertEquals("b", objectMap.remove("2"));

        Assert.assertEquals("a", objectMap.get("1"));
        Assert.assertEquals("c", objectMap.get("3"));

        Assert.assertNull(objectMap.get("2"));
    }

    @Test
    public void containsKey() {
        objectMap.put("a", "b");

        Assert.assertEquals(1, objectMap.size());
        Assert.assertTrue(objectMap.containsKey("a"));
    }

    @Test
    public void containsValue() {
        objectMap.put("a", "b");

        Assert.assertEquals(1, objectMap.size());
        Assert.assertTrue(objectMap.containsValue("b"));
    }

    @Test
    public void empty() {
        Assert.assertTrue(objectMap.isEmpty());

        objectMap.put("a", "b");

        Assert.assertFalse(objectMap.isEmpty());
        Assert.assertEquals(1, objectMap.size());
    }

    @Test
    public void putIfAbsent() {
        objectMap.put("a", "b");
        objectMap.putIfAbsent("a", "x");

        objectMap.putIfAbsent("b", "a");

        Assert.assertEquals("b", objectMap.get("a"));
        Assert.assertEquals("a", objectMap.get("b"));
    }

    @Test
    public void testCont() throws InterruptedException {
        Map<String, String> concurMap = new ConcurrentHashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            pool.submit(() -> {
                for (int j = 0; j < anInt; j++) {
                    String key = RandomString.make();
                    String value = RandomString.make();
                    objectMap.put(key, value);
                    concurMap.put(key, value);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        Set<Map.Entry<String, String>> concurEntries = concurMap.entrySet();
        Set<Map.Entry<Object, Object>> entries = objectMap.entrySet();

        Assert.assertTrue(concurEntries.containsAll(entries));
    }
}
