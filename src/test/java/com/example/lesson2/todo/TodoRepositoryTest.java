package com.example.lesson2.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@MybatisTest
public class TodoRepositoryTest {
    private static  final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    NamedParameterJdbcOperations jdbcOperations;

    @Test
    @DisplayName("全Todoが取得できることを確認する")
    void testFindAll() {
        Collection<Todo> actualTodos = todoRepository.findAll();

        assertThat(actualTodos)
                .extracting(Todo::getTodoId, Todo::getTodoTitle, Todo::isFinished, Todo::getCreatedAt)
                .contains(
                        tuple(1L,"sample 1", false, LocalDateTime.parse("2021/10/11 01:01:01", DATETIME_FORMAT)),
                        tuple(2L,"sample 2", true,  LocalDateTime.parse("2021/10/11 02:02:02", DATETIME_FORMAT)),
                        tuple(3L,"sample 3", false, LocalDateTime.parse("2021/10/11 03:03:03", DATETIME_FORMAT))
                );
    }

    @Test
    @DisplayName("todoIdに対応するTodoが取得できるか")
    void testFindById() {
        Todo actualTodo = todoRepository.findById(1L).get();

        assertThat(actualTodo)
                .extracting(Todo::getTodoId, Todo::getTodoTitle, Todo::isFinished, Todo::getCreatedAt)
                .contains(1L,"sample 1", false, LocalDateTime.parse("2021/10/11 01:01:01", DATETIME_FORMAT));
    }

    @Test
    @DisplayName("新たなTodoが作成できるか")
    void testCreate() {
        Todo actualTodo = new Todo(null, "sample 4", false, LocalDateTime.parse("2020/10/11 04:04:04", DATETIME_FORMAT));

        todoRepository.create(actualTodo);

        Todo todo = getLastTodo();
        assertThat(actualTodo).usingRecursiveComparison()
                .ignoringFields("todoId")
                .isEqualTo(todo);
        assertThat(actualTodo).hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("finishedをfalseからtrueに変更できるか")
    void testUpdateById() {
        Todo todo = getTodo(1L);

        long count = todoRepository.updateById(1L);
        Todo updated = getTodo(1L);

        assertThat(count).isEqualTo(1L);
        assertThat(updated).usingRecursiveComparison()
                .ignoringFields("todoId", "finished")
                .isEqualTo(todo);
        assertThat(updated).hasFieldOrPropertyWithValue("finished", true);
    }

    @Test
    @DisplayName("未完了か完了済みのTodoの件数を取得できるか")
    void testCountByFinished() {
        long unfinishedCount = todoRepository.countByFinished(false);
        long finishedCount = todoRepository.countByFinished(true);

        assertThat(unfinishedCount).isEqualTo(2);
        assertThat(finishedCount).isEqualTo(1);
    }

    private Todo getLastTodo() {
        String sql = "SELECT * FROM todo ORDER BY todo_id DESC LIMIT 1";
        SqlParameterSource parameterSource = new EmptySqlParameterSource();
        RowMapper<Todo> rowMapper = new BeanPropertyRowMapper<>(Todo.class);
        return jdbcOperations.queryForObject(sql, parameterSource, rowMapper);
    }

    private Todo getTodo(Long todoId) {
        String sql = "SELECT * FROM todo WHERE todo_id=:todoId";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("todoId", todoId);
        RowMapper<Todo> rowMapper = new BeanPropertyRowMapper<>(Todo.class);
        return jdbcOperations.queryForObject(sql, parameterSource, rowMapper);
    }
}
