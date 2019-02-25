package com.subbu.todoApp.datamodel;

/**
 * Created by Subbu
 */
public class TodayItems {

    private String task;
    private String applicationName;
    private String Date;
    private String completed;
    private String priority;
    
	public TodayItems(String task, String applicationName, String date, String completed, String priority) {
        this.task = task;
        this.applicationName = applicationName;
        this.Date = date;
        this.completed = completed;
        this.priority = priority;
    }

    public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

    public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
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
	
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Override
    public String toString() {
        return task;
    }
	
}
