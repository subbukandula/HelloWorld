package com.subbu.todoApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.subbu.todoApp.datamodel.TodayItems;
import com.subbu.todoApp.datamodel.TodoData;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;

public class CenterController {
	
	private static final String applicationFileName = "ApplicationItems.txt"; 
	DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private static final String completed = "yes";
	private static final String notCompleted = "no";

	@FXML 
	public ListView<TodayItems> centerListView;
	
	public CenterController() {
		//TO DO
	}
	
	public void initialize() throws IOException {
		TodoData.getInstance().loadTodayItems(applicationFileName);
		ObservableList<TodayItems> todayItems = TodoData.getInstance().getTodayItems();
		SortedList<TodayItems> sortedList = new SortedList<>( todayItems, 
				(TodayItems toDo1, TodayItems toDo2) -> {
					if( LocalDate.parse(toDo1.getDate(), format).isBefore(LocalDate.parse(toDo2.getDate(), format)) ) {
						return -1;
					} else if( LocalDate.parse(toDo1.getDate(), format).isAfter(LocalDate.parse(toDo2.getDate(), format))  ) {
						return 1;
					} else {
						return 0;
					}
				});
		centerListView.getItems().setAll(sortedList);
		centerListView.setCellFactory(new Callback<ListView<TodayItems>, ListCell<TodayItems>>(){
			@Override
			public ListCell<TodayItems> call(ListView<TodayItems> param) {
				// TODO Auto-generated method stub
				return new ListCell<TodayItems>(){
					@Override
					protected void updateItem(TodayItems myBean, boolean b) {
						super.updateItem(myBean, b);
						if (!b) {
							HBox box = new HBox();
							Label label = new Label(myBean.getTask());
							label.setPrefWidth(450);
							label.setFont(new Font("Tahoma",13.00));
							Label label1 = new Label(myBean.getApplicationName());
							label1.setPrefWidth(100);
							label1.setFont(new Font("Tahoma",13.00));
							Label label2;
							if(LocalDate.parse(myBean.getDate(), format).isBefore(LocalDate.now())) {
								label2 = new Label(myBean.getDate());
								label2.setPrefWidth(75);
								label2.setFont(new Font("Tahoma",13.00));
								label2.setTextFill(Color.RED);
							} else if(LocalDate.parse(myBean.getDate(), format).isEqual(LocalDate.now())) {
								label2 = new Label("Today");
								label2.setPrefWidth(75);
								label2.setFont(new Font("Tahoma",13.00));
								label2.setTextFill(Color.DARKGREEN);
							} else {
								label2 = new Label(myBean.getDate());
								label2.setPrefWidth(75);
								label2.setFont(new Font("Tahoma",13.00));
							}
							box.getChildren().add(label);
							box.getChildren().add(label1);
							box.getChildren().add(label2);
							setGraphic(box);
						} else {
							setGraphic(null);
						}
					}
				};
			}

		});
		centerListView.setStyle("-fx-font-size: 14.0 ; -fx-font-family: 'Consolas';");
	}

	@FXML 
	public void editToDo(ActionEvent event) {
		TodayItems selectedItem = centerListView.getSelectionModel().getSelectedItem();
		if(null != selectedItem) {
			String applicationName = selectedItem.getApplicationName();
			String taskSelected = selectedItem.getTask();
			String prioritySelected = selectedItem.getPriority();
			String dateSelected = selectedItem.getDate();
			String taskEdit = taskSelected + "\t" + prioritySelected + "\t" + dateSelected; 
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Button bt = new Button("Edit");
			TextField tf2 = new TextField(taskSelected);
			tf2.setMinWidth(60.00);
			MenuButton menuButton = new MenuButton(prioritySelected);
			menuButton.setMinWidth(150.00);
			MenuItem item1 = new MenuItem("1");
			MenuItem item2 = new MenuItem("2");
			MenuItem item3 = new MenuItem("3");
			MenuItem item4 = new MenuItem("4");
			MenuItem item5 = new MenuItem("5");
			menuButton.getItems().addAll(item1,item2,item3,item4,item5);
			item1.setOnAction(e -> {
				menuButton.setText("1");
			});
			item2.setOnAction(e -> {
				menuButton.setText("2");
			});
			item3.setOnAction(e -> {
				menuButton.setText("3");
			});
			item4.setOnAction(e -> {
				menuButton.setText("4");
			});
			item5.setOnAction(e -> {
				menuButton.setText("5");
			});
			DatePicker dp = new DatePicker();
			dp.setValue(LocalDate.parse(dateSelected, format));
			flow.getChildren().addAll(new Text("          Task editing for "+applicationName+" application"));
			flow.getChildren().addAll(new Text("          Task:"),tf2);
			flow.getChildren().addAll(new Text("     Priority:"),menuButton);
			flow.getChildren().addAll(new Text("  Due Date:"),dp);
			flow.getChildren().addAll(new Text("                            "),bt);
			Scene dialogScene = new Scene(flow, 250, 170);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
			bt.setOnAction(e -> {
				String taskName = tf2.getText();
				String priority = menuButton.getText();
				LocalDate value = dp.getValue();
				String dueDate = value.format(format);
				if("".equals(taskName) || taskName.isEmpty()) {
					final Stage dialog1 = new Stage();
					dialog1.initModality(Modality.APPLICATION_MODAL);
					FlowPane flow1 = new FlowPane();
					flow1.setPadding(new Insets(20,5,5,10));
					flow1.setStyle("-fx-background-color: DAE6F3;");
					flow1.getChildren().addAll(new Text("Task Name is Empty. Please try again."));
					Scene dialogScene1 = new Scene(flow1, 230, 50);
					dialog1.setScene(dialogScene1);
					dialog1.setResizable(false);
					dialog1.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
					dialog.close();
					dialog1.show();
				} else if(checkEditTasksExists(taskName,applicationName,taskEdit)){
					final Stage dialog1 = new Stage();
					dialog1.initModality(Modality.APPLICATION_MODAL);
					FlowPane flow1 = new FlowPane();
					flow1.setPadding(new Insets(20,5,5,10));
					flow1.setStyle("-fx-background-color: DAE6F3;");
					flow1.getChildren().addAll(new Text("Task Name already exists for "+applicationName));
					Scene dialogScene1 = new Scene(flow1, 230, 50);
					dialog1.setScene(dialogScene1);
					dialog1.setResizable(false);
					dialog1.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
					dialog.close();
					dialog1.show();
				} else {
					File inputFile = new File(applicationName+".txt");
					File tempFile = new File("myTempEditedTasksFile.txt");
					BufferedReader reader = null;
					BufferedWriter writer = null;
					String input;
					try {
						reader = new BufferedReader(new FileReader(inputFile));
						writer = new BufferedWriter(new FileWriter(tempFile));
						while((input = reader.readLine()) != null) {
							String taskToEdit = taskSelected + "\t" + prioritySelected + "\t" + dateSelected; 
							if(!(input.contains(taskToEdit))) {
								writer.write(input + System.getProperty("line.separator"));
							} else {
								String newTask = taskName + "\t" + priority + "\t" + dueDate + "\t" + notCompleted;
								writer.write(newTask + System.getProperty("line.separator"));
							}
						}
						reader.close();
						writer.close();
						Files.move(tempFile.toPath(), inputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
						final Stage dialog3 = new Stage();
						dialog3.initModality(Modality.APPLICATION_MODAL);
						FlowPane flow3 = new FlowPane();
						flow3.setPadding(new Insets(20,5,5,10));
						flow3.setVgap(10);
						flow3.setHgap(10);
						flow3.setPrefWrapLength(170);
						flow3.setStyle("-fx-background-color: DAE6F3;");
						flow3.getChildren().addAll(new Text("    Task edited successfully for "+applicationName+" application."));
						Scene dialogScene3 = new Scene(flow3, 330, 70);
						dialog3.setScene(dialogScene3);
						dialog3.setResizable(false);
						dialog3.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
						dialog3.show();
						dialog.close();
						initialize();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} 
				}
			});
		} else {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Text tx1 = new Text(" No task is selected for edit");
			flow.getChildren().addAll(tx1);
			Scene dialogScene = new Scene(flow, 200, 75);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
		}
		
	}

	@FXML 
	public void completeToDo(ActionEvent event) {
		TodayItems selectedItem = centerListView.getSelectionModel().getSelectedItem();
		if(null != selectedItem) {
			String applicationName = selectedItem.getApplicationName();
			String task = selectedItem.getTask();
			String priority = selectedItem.getPriority();
			String date = selectedItem.getDate();
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Button bt = new Button("Yes");
			Button bt1 = new Button("No");
			Text tx1 = new Text(" Are you sure to complete selected task for application "+applicationName);
			flow.getChildren().addAll(tx1);
			flow.getChildren().addAll(new Text("                   "),bt,new Text("    "),bt1);
			Scene dialogScene = new Scene(flow, 350, 100);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
			bt.setOnAction(e -> {
				File inputFile = new File(applicationName+".txt");
				File tempFile = new File("myTempCompleteFile.txt");
				BufferedReader reader = null;
				BufferedWriter writer = null;
				String input;
				try {
					reader = new BufferedReader(new FileReader(inputFile));
					writer = new BufferedWriter(new FileWriter(tempFile));
					while((input = reader.readLine()) != null) {
						String taskToComplete = task + "\t" + priority + "\t" + date; 
						if(!(input.contains(taskToComplete))) {
							writer.write(input + System.getProperty("line.separator"));
						} else {
							String taskCompleted = task + "\t" + priority + "\t" + date + "\t" + completed;
							writer.write(taskCompleted + System.getProperty("line.separator"));
						}
					}
					reader.close();
					writer.close();
					Files.move(tempFile.toPath(), inputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					final Stage dialog3 = new Stage();
					dialog3.initModality(Modality.APPLICATION_MODAL);
					FlowPane flow3 = new FlowPane();
					flow3.setPadding(new Insets(20,5,5,10));
					flow3.setVgap(10);
					flow3.setHgap(10);
					flow3.setPrefWrapLength(170);
					flow3.setStyle("-fx-background-color: DAE6F3;");
					flow3.getChildren().addAll(new Text("   Selected Task completed successfully for "+applicationName+" application."));
					Scene dialogScene3 = new Scene(flow3, 350, 70);
					dialog3.setScene(dialogScene3);
					dialog3.setResizable(false);
					dialog3.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
					dialog3.show();
					dialog.close();
					initialize();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
			});
			bt1.setOnAction(e -> {
				dialog.close();
			});
		} else {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Text tx1 = new Text(" No task is selected to complete");
			flow.getChildren().addAll(tx1);
			Scene dialogScene = new Scene(flow, 200, 75);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
		}
	}
	
	private boolean checkEditTasksExists(String taskName, String applicationName, String taskEdit) {
		String input;
		BufferedReader br = null;
		try {
			Path path = Paths.get(applicationName+".txt");
			br = Files.newBufferedReader(path);
			while ((input = br.readLine()) != null) {
				String[] tasks = input.split("\t");
				if(!(input.contains(taskEdit)) && tasks[0].trim().equals(taskName.trim())) {
					return true;
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if(br != null) {
                try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        return false;
	}
}
