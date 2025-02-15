package my.project.orm;

import my.project.orm.annotation.Table;
import my.project.orm.entity.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultQueryGeneratorTest {

    private final DefaultQueryGenerator queryGenerator = new DefaultQueryGenerator();

    @Test
    public void testFindAllGeneratesCorrectSelectQuery() {
        String query = queryGenerator.findAll(Person.class);
        assertEquals("SELECT id, name, salary FROM Person;", query);
    }

    @Test
    public void testFindByIdGeneratesCorrectSelectQueryWithIdCondition() {
        String query = queryGenerator.findById(Person.class, 1);
        assertEquals("SELECT id, name, salary FROM Person where id = 1;", query);
    }

    @Test
    public void testDeleteByIdGeneratesCorrectDeleteQueryWithIdCondition() {
        String query = queryGenerator.deleteById(Person.class, 1);
        assertEquals("DELETE FROM Person where id = 1;", query);
    }

    @Test
    public void testInsertGeneratesCorrectInsertQueryWithValues() throws IllegalAccessException {
        Person person = new Person(1, "Dima", 200.4);
        String query = queryGenerator.insert(person);
        assertEquals("INSERT INTO Person (id, name, salary) VALUES ('1', 'Dima', '200.4');", query);
    }

    @Test
    public void testUpdateGeneratesCorrectUpdateQueryWithValuesAndIdCondition() throws IllegalAccessException {
        Person person = new Person(1, "Dima", 200.4);
        String query = queryGenerator.update(person);
        assertEquals("UPDATE Person SET id = '1', name = 'Dima', salary = '200.4' WHERE id = 1;", query);
    }

    @Test
    public void testFindAllThrowsExceptionWhenClassIsNotAnnotatedWithTable() {
        class NotAnnotatedClass {}
        assertThrows(IllegalArgumentException.class, () -> queryGenerator.findAll(NotAnnotatedClass.class));
    }

    @Test
    public void testFindAllThrowsExceptionWhenClassHasNoColumnsAnnotatedWithColumn() {
        @Table(name = "EmptyClass")
        class EmptyClass {}
        assertThrows(IllegalArgumentException.class, () -> queryGenerator.findAll(EmptyClass.class));
    }
}
