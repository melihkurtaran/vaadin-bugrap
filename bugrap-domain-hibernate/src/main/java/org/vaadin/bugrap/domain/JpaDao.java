package org.vaadin.bugrap.domain;

import org.vaadin.bugrap.domain.entities.BugrapEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A set of low-level JPA DAO functions. Please use {@link BugrapRepository} for
 * a higher-level API.
 */
public final class JpaDao {

    protected final EntityManagerFactory emf;
    protected final ThreadLocal<EntityManager> em = new ThreadLocal<>();

    public JpaDao(String name) {
        emf = Persistence.createEntityManagerFactory(name, null);
    }

    public <T extends BugrapEntity> T find(Class<T> clazz, Long id) {
        return dbr(em -> em.find(clazz, id));
    }

    @SuppressWarnings("unchecked")
    public <T extends BugrapEntity> List<T> list(Class<T> clazz) {
        return dbr(em -> em.createQuery("SELECT a FROM " + clazz.getSimpleName() + " a").getResultList());
    }

    @SuppressWarnings("unchecked")
    public <T extends BugrapEntity> List<T> list(String queryStr,
            Map<String, Object> parameters) {
        return dbr(em -> {
            Query query = em.createQuery(queryStr);
            if (parameters != null) {
                for (String key : parameters.keySet()) {
                    query.setParameter(key, parameters.get(key));
                }
            }
            return query.getResultList();
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends BugrapEntity> List<T> list(String queryStr,
            Map<String, Object> parameters, int max) {
        return dbr(em -> {
            Query query = em.createQuery(queryStr);
            query.setMaxResults(max);
            if (parameters != null) {
                for (String key : parameters.keySet()) {
                    query.setParameter(key, parameters.get(key));
                }
            }
            return query.getResultList();
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends BugrapEntity> T find(String queryStr,
            Map<String, Object> parameters) {
        return dbr(em -> {
            try {
                Query query = em.createQuery(queryStr);
                if (parameters != null) {
                    for (String key : parameters.keySet()) {
                        query.setParameter(key, parameters.get(key));
                    }
                }

                return (T) query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }

    /**
     * Saves any entity to the database
     *
     * @param entity
     *            The entity to save
     */
    public <T extends BugrapEntity> T store(T entity) {
        return dbr(em -> {
            if (entity.getId() != -1) {
                return em.merge(entity);
            } else {
                em.persist(entity);
                return entity;
            }
        });
    }

    public <T extends BugrapEntity> void storeAll(Collection<T> pojos) {
        db(em -> {
            for (BugrapEntity pojo : pojos) {
                if (pojo.getId() != -1) {
                    em.merge(pojo);
                } else {
                    em.persist(pojo);
                }
            }
        });
    }

    /**
     * Delete an entity from the database
     *
     * @param entity
     *            The entity to delete
     */
    public void delete(BugrapEntity entity) {
        // If it isn't stored, it can't be removed
        if (entity.getId() == -1) {
            return;
        }

        db(em -> {
            Object e = em.find(entity.getClass(), entity.getId());
            em.remove(e);
        });
    }

    public <T extends BugrapEntity> void deleteAll(Collection<T> pojos) {
        Objects.requireNonNull(pojos);
        db(em -> {
            for (T pojo : pojos) {
                // If it isn't stored, it can't be removed
                if (pojo.getId() == -1) {
                    continue;
                }
                Object entity = em.find(pojo.getClass(), pojo.getId());
                if (entity != null) {
                    em.remove(entity);
                }
            }
        });
    }

    public <T extends BugrapEntity> void deleteAll(Class<T> entityClass) {
        Objects.requireNonNull(entityClass);
        db(em -> em.createQuery("delete from " + entityClass.getSimpleName()).executeUpdate());
    }

    /**
     * Runs given block in a transaction. Commits the transaction afterwards,
     * or rolls the transaction back if the block failed with an exception.
     * <p></p>
     * Calling the function from within already running
     * transaction will not start a nested transaction - it will simply run the block
     * directly.
     * @param block the block to run, not null.
     */
    public void db(Consumer<EntityManager> block) {
        EntityManager em = this.em.get();
        final boolean inTransaction = em != null;
        if (inTransaction) {
            block.accept(em);
            return;
        }

        em = emf.createEntityManager();
        try {
            this.em.set(em);
            try {
                boolean success = false;
                try {
                    em.getTransaction().begin();
                    block.accept(em);
                    success = true;
                } finally {
                    if (success) {
                        em.getTransaction().commit();
                    } else {
                        em.getTransaction().rollback();
                    }
                }
            } finally {
                this.em.set(null);
            }
        } finally {
            em.close();
        }
    }

    /**
     * {@link #db(Consumer) db} with result.
     * @param block takes in the entity manager and calculates the value.
     * @param <R> the returned value type.
     * @return the returned value, may be null.
     */
    public <R> R dbr(Function<EntityManager, R> block) {
        final AtomicReference<R> result = new AtomicReference<>();
        db(em -> result.set(block.apply(em)));
        return result.get();
    }

    public long count(String queryStr, Map<String, Object> parameters) {
        return dbr(em -> {
            try {
                Query query = em.createQuery(queryStr);
                if (parameters != null) {
                    for (String key : parameters.keySet()) {
                        query.setParameter(key, parameters.get(key));
                    }
                }
                return (Long) query.getSingleResult();
            } catch (NoResultException e) {
                return 0L;
            }
        });
    }

    private static final JpaDao JPA_DAO = new JpaDao("default");
    public static JpaDao getInstance() {
        return JPA_DAO;
    }
}
