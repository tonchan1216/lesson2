package com.example.lesson2.todo;

import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.Optional;

@Mapper
public interface TodoRepository {
    @Select("SELCT todo_id, todo_title, finished, created_at FROM todo where todo_id = #{todoId}")
    Optional<Todo> findById(Long todoId);

    @Select("SELCT todo_id, todo_title, finished, created_at FROM todo")
    Collection<Todo> findAll();

    @Insert("INSERT INTO todo(todo_title, finished, created_at) VALUES (#{todoTitle}, #{finished}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "todoId")
    void create(Todo todo);

    @Update("UPDATE todo SET finished = true WHERE todo_id = #{todoId}")
    long updateById(Long todoId);

    @Delete("DELETE FROM todo where todo_id = #{todoId}")
    long deleteById(Long todoId);

    @Select("SELCT count(*) FROM todo where finished = #{finished}")
    long countByFinished(boolean finished);
}
