package my.project.orm;

import my.project.orm.annotation.Table;
import my.project.orm.entity.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultQueryGeneratorTest {

    private final DefaultQueryGenerator queryGenerator = new DefaultQueryGenerator();

    @DisplayName("test find all generates correct select query")
    @Test
    public void testFindAllGeneratesCorrectSelectQuery() {
        String query = queryGenerator.findAll(Person.class);
        assertEquals("SELECT id, name, salary FROM Person;", query);
    }

    @DisplayName("test find by id generates correct select query with id condition")
    @Test
    public void testFindByIdGeneratesCorrectSelectQueryWithIdCondition() {
        String query = queryGenerator.findById(Person.class, 1);
        assertEquals("SELECT id, name, salary FROM Person where id = 1;", query);
    }

    @DisplayName("test delete by id generates correct delete query with id condition")
    @Test
    public void testDeleteByIdGeneratesCorrectDeleteQueryWithIdCondition() {
        String query = queryGenerator.deleteById(Person.class, 1);
        assertEquals("DELETE FROM Person where id = 1;", query);
    }

    @DisplayName("test insert generates correct insert query with values")
    @Test
    public void testInsertGeneratesCorrectInsertQueryWithValues() throws IllegalAccessException {
        Person person = new Person(1, "Dima", 200.4);
        String query = queryGenerator.insert(person);
        assertEquals("INSERT INTO Person (id, name, salary) VALUES ('1', 'Dima', '200.4');", query);
    }

    @DisplayName("test update generates correct update query with values and id condition")
    @Test
    public void testUpdateGeneratesCorrectUpdateQueryWithValuesAndIdCondition() throws IllegalAccessException {
        Person person = new Person(1, "Dima", 200.4);
        String query = queryGenerator.update(person);
        assertEquals("UPDATE Person SET id = '1', name = 'Dima', salary = '200.4' WHERE id = 1;", query);
    }

    @DisplayName("test find all throws exception when class is not annotated with table")
    @Test
    public void testFindAllThrowsExceptionWhenClassIsNotAnnotatedWithTable() {
        class NotAnnotatedClass {}
        assertThrows(IllegalArgumentException.class, () -> queryGenerator.findAll(NotAnnotatedClass.class));
    }

    @DisplayName("test find all throws exception when class has no columns annotated with column")
    @Test
    public void testFindAllThrowsExceptionWhenClassHasNoColumnsAnnotatedWithColumn() {
        @Table(name = "EmptyClass")
        class EmptyClass {}
        assertThrows(IllegalArgumentException.class, () -> queryGenerator.findAll(EmptyClass.class));
    }
}
