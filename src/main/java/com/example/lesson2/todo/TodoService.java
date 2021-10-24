package com.example.lesson2.todo;

import java.util.Collection;

public interface TodoService {
    Todo findOne(Long todoId);

    Collection<Todo> findAll();

    Todo create(Todo todo);

    Todo finish(Long todoId);

    void delete(Long todoId);
}
