package com.luv2code.todos.service;

import com.luv2code.todos.entity.Todo;
import com.luv2code.todos.entity.User;
import com.luv2code.todos.repository.TodoRepository;
import com.luv2code.todos.request.TodoRequest;
import com.luv2code.todos.response.TodoResponse;
import com.luv2code.todos.util.FindAuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;

    public TodoServiceImpl(TodoRepository todoRepository, FindAuthenticatedUser findAuthenticatedUser) {
        this.todoRepository = todoRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    // ===================================== GET ALL =====================================

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getAllTodos() {
        // Get current user, who logging in
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        return todoRepository.findByOwner(currentUser)
                .stream()
                .map(this::convertToTodoResponse)
                .toList();
    }

    private TodoResponse convertToTodoResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getPriority(),
                todo.isComplete()
        );
    }

    // ===================================== CREATE =====================================
    @Override
    @Transactional
    public TodoResponse createTodo(TodoRequest todoRequest) {
        // Get current user, who logging in
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        // Mapper TodoRequest -> Todo Entity
        Todo todo = new Todo(
                todoRequest.getTitle(),
                todoRequest.getDescription(),
                todoRequest.getPriority(),
                false,
                currentUser
        );

        // Save todo to database
        Todo savedTodo = todoRepository.save(todo);

        // Mapper savedTodo -> Todo Response and return
        return convertToTodoResponse(savedTodo);
    }

    // ===================================== TOGGLE =====================================
    @Override
    public TodoResponse toggleTodoCompletion(long id) {
        // Get current user, who logging in
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        // Find todo object
        Optional<Todo> todo = todoRepository.findByIdAndOwner(id, currentUser);

        // Check not found todo object
        if (todo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TODO NOT FOUND");
        }

        // Toggle process
        todo.get().setComplete(
                !todo.get().isComplete()
        );

        // Updated to database
        Todo updatedTodo = todoRepository.save(todo.get());

        return convertToTodoResponse(updatedTodo);
    }

    // ===================================== DELETE =====================================
    @Override
    @Transactional
    public void deleteTodo(long id) {
        // Get current user, who logging in
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        // Find todo object
        Optional<Todo> todo = todoRepository.findByIdAndOwner(id, currentUser);

        // Check not found todo object
        if (todo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TODO NOT FOUND");
        }

        // Delete from database
        todoRepository.delete(todo.get());
    }
}
