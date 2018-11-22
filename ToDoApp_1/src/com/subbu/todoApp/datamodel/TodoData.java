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

    public static TodoData getInstance() {
        return instance;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }
    
    public ObservableList<TodoItem> getCompletedItems() {
        return completedItems;
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

}
