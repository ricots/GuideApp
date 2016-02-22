package com.guideapp.backend.dao.generic;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.guideapp.backend.util.OfyService.ofy;


/**
 * Created by thales on 1/24/16.
 */
public class ObjectifyGenericDAO<T> implements IGenericDAO<T> {
    protected Class<T> clazz;

    @SuppressWarnings("unchecked")
    public ObjectifyGenericDAO() {
        clazz = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @Override
    public Key<T> save(T entity) {
        return ofy().save().entity(entity).now();
    }

    @Override
    public void insert(T entity) {
        save(entity);
    }

    @Override
    public void delete(T entity) {
        ofy().delete().entity(entity).now();
    }

    @Override
    public void delete(Iterable<?> entities) {
        ofy().delete().entities(entities).now();
    }

    @Override
    public void update(T entity) {
        save(entity);
    }

    @Override
    public List<T> listAll() {
        Query<T> query = ofy().load().type(clazz);
        return query.list();
    }

    @Override
    public T getByProperty(String propName, Object propValue) {
        return ofy().load().type(clazz).filter(propName, propValue).first().now();
    }

    @Override
    public T getById(Long id) {
        return ofy().load().type(clazz).id(id).now();
    }

    @Override
    public T getByKey(Long id) {
        return ofy().load().key(Key.create(clazz, id)).now();
    }

    @Override
    public List<T> listByProperty(String propName, Object propValue) {
        return ofy().load().type(clazz).filter(propName, propValue).list();
    }

    @Override
    public List<T> listByProperties(Map<String, Object> filters) {
        Query<T> query = ofy().load().type(clazz);
        Collection<com.google.appengine.api.datastore.Query.Filter> filters1 = new ArrayList<>();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            filters1.add(new com.google.appengine.api.datastore.Query.FilterPredicate(
                    entry.getKey(),
                    com.google.appengine.api.datastore.Query.FilterOperator.EQUAL,
                    entry.getValue()));
        }

        com.google.appengine.api.datastore.Query.Filter filter
                = com.google.appengine.api.datastore.Query.CompositeFilterOperator.and(filters1);

        return query.filter(filter).list();
    }
}