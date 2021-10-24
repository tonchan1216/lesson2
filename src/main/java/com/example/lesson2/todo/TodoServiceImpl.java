package com.example.lesson2.todo;

import com.example.lesson2.common.exception.BusinessException;
import com.example.lesson2.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Transactional
public class TodoServiceImpl implements TodoService{
    private  static final long MAX_UNFINISHED_COUNT = 5;

    @Autowired
    TodoRepository todoRepository;

    @Override
    public Todo findOne(Long todoId) {
        return todoRepository.findById(todoId).orElseThrow(() -> new ResourceNotFoundException(
                "The requested Todo is not found. (id=" + todoId + ")"
        ));
    }

    @Override
    public Collection<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Override
    public  Todo create(Todo todo) {
        long unfinishedCount = todoRepository.countByFinished(false);
        if (unfinishedCount >= MAX_UNFINISHED_COUNT) {
            throw new BusinessException(
                    "The count of un-finished Todo must not be over " + MAX_UNFINISHED_COUNT + "."
            );
        }

        LocalDateTime createdAt = LocalDateTime.now();
        todo.setCreatedAt(createdAt);
        todo.setFinished(false);

        todoRepository.create(todo);
        return todo;
    }

    @Override
    public Todo finish(Long todoId) {
        Todo todo = findOne(todoId);
        if(todo.isFinished()) {
            throw new BusinessException(
                    "the Requested Todo is already finished. (id=" + todoId + ")"
            );
        }
        todo.setFinished(true);
        todoRepository.updateById(todoId);
        return  todo;
    }

    @Override
    public void delete(Long todoId) {
        findOne(todoId);
        todoRepository.deleteById(todoId);
    }


}
