package my.project.orm;

import my.project.orm.annotation.Column;
import my.project.orm.annotation.Table;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.StringJoiner;

public class DefaultQueryGenerator implements QueryGenerator {

    @Override
    public String findAll(Class<?> type) {
        String tableName = getTableName(type);

        ArrayList<String> listColumns = tableColumns(type);
        String columns = toString(listColumns);

        return String.format("SELECT %s FROM %s;", columns, tableName);
    }

    @Override
    public String findById(Class<?> type, Serializable id) {
        String tableName = getTableName(type);

        ArrayList<String> listColumns = tableColumns(type);
        String columns = toString(listColumns);

        return String.format("SELECT %s FROM %s where id = %s;", columns, tableName, id);
    }

    @Override
    public String deleteById(Class<?> type, Serializable id) {
        String tableName = getTableName(type);

        return String.format("DELETE FROM %s where id = %s;", tableName, id);
    }

    @Override
    public String insert(Object value) throws IllegalAccessException {
        String tableName = getTableName(value.getClass());
        ArrayList<String> listColumns = tableColumns(value.getClass());
        ArrayList<String> listValueColumns = valueColumns(value);

        StringJoiner valueJoiner = new StringJoiner(", ");
        for (String columnValue : listValueColumns) {
            valueJoiner.add("'" + columnValue + "'");
        }

        return String.format(
                "INSERT INTO %s (%s) VALUES (%s);",
                tableName,
                toString(listColumns),
                valueJoiner.toString()
        );
    }

    @Override
    public String update(Object value) throws IllegalAccessException {
        String tableName = getTableName(value.getClass());
        String setClause = generateSetClause(value);
        String idValue = getIdValue(value);

        return String.format(
                "UPDATE %s SET %s WHERE id = %s;",
                tableName,
                setClause,
                idValue
        );
    }

    private String getTableName(Class<?> type) {
        Table tableAnnotation = type.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new IllegalArgumentException("Class is not ORM entity");
        }

        return tableAnnotation.name().isEmpty() ? type.getSimpleName() : tableAnnotation.name();
    }

    private ArrayList<String> tableColumns(Class<?> clazz) {
        ArrayList<String> columns = new ArrayList<>();
        for (Field declareField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declareField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String column = columnAnnotation.name().isEmpty() ? declareField.getName() : columnAnnotation.name();
                columns.add(column);
            }
        }

        if (columns.isEmpty()) {
            throw new IllegalArgumentException("No one in the class has an annotation \"Class\"");
        }

        return columns;
    }

    private ArrayList<String> valueColumns(Object value) throws IllegalAccessException {
        ArrayList<String> values = new ArrayList<>();
        Class<?> clazz = value.getClass();

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                declaredField.setAccessible(true);
                Object fieldValue = declaredField.get(value);
                values.add(fieldValue != null ? String.valueOf(fieldValue) : "NULL");
            }
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException("There are no annotated fields in class: " + clazz.getName());
        }

        return values;
    }

    private String generateSetClause(Object value) throws IllegalAccessException {
        ArrayList<String> listColumns = tableColumns(value.getClass());
        ArrayList<String> listValueColumns = valueColumns(value);

        StringJoiner setJoiner = new StringJoiner(", ");
        for (int i = 0; i < listColumns.size(); i++) {
            setJoiner.add(listColumns.get(i) + " = '" + listValueColumns.get(i) + "'");
        }

        return setJoiner.toString();
    }

    private String getIdValue(Object value) throws IllegalAccessException {
        try {
            Field idField = value.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return String.valueOf(idField.get(value));
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Entity " + value.getClass().getSimpleName() + " must have an 'id' field");
        }
    }

    private String toString(ArrayList<String> list) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (String value : list) {
            stringJoiner.add(value);
        }

        return stringJoiner.toString();
    }
}
