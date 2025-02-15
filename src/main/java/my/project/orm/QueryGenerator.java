package my.project.orm;

import java.io.Serializable;

public interface QueryGenerator {

    String findAll(Class<?> type);
    String findById(Class<?> type, Serializable id);
    String deleteById(Class<?> type, Serializable id);
    String insert(Object value) throws NoSuchFieldException, IllegalAccessException;
    String update(Object value) throws IllegalAccessException;
}
