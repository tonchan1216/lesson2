package com.example.lesson2.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
public class TodoServiceTest {
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

    @Autowired
    private TodoService todoService;

    @MockBean
    private TodoRepository todoRepository;

    @Test
    @DisplayName("全Todoが取得できるkとを確認")
    void testFindAll() {
        Todo expectTodo1 = new Todo(1L, "sample 1", false, LocalDateTime.parse("2021/10/11 01:01:01", DATETIME_FORMAT));
        Todo expectTodo2 = new Todo(2L, "sample 2", true, LocalDateTime.parse("2021/10/11 01:01:01",DATETIME_FORMAT));
        Todo expectTodo3 = new Todo(3L, "sample 3", false, LocalDateTime.parse("2021/10/11 01:01:01",DATETIME_FORMAT));

        given(todoRepository.findAll()).willReturn(Arrays.asList(expectTodo1,expectTodo2,expectTodo3));
        Collection<Todo> actualTodos = todoService.findAll();

        then(todoRepository).should(times(1)).findAll();
        assertThat(actualTodos).usingFieldByFieldElementComparator().containsExactly(expectTodo1, expectTodo2, expectTodo3);
    }

    @Test
    @DisplayName("todoIdに対応するTodoが取得できることを確認する(Service)")
    void testFindOne() {
        Todo expectTodo = new Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));
        given(todoRepository.findById(1L)).willReturn(Optional.of(expectTodo));

        Todo actualTodo = todoService.findOne(1L);
        then(todoRepository).should(times(1)).findById(ArgumentMatchers.longThat(arg -> arg == actualTodo.getTodoId()));
        assertThat(actualTodo).isEqualToComparingFieldByField(expectTodo);
    }

    @Test
    @DisplayName("新たなTodoが作成できることを確認する(service)")
    void testCreate() {
        Todo expectTodo = new Todo(null, "sample todo 4", false, null);

        willDoNothing().given(todoRepository).create(expectTodo);

        todoService.create(expectTodo);

        then(todoRepository).should(times(1)).create(
                ArgumentMatchers.<Todo>argThat(
                        arg -> expectTodo.getTodoTitle().equals(arg.getTodoTitle())
                                && !arg.isFinished()
                                && Objects.nonNull(arg.getCreatedAt())
                )
        );
    }

    @Test
    @DisplayName("todoId=1のfinishedがtrueになることを確認する(service)")
    void testFinish() {
        Todo expectTodo = new Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));

        given(todoRepository.findById(1L)).willReturn(Optional.of(expectTodo));
        given(todoRepository.updateById(1L)).willReturn(1L);

        todoService.finish(1L);

        then(todoRepository).should(times(1)).findById(ArgumentMatchers.longThat(arg -> arg == expectTodo.getTodoId()));
        then(todoRepository).should(times(1)).updateById(ArgumentMatchers.longThat(arg -> arg == 1L));
    }

    @Test
    @DisplayName("todoId=1がDeleteによって削除されることを確認する(service)")
    void testDelete() {
        Todo expectTodo = new Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));

        given(todoRepository.findById(1L)).willReturn(Optional.of(expectTodo));
        given(todoRepository.deleteById(1L)).willReturn(1L);

        todoService.delete(1L);

        then(todoRepository).should(times(1)).findById(ArgumentMatchers.longThat(arg -> arg == expectTodo.getTodoId()));
        then(todoRepository).should(times(1)).deleteById(ArgumentMatchers.longThat(arg -> arg == 1L));
    }
}
