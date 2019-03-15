package ru.iteco.springl;

import net.bytebuddy.utility.RandomString;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.hibernate.*;
import org.hibernate.cache.spi.Region;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.NativeQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.springl.components.UserWithLock;
import ru.iteco.springl.model.*;
import ru.iteco.springl.repository.EmployeeRepository;
import ru.iteco.springl.util.LoadListener;
import ru.iteco.springl.util.RollbackInterceptor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("circle")
public class HibernateInsertTest {

    public static final String SELECT_TABLE_COLUMNS = "SELECT column_name FROM information_schema.columns WHERE table_schema = 'learnspring' AND table_name = :tableName";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void entityLifeCycleHiber() {
        Session session = getSession();
        session.beginTransaction();

        User user = new User().setFirstName("User").setLastName("Userovskiy");
        Assert.assertEquals("NEW/TRANSIENT", 0, getManagedEntities(session).size());

        Serializable id = session.save(user);
        Assert.assertEquals("MANAGED/PERSISTED", 1, getManagedEntities(session).size());

        session.getTransaction().commit();

        session.evict(user);
        Assert.assertEquals("DETACHED", 0, getManagedEntities(session).size());

        session.close();

        Assert.assertEquals(id, user.getId());
    }

    @Test
    public void entityLifeCycleJpa() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = new User().setFirstName("UserJpa").setLastName("Userovskiy");
        Assert.assertFalse("NEW/TRANSIENT", entityManager.contains(user));

        entityManager.persist(user);
        Assert.assertTrue("MANAGED/PERSISTED", entityManager.contains(user));

        entityManager.getTransaction().commit();

        entityManager.detach(user);
        Assert.assertFalse("DETACHED", entityManager.contains(user));

        entityManager.close();
    }

    @Test
    public void reattachEntity() {
        Session session = getSession();
        session.beginTransaction();

        Assert.assertEquals(0, getManagedEntities(session).size());

        //MANAGED
        User user = session.find(User.class, 1L);
        Assert.assertEquals(1, getManagedEntities(session).size());
        Assert.assertNotNull(user.getId());
        Assert.assertNotNull(user.getFirstName());
        Assert.assertNotNull(user.getLastName());

        //DETACHED
        session.evict(user);
        Assert.assertEquals(0, getManagedEntities(session).size());

        //REATTACH
        session.lock(user, LockMode.NONE);
        Assert.assertEquals(1, getManagedEntities(session).size());

        //DETACHED
        session.evict(user);
        Assert.assertEquals(0, getManagedEntities(session).size());

        //REATTACH
        session.saveOrUpdate(user);
        Assert.assertEquals(1, getManagedEntities(session).size());

        //DETACHED
        session.evict(user);
        Assert.assertEquals(0, getManagedEntities(session).size());

        //REATTACH
        Object merged = session.merge(user);
        Assert.assertEquals(user, merged);
        Assert.assertEquals(1, getManagedEntities(session).size());

        //DETACHED
        session.evict(merged);
        Assert.assertEquals(0, getManagedEntities(session).size());

        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void optimisticLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        RollbackInterceptor firstSessionInterceptor = new RollbackInterceptor();
        RollbackInterceptor secondSessionInterceptor = new RollbackInterceptor();

        Future<String> user = executorService.submit(new UserWithLock(1L, LockModeType.OPTIMISTIC, 5000, getSession(firstSessionInterceptor)));
        Thread.sleep(1000);
        Future<String> user2 = executorService.submit(new UserWithLock(1L, LockModeType.OPTIMISTIC, 20, getSession(secondSessionInterceptor)));

        try {
            user.get();
            user2.get();
        } catch (Exception e) {
            //javax.persistence.RollbackException: Error while committing the transaction
        }

        Assert.assertFalse(firstSessionInterceptor.wasCommitted());
        Assert.assertTrue(secondSessionInterceptor.wasCommitted());
    }

    @Test
    public void pessimisticLock() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        RollbackInterceptor firstSessionInterceptor = new RollbackInterceptor();
        RollbackInterceptor secondSessionInterceptor = new RollbackInterceptor();

        Future<String> user = executorService.submit(new UserWithLock(1L, LockModeType.PESSIMISTIC_WRITE, 5000, getSession(firstSessionInterceptor)));
        Thread.sleep(1000);
        Future<String> user2 = executorService.submit(new UserWithLock(1L, LockModeType.PESSIMISTIC_WRITE, 20, getSession(secondSessionInterceptor)));

        user.get();
        user2.get();

        Assert.assertTrue(firstSessionInterceptor.wasCommitted());
        Assert.assertTrue(secondSessionInterceptor.wasCommitted());
    }

    @Test
    public void inheritance() {

        Cat cat = new Cat();
        cat.setName("The cat");
        cat.setType("Siam");
        cat.setFurColor("Black");
        cat.setCanSwim(false);

        Dog dog = new Dog();
        dog.setName("The dog");
        dog.setType("Shepperd");
        dog.setFurColor("Brown");

        Session session = getSession();
        session.beginTransaction();

        session.persist(cat);
        session.persist(dog);

        session.getTransaction().commit();

        List<String> catColumns = Arrays.asList("id", "name", "type", "can_swim", "fur_color");
        List<String> dogColumns = Arrays.asList("id", "name", "type", "fur_color");

        checkColumns(catColumns, "cat_union", session);
        checkColumns(dogColumns, "dog_union", session);

        session.close();
    }

    @Test
    public void inheritanceUnion() {

        CatUnion cat = new CatUnion();
        cat.setName("The cat");
        cat.setType("Siam");
        cat.setFurColor("Black");
        cat.setCanSwim(false);

        DogUnion dog = new DogUnion();
        dog.setName("The dog");
        dog.setType("Shepperd");
        dog.setFurColor("Brown");

        Session session = getSession();
        session.beginTransaction();

        session.persist(cat);
        session.persist(dog);

        session.getTransaction().commit();

        session.beginTransaction();
        List<AnimalUnion> animals = session.byMultipleIds(AnimalUnion.class).multiLoad(cat.getId(), dog.getId());
        session.getTransaction().commit();

        List<String> catColumns = Arrays.asList("id", "name", "type", "can_swim", "fur_color");
        List<String> dogColumns = Arrays.asList("id", "name", "type", "fur_color");

        checkColumns(catColumns, "cat_union", session);
        checkColumns(dogColumns, "dog_union", session);

        session.close();

        Assert.assertEquals("The cat", animals.get(0).getName());
        Assert.assertEquals("The dog", animals.get(1).getName());
    }

    @Test
    public void inheritanceSingle() {
        CatSingle cat = new CatSingle();
        cat.setName("The cat");
        cat.setType("Siam");
        cat.setFurColor("Black");
        cat.setCanSwim(false);

        DogSingle dog = new DogSingle();
        dog.setName("The dog");
        dog.setType("Shepperd");
        dog.setFurColor("Brown");

        Session session = getSession();
        session.beginTransaction();

        session.persist(cat);
        session.persist(dog);

        session.getTransaction().commit();

        session.beginTransaction();
        List<AnimalSingle> animals = session.byMultipleIds(AnimalSingle.class).multiLoad(cat.getId(), dog.getId());
        session.getTransaction().commit();

        List<String> allColumns = Arrays.asList("id", "name", "type", "animal_type", "can_swim", "fur_color");

        checkColumns(allColumns, "animal_single", session);

        session.close();

        Assert.assertEquals("The cat", animals.get(0).getName());
        Assert.assertEquals("The dog", animals.get(1).getName());
    }

    @Test
    public void inheritanceJoin() {
        CatJoin cat = new CatJoin();
        cat.setName("The cat");
        cat.setType("Siam");
        cat.setFurColor("Black");
        cat.setCanSwim(false);

        DogJoin dog = new DogJoin();
        dog.setName("The dog");
        dog.setType("Shepperd");
        dog.setFurColor("Brown");

        Session session = getSession();
        session.beginTransaction();

        session.persist(cat);
        session.persist(dog);

        session.getTransaction().commit();

        session.beginTransaction();
        List<AnimalJoin> animals = session.byMultipleIds(AnimalJoin.class).multiLoad(cat.getId(), dog.getId());
        session.getTransaction().commit();

        List<String> animalColumns = Arrays.asList("id", "name", "type");
        List<String> catColumns = Arrays.asList("id", "can_swim", "fur_color");
        List<String> dogColumns = Arrays.asList("id", "fur_color");

        checkColumns(animalColumns, "animal_join", session);
        checkColumns(catColumns, "cat_join", session);
        checkColumns(dogColumns, "dog_join", session);

        session.close();

        Assert.assertEquals("The cat", animals.get(0).getName());
        Assert.assertEquals("The dog", animals.get(1).getName());
    }

    @Test
    public void manyToMany() {
        Session session = getSession();
        session.beginTransaction();

        Lesson englishLesson = new Lesson().setName("English");
        Lesson mathLesson = new Lesson().setName("Math");
        Lesson programmingLesson = new Lesson().setName("Programming");

        Student abraham = new Student().setFirstName("Abraham").setLastName("Lincoln");
        Student luis = new Student().setFirstName("Luis").setLastName("Ochoa");
        Student alyssia = new Student().setFirstName("Alyssia").setLastName("Bradford");

        englishLesson.addStudent(abraham).addStudent(luis).addStudent(alyssia);
        mathLesson.addStudent(abraham).addStudent(luis).addStudent(alyssia);
        programmingLesson.addStudent(luis).addStudent(alyssia);

        abraham.addLesson(englishLesson).addLesson(mathLesson);
        luis.addLesson(englishLesson).addLesson(mathLesson).addLesson(programmingLesson);
        alyssia.addLesson(englishLesson).addLesson(mathLesson).addLesson(programmingLesson);

        session.persist(englishLesson);
        session.persist(mathLesson);
        session.persist(programmingLesson);

        List<String> lessonColumns = Arrays.asList("id", "name");
        List<String> studentColumns = Arrays.asList("id", "first_name", "last_name");
        List<String> relationsColumns = Arrays.asList("students_id", "lessons_id");

        session.getTransaction().commit();

        checkColumns(lessonColumns, "lesson", session);
        checkColumns(studentColumns, "student", session);
        checkColumns(relationsColumns, "lesson_student", session);
        session.close();

        session = getSession();
        session.beginTransaction();

        Lesson lesson = session.find(Lesson.class, programmingLesson.getId());

        Assert.assertTrue(lesson.getStudents().containsAll(programmingLesson.getStudents()));

        session.getTransaction().commit();
        session.close();

    }

    @Test
    public void cache() throws InterruptedException {
        Employee savedEmployee = employeeRepository.save(new Employee().setName(RandomString.make(10)));
        Cache test = cacheManager.getCache("queries");
        Assert.assertEquals(0, test.getKeys().size());


        Employee employee = employeeRepository.findByName(savedEmployee.getName());

        Map<String, Region> regions = getRegions(entityManagerFactory.unwrap(SessionFactory.class).getCache());

//        Assert.assertEquals(1, test.getKeys().size());
//        Assert.assertEquals(test.get(savedEmployee.getName()).getObjectValue(), employee);
//        Assert.assertEquals(1, LoadListener.getCounter());

        Thread.sleep(1000);

        Employee cachedEmployee = employeeRepository.findByName(savedEmployee.getName());
        Assert.assertEquals(1, test.getKeys().size());
        Assert.assertEquals(employee, cachedEmployee);
        Assert.assertEquals(1, LoadListener.getCounter());

        Thread.sleep(5000);
        Assert.assertNull(test.get(savedEmployee.getName()));
    }

    private List<Map.Entry<Object, EntityEntry>> getManagedEntities(Session session) {
        PersistenceContext persistenceContext = ((SessionImplementor) session).getPersistenceContext();
        return Arrays.asList(persistenceContext.reentrantSafeEntityEntries());
    }

    private Session getSession() {
        return entityManagerFactory.unwrap(SessionFactory.class).openSession();
    }

    private Session getSession(Interceptor interceptor) {
        return entityManagerFactory.unwrap(SessionFactory.class).withOptions().interceptor(interceptor).openSession();
    }

    private void checkColumns(List<String> columns, String tableName, Session session) {
        NativeQuery query = session.createSQLQuery(String.format(SELECT_TABLE_COLUMNS, tableName)).setParameter("tableName", tableName);

        for (Object s : query.list()) {
            Assert.assertTrue(columns.contains(s));
        }
    }

    private Map<String, Region> getRegions(org.hibernate.Cache cache) {
        try {
            Field regionsByName = cache.getClass().getDeclaredField("regionsByName");
            regionsByName.setAccessible(true);
            return ( Map<String, Region>) regionsByName.get(cache);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Gg");
    }
}
