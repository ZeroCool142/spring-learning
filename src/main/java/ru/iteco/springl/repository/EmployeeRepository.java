package ru.iteco.springl.repository;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.springl.model.Employee;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Repository
public class EmployeeRepository {

    private static final String QUERY = "select e from Employee e WHERE e.name = :name";

    @Autowired
    private EntityManager entityManager;


    @Transactional
    @Cacheable("queries")
    public Employee findByName(String name) {
        slowQuery(5000L);
        TypedQuery<Employee> query = entityManager.unwrap(Session.class)
                .createQuery(QUERY, Employee.class)
//                .setCacheable(true)
//                .setCacheRegion("queries")
                .setParameter("name", name);
        return query.getSingleResult();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Employee save(Employee employee) {
        entityManager.persist(employee);

        return employee;
    }

    private void slowQuery(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
