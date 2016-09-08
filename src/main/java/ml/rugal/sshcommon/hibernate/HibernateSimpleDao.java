package ml.rugal.sshcommon.hibernate;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import ml.rugal.sshcommon.page.Pagination;
import ml.rugal.sshcommon.util.BeanUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

/**
 *
 * This is abstract hibernate DAO, including basic operation such like find object by its primary
 * key; search for list of matched records by properties;
 *
 * @author Rugal Bernstein
 * @since 0.1
 */
public abstract class HibernateSimpleDao
{

    protected SessionFactory sessionFactory;

    /**
     * Hibernate ordering entry.
     */
    protected static final String ORDER_ENTRIES = "orderEntries";

    /**
     * Generate HQL with properties set.
     *
     * @param hql    The template HQL
     * @param values the properties values to be set into the template HQL
     * <p>
     * @return a list of matched record with original object type.
     */
    protected List find(String hql, Object... values)
    {
        return createQuery(hql, values).list();
    }

    /**
     * Find unique or the first record return from database.
     *
     * @param hql    The template HQL
     * @param values the properties values to be set into the template HQL
     * <p>
     * @return Object matched record with original type.
     */
    protected Object findUnique(String hql, Object... values)
    {
        return createQuery(hql, values).setMaxResults(1).uniqueResult();
    }

    /**
     * Use finder to get a specific page.
     *
     * @param finder   Finder to query
     * @param pageNo   the page number to get, start from 1.
     * @param pageSize the records or objects size which each page could have
     * <p>
     * @return A page which contain criteria matched object.
     */
    protected Pagination find(Finder finder, int pageNo, int pageSize)
    {
        int totalCount = countQueryResult(finder);
        Pagination p = new Pagination(pageNo, pageSize, totalCount);
        if (totalCount < 1)
        {
            p.setList(new ArrayList());
            return p;
        }
        Query query = getSession().createQuery(finder.getOrigHql());
        finder.setParamsToQuery(query);
        query.setFirstResult(p.getFirstResult());
        query.setMaxResults(p.getPageSize());
        if (finder.isCacheable())
        {
            query.setCacheable(true);
        }
        List list = query.list();
        p.setList(list);
        return p;
    }

    /**
     * Use finder to get a specific page.
     *
     * @param finder Finder to query
     * <p>
     * @return A page which contain criteria matched object.
     */
    protected List find(Finder finder)
    {
        Query query = finder.createQuery(getSession());
        List list = query.list();
        return list;
    }

    /**
     * create query object.
     *
     * @param queryString query string of HQL
     * @param values      properties for criteria
     * <p>
     * @return A query object.
     */
    protected Query createQuery(String queryString, Object... values)
    {
        Assert.hasText(queryString);
        Query queryObject = getSession().createQuery(queryString);
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                queryObject.setParameter(i, values[i]);
            }
        }
        return queryObject;
    }

    /**
     * Use given criteria to query and get page.
     *
     * @param crit     the given criteria for query.
     * @param pageNo   the page number to get, start from 1.
     * @param pageSize the records or objects size which each page could have
     * <p>
     * @return A page which contain criteria matched object.
     */
    protected Pagination findByCriteria(Criteria crit, int pageNo, int pageSize)
    {
        CriteriaImpl impl = (CriteriaImpl) crit;
        // 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
        Projection projection = impl.getProjection();
        ResultTransformer transformer = impl.getResultTransformer();
        List<CriteriaImpl.OrderEntry> orderEntries;
        try
        {
            orderEntries = (List) BeanUtils.getFieldValue(impl, ORDER_ENTRIES);
            BeanUtils.setFieldValue(impl, ORDER_ENTRIES, new ArrayList());
        }
        catch (Exception e)
        {
            throw new RuntimeException("cannot read/write 'orderEntries' from CriteriaImpl", e);
        }

        //total count of this query, equivalent to select count(id) from table where ...
        int totalCount = ((Number) crit.setProjection(Projections.rowCount()).uniqueResult()).intValue();
        Pagination p = new Pagination(pageNo, pageSize, totalCount);
        if (totalCount < 1)
        {
            p.setList(new ArrayList());
            return p;
        }

        // set projection for column.
        crit.setProjection(projection);
        if (projection == null)
        {
            crit.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        }
        if (transformer != null)
        {
            crit.setResultTransformer(transformer);
        }
        try
        {
            BeanUtils.setFieldValue(impl, ORDER_ENTRIES, orderEntries);
        }
        catch (Exception e)
        {
            throw new RuntimeException("set 'orderEntries' to CriteriaImpl faild", e);
        }
        crit.setFirstResult(p.getFirstResult());
        crit.setMaxResults(p.getPageSize());
        p.setList(crit.list());
        return p;
    }

    /**
     * Counting row number.
     *
     * @param finder target object that contain HQL content to count.
     * <p>
     * @return row number in a result set.
     */
    protected int countQueryResult(Finder finder)
    {
        Query query = getSession().createQuery(finder.getRowCountHql());
        finder.setParamsToQuery(query);
        if (finder.isCacheable())
        {
            query.setCacheable(true);
        }
        return ((Number) query.iterate().next()).intValue();
    }

    /**
     * Inject a session factory from spring application context.
     *
     * @param sessionFactory The bean to be set.
     */
    @Resource
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Use {@code getCurrentSession()} from hibernate to get current hibernate session, thus there
     * must be some opened session in container.
     *
     * @return Get current hibernate session.
     */
    protected Session getSession()
    {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }
}
