package com.subbu.todoApp.datamodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.collections.FXCollections;

/**
 * 
 * @author subbu
 *
 */
public class ApplicationData {

	private static ApplicationData instance = new ApplicationData();
	private List<ApplicationItem> applicationItems;
	
	public static ApplicationData getInstance() {
        return instance;
    }
	
	public List<ApplicationItem> getApplicationItems() {
        return applicationItems;
    }

    public void loadApplicationItems(String fileName) throws IOException {

        applicationItems = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);
        BufferedReader br = Files.newBufferedReader(path);
        String input;
        try {
            while ((input = br.readLine()) != null) {
            	ApplicationItem applicationItem = new ApplicationItem(input);
                applicationItems.add(applicationItem);
            }

        } finally {
            if(br != null) {
                br.close();
            }
        }
    }

}
