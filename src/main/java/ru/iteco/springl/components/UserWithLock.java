package ru.iteco.springl.components;

import org.hibernate.Session;
import ru.iteco.springl.model.User;

import javax.persistence.LockModeType;
import java.security.SecureRandom;
import java.util.concurrent.Callable;

public class UserWithLock implements Callable<String> {

    Long id;
    LockModeType lockModeType;
    long ms;
    Session session;


    public UserWithLock(Long id, LockModeType lockModeType, long ms, Session session) {
        this.id = id;
        this.lockModeType = lockModeType;
        this.ms = ms;
        this.session = session;
    }

    @Override
    public String call() throws Exception {
        String lastName = generate(10);
        session.beginTransaction();

        User user = session.find(User.class, id, lockModeType);

        user.setLastName(lastName);
        session.saveOrUpdate(user);

        Thread.sleep(ms);
        session.getTransaction().commit();

        session.close();


        return lastName;
    }

    private SecureRandom secureRandom = new SecureRandom();

    public String generate(int length) {
        StringBuilder sb = new StringBuilder();
        secureRandom.ints(97, 123).limit(length).forEach(i -> sb.append((char) i));

        return sb.toString();
    }
}
