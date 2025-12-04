package com.dsti.devops_task_manager.exceptions;


public class TaskCreationException extends TaskException {
    public TaskCreationException(String message) {
        super(message);
    }

    public TaskCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
