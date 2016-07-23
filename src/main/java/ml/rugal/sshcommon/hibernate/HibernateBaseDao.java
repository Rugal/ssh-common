package ml.rugal.sshcommon.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import ml.rugal.sshcommon.page.Pagination;
import ml.rugal.sshcommon.util.BeanUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 *
 * An abstract hibernate DAO class that implement the HibernateSimpleDao, implemented its get and
 * find method. <BR>
 * In addition to this, this class provide abstract update method.
 *
 * @author Rugal Bernstein
 * @param <T>  This is the entity class for finder reflection
 * @param <ID> This is the ID type of entity class.
 * <p>
 * @since 0.1
 */
@Transactional
public abstract class HibernateBaseDao<T, ID extends Serializable> extends HibernateSimpleDao
{

    /**
     * @see org.hibernate.Session#get(Class,Serializable)
     * @param id primary key to criteria for.
     * <p>
     * @return get reflected object.
     */
    @Transactional(readOnly = true)
    protected T get(ID id)
    {
        return get(id, false);
    }

    /**
     * @see org.hibernate.Session#get(Class,Serializable,LockMode)
     * @param id   primary key to criteria for.
     * @param lock set lock mode in query
     * <p>
     * @return get reflected object.
     */
    @Transactional(readOnly = true)
    protected T get(ID id, boolean lock)
    {
        T entity;
        entity = (lock) ? (T) getSession().get(getEntityClass(), id, LockMode.UPGRADE) : (T) getSession()
            .get(getEntityClass(), id);
        return entity;
    }

    /**
     * Query for list of matched object by given properties.
     *
     * @param property name of property
     * @param value    match for value
     * <p>
     * @return A list of record that their property match value.
     */
    @Transactional(readOnly = true)
    protected List<T> findByProperty(String property, Object value)
    {
        Assert.hasText(property);
        return createCriteria(Restrictions.eq(property, value)).list();
    }

    /**
     * Query for list of matched object by given string data. For this is front matching method.
     * <BR>
     * Pattern: (value%)
     *
     * @param property name of property
     * @param value    match for value
     * <p>
     * @return A list of record that their property match value.
     */
    @Transactional(readOnly = true)
    protected List<T> startsWith(String property, String value)
    {
        Assert.hasText(property);
        return createCriteria(Restrictions.like(property, value + "%")).list();
    }

    /**
     * Check if given property contains given value.<BR>
     * Pattern: (%value%)
     *
     * @param property name of property
     * @param value    match for value
     * <p>
     * @return A list of record that their property match value.
     */
    @Transactional(readOnly = true)
    protected List<T> contains(String property, String value)
    {
        Assert.hasText(property);
        return createCriteria(Restrictions.like(property, "%" + value + "%")).list();
    }

    /**
     * Query for list of matched object by given string data. For this is backend matching
     * method.<BR>
     * Pattern: (%value)
     *
     * @param property name of property
     * @param value    match for value
     * <p>
     * @return A list of record that their property match value.
     */
    @Transactional(readOnly = true)
    protected List<T> endsWith(String property, String value)
    {
        Assert.hasText(property);
        return createCriteria(Restrictions.like(property, "%" + value)).list();
    }

    /**
     * Query for unique object of matched by given criteria.
     *
     * @param property name of property
     * @param value    match for value
     * <p>
     * @return An record that its property match value.
     */
    @Transactional(readOnly = true)
    protected T findUniqueByProperty(String property, Object value)
    {
        Assert.hasText(property);
        Assert.notNull(value);
        return (T) createCriteria(Restrictions.eq(property, value)).uniqueResult();
    }

    /**
     * This method count quantity of returned row filtered by property.
     *
     * @param property The given property name
     * @param value    The given property value
     * <p>
     * @return matched record number.
     */
    @Transactional(readOnly = true)
    protected int countByProperty(String property, Object value)
    {
        Assert.hasText(property);
        Assert.notNull(value);
        return ((Number) (createCriteria(
                          Restrictions.eq(property, value)).setProjection(
                          Projections.rowCount()).uniqueResult())).intValue();
    }

    /**
     * Similar to query, but predicate with criteria.
     *
     * @param criterion Given criteria to find.
     * <p>
     * @return A list of record that match the given criterion
     */
    @Transactional(readOnly = true)
    protected List findByCriteria(Criterion... criterion)
    {
        return createCriteria(criterion).list();
    }

    /**
     * To update entity table by bean updater.
     *
     * @param updater The updater.
     * <p>
     * @return The updated record
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public T updateByUpdater(Updater<T> updater)
    {
        ClassMetadata cm = sessionFactory.getClassMetadata(
            getEntityClass());
        T bean = updater.getBean();
        T po = (T) getSession().get(getEntityClass(), cm.getIdentifier(bean));
        updaterCopyToPersistentObject(updater, po, cm);
        return po;
    }

    /**
     * Update and copy field information into persistent object of database.
     *
     * @param updater
     * @param po
     */
    private void updaterCopyToPersistentObject(Updater<T> updater, T po, ClassMetadata cm)
    {
        String[] propNames = cm.getPropertyNames();
        String identifierName = cm.getIdentifierPropertyName();
        T bean = updater.getBean();
        Object value;
        for (String propName : propNames)
        {
            if (propName.equals(identifierName))
            {
                continue;
            }
            try
            {
                value = BeanUtils.getSimpleProperty(bean,
                                                    propName);
                if (!updater.isUpdate(propName, value))
                {
                    continue;
                }
                cm.setPropertyValue(po, propName, value);
            }
            catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | HibernateException e)
            {
                throw new RuntimeException(
                    "copy property to persistent object failed: '"
                    + propName + "'", e);
            }
        }
    }

    /**
     * Create a criteria to filter.
     *
     * @param criterions <p>
     * @return AN criteria object.
     */
    protected Criteria createCriteria(Criterion... criterions)
    {
        Criteria criteria = getSession().createCriteria(getEntityClass());
        for (Criterion c : criterions)
        {
            criteria.add(c);
        }
        return criteria;
    }

    /**
     * This method could used for reflection in later funtionality.
     *
     * @return return current entity class.
     */
    abstract protected Class<T> getEntityClass();

    /**
     * Get a page of objects.
     *
     * @param pageNo
     * @param pageSize
     *
     * @return
     */
    @Transactional(readOnly = true)
    public Pagination getPage(int pageNo, int pageSize)
    {
        return findByCriteria(this.createCriteria(), pageNo, pageSize);
    }

    /**
     * Default Save behavior. <BR>
     * The returned bean will probably has ID. <BR>
     * The way ID generates depends on Database and Bean setting.
     *
     * @param bean
     *
     * @return Bean with ID probably filled.
     */
    public T save(T bean)
    {
        getSession().save(bean);
        return bean;
    }

    /**
     * Delete object by ID.
     *
     * @param id
     *
     * @return
     */
    public T deleteByPK(ID id)
    {
        T entity = this.get(id);
        return this.delete(entity);
    }

    /**
     * Delete object.
     *
     * @param bean
     *
     * @return
     */
    public T delete(T bean)
    {
        Assert.notNull(bean);
        getSession().delete(bean);
        return bean;
    }
}
