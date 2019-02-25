package com.subbu.todoApp.datamodel;

/**
 * Created by Subbu
 */
public class TodoItem {

    private String task;
    private String priority;
    private String Date;
    private String completed;
    
	public TodoItem(String task, String priority, String date, String completed) {
        this.task = task;
        this.priority = priority;
        this.Date = date;
        this.completed = completed;
    }

    public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

    public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}

	@Override
    public String toString() {
        return task;
    }
}
