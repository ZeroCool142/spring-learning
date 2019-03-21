package ru.iteco.springl.components;

import net.bytebuddy.utility.RandomString;
import org.hibernate.Session;
import ru.iteco.springl.model.User;

import javax.persistence.LockModeType;
import java.security.SecureRandom;
import java.util.concurrent.Callable;

public class UserWithLock implements Callable<String> {

    private Long id;
    private LockModeType lockModeType;
    private long ms;
    private Session session;


    public UserWithLock(Long id, LockModeType lockModeType, long ms, Session session) {
        this.id = id;
        this.lockModeType = lockModeType;
        this.ms = ms;
        this.session = session;
    }

    @Override
    public String call() throws Exception {
        String lastName = RandomString.make();
        session.beginTransaction();

        User user = session.find(User.class, id, lockModeType);

        user.setLastName(lastName);
        session.saveOrUpdate(user);

        Thread.sleep(ms);
        session.getTransaction().commit();

        session.close();

        return lastName;
    }
}
