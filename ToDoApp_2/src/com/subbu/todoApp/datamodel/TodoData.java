package com.subbu.todoApp.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Subbu
 */
public class TodoData{
    private static TodoData instance = new TodoData();
    private ObservableList<TodoItem> todoItems;
    private ObservableList<TodoItem> completedItems;
    private ObservableList<TodayItems> todayItems;
    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public static TodoData getInstance() {
        return instance;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }
    
    public ObservableList<TodoItem> getCompletedItems() {
        return completedItems;
    }
    
    public ObservableList<TodayItems> getTodayItems() {
        return todayItems;
    }

    public void loadTodoItems(String filename) throws IOException {
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);
        String input;
        try {
            while ((input = br.readLine()) != null) {
            	String[] tasks = input.split("\t");
            	if("no".equals(tasks[3])) {
            		TodoItem todoItem = new TodoItem(tasks[0],tasks[1],tasks[2],tasks[3]);
            		todoItems.add(todoItem);
            	}
            }
        } finally {
            if(br != null) {
                br.close();
            }
        }
    }
    
    public void loadCompletedItems(String filename) throws IOException {
    	completedItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);
        String input;
        try {
            while ((input = br.readLine()) != null) {
            	String[] tasks = input.split("\t");
            	if("yes".equals(tasks[3])) {
            		TodoItem todoItem = new TodoItem(tasks[0],tasks[1],tasks[2],tasks[3]);
            		completedItems.add(todoItem);
            	}
            }
        } finally {
            if(br != null) {
                br.close();
            }
        }
    }
    
    public void loadTodayItems(String applicationFile) throws IOException {
    	todayItems = FXCollections.observableArrayList();
        Path path = Paths.get(applicationFile);
        BufferedReader br = Files.newBufferedReader(path);
        String applicationName;
        String input;
        try {
            while ((applicationName = br.readLine()) != null) {
            	Path filePath = Paths.get(applicationName+".txt");
            	BufferedReader br1 = null;
            	try {
            		br1 = Files.newBufferedReader(filePath);
            		while ((input = br1.readLine()) != null) {
            			String[] tasks = input.split("\t");
            			String date = tasks[2];
            			if("no".equals(tasks[3]) && !(LocalDate.parse(date, format).isAfter(LocalDate.now()))) {
            				TodayItems todayItem = new TodayItems(tasks[0],applicationName,tasks[2],tasks[3],tasks[1]);
            				todayItems.add(todayItem);
            			}
            		}
            	} finally {
            		if(br1 != null) {
            			br1.close();
            		}
            	}
            }
        } finally {
            if(br != null) {
                br.close();
            }
        }
    }

}
