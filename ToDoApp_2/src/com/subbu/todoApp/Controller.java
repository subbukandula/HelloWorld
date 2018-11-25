package com.subbu.todoApp;

import com.subbu.todoApp.datamodel.ApplicationData;
import com.subbu.todoApp.datamodel.ApplicationItem;
import com.subbu.todoApp.datamodel.TodoData;
import com.subbu.todoApp.datamodel.TodoItem;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

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
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;

/**
 * 
 * @author subbu
 *
 */
public class Controller {
	
	private static final String applicationFileName = "ApplicationItems.txt"; 
	DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private Font font = Font.font("Tahoma",FontPosture.ITALIC,13.00);
	private static final String completed = "yes";
	private static final String notCompleted = "no";

    @FXML
    private ListView<ApplicationItem> applicationItemView;

    @FXML
    private ListView<TodoItem> todoListView;
    
    @FXML
    private ListView<TodoItem> completedListView;
	
    @FXML 
    private BorderPane borderPane;
	
    @FXML 
    private VBox centerVBOX;
	
    public void initialize() throws IOException {
    	
    	borderPane.setCenter(centerVBOX);
    	
    	File file = new File(applicationFileName);
    	if(!file.exists()) {
    		file.createNewFile();
    	}
    	ApplicationData.getInstance().loadApplicationItems(applicationFileName);
    	applicationItemView.getItems().setAll(ApplicationData.getInstance().getApplicationItems());
    	applicationItemView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	applicationItemView.getSelectionModel().selectFirst();
    	applicationItemView.setStyle("-fx-font-size: 12.0 ; -fx-font-family: 'SketchFlow Print';");

    	applicationItemView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ApplicationItem>() {
    		@Override
    		public void changed(ObservableValue<? extends ApplicationItem> observable, ApplicationItem oldValue,
    				ApplicationItem newValue) {
    			if(newValue != null) {
    				borderPane.setCenter(centerVBOX);
    				ApplicationItem applicationItem = applicationItemView.getSelectionModel().getSelectedItem();
    				String fileName = applicationItem.getApplicationName();
    				try {
    					File file2 = new File(fileName+".txt");
    					if(!file2.exists()) {
    						file2.createNewFile();
    					}
    					TodoData.getInstance().loadTodoItems(fileName+".txt");
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    				ObservableList<TodoItem> todoItems = TodoData.getInstance().getTodoItems();
    				SortedList<TodoItem> sortedList = new SortedList<>( todoItems, 
    						(TodoItem toDo1, TodoItem toDo2) -> {
    							if( LocalDate.parse(toDo1.getDate(), format).isBefore(LocalDate.parse(toDo2.getDate(), format)) ) {
    								return -1;
    							} else if( LocalDate.parse(toDo1.getDate(), format).isAfter(LocalDate.parse(toDo2.getDate(), format))  ) {
    								return 1;
    							} else if(Integer.parseInt(toDo1.getPriority()) < Integer.parseInt(toDo2.getPriority())) {
    								return -1;
    							} else if(Integer.parseInt(toDo1.getPriority()) > Integer.parseInt(toDo2.getPriority())) {
    								return 1;
    							} else {
    								return 0;
    							}
    						});
    				todoListView.getItems().setAll(sortedList);
    				todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>(){
    					@Override
    					public ListCell<TodoItem> call(ListView<TodoItem> param) {
    						// TODO Auto-generated method stub
    						return new ListCell<TodoItem>(){
    							@Override
    							protected void updateItem(TodoItem myBean, boolean b) {
    								super.updateItem(myBean, b);
    								if (!b) {
    									HBox box = new HBox();
    									Label label = new Label(myBean.getTask());
    									label.setPrefWidth(475);
    									label.setFont(new Font("Tahoma",13.00));
    									Label label1 = new Label(myBean.getPriority());
    									label1.setPrefWidth(75);
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
    				todoListView.setStyle("-fx-font-size: 14.0 ; -fx-font-family: 'Consolas';");
    				try {
    					TodoData.getInstance().loadCompletedItems(fileName+".txt");
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    				ObservableList<TodoItem> completedItems = TodoData.getInstance().getCompletedItems();
    				SortedList<TodoItem> sortedCompletedList = new SortedList<>( completedItems, 
    						(TodoItem toDo1, TodoItem toDo2) -> {
    							if( LocalDate.parse(toDo1.getDate(), format).isBefore(LocalDate.parse(toDo2.getDate(), format)) ) {
    								return -1;
    							} else if( LocalDate.parse(toDo1.getDate(), format).isAfter(LocalDate.parse(toDo2.getDate(), format))  ) {
    								return 1;
    							} else if(Integer.parseInt(toDo1.getPriority()) < Integer.parseInt(toDo2.getPriority())) {
    								return -1;
    							} else if(Integer.parseInt(toDo1.getPriority()) > Integer.parseInt(toDo2.getPriority())) {
    								return 1;
    							} else {
    								return 0;
    							}
    						});
    				completedListView.getItems().setAll(sortedCompletedList);
    				completedListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>(){

    					@Override
    					public ListCell<TodoItem> call(ListView<TodoItem> param) {
    						// TODO Auto-generated method stub
    						return new ListCell<TodoItem>(){
    							@Override
    							protected void updateItem(TodoItem myBean, boolean b) {
    								super.updateItem(myBean, b);
    								if (!b) {
    									HBox box = new HBox();
    									Text tx1 = new Text(myBean.getTask());
    									tx1.setWrappingWidth(475);
    									tx1.setFont(font);
    									tx1.setStrikethrough(true);
    									Text tx2 = new Text(myBean.getPriority());
    									tx2.setWrappingWidth(75);
    									tx2.setFont(font);
    									tx2.setStrikethrough(true);
    									Text tx3;
    									if(LocalDate.parse(myBean.getDate(), format).isBefore(LocalDate.now())) {
    										tx3 = new Text(myBean.getDate());
    										tx3.setWrappingWidth(75);
    										tx3.setFont(font);
    										tx3.setStrikethrough(true);
    										tx3.setFill(Color.RED);
    									} else if(LocalDate.parse(myBean.getDate(), format).isEqual(LocalDate.now())) {
    										tx3 = new Text("Today");
    										tx3.setWrappingWidth(75);
    										tx3.setFont(font);
    										tx3.setStrikethrough(true);
    										tx3.setFill(Color.DARKGREEN);
    									} else {
    										tx3 = new Text(myBean.getDate());
    										tx3.setWrappingWidth(75);
    										tx3.setFont(font);
    										tx3.setStrikethrough(true);
    									}
    									box.getChildren().add(tx1);
    									box.getChildren().add(tx2);
    									box.getChildren().add(tx3);
    									setGraphic(box);
    								} else {
    									setGraphic(null);
    								}
    							}
    						};
    					}

    				});
    				completedListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    				completedListView.getSelectionModel().selectFirst();
    				completedListView.setStyle("-fx-font-size: 14.0 ; -fx-font-family: 'Consolas';");
    			}
    		}
    	});

    	ApplicationItem applicationItem = applicationItemView.getSelectionModel().getSelectedItem();
    	if(null != applicationItem) {
    		String fileName = applicationItem.getApplicationName();
    		File file3 = new File(fileName+".txt");
    		if(!file3.exists()) {
    			file3.createNewFile();
    		}
    		TodoData.getInstance().loadTodoItems(fileName+".txt");
    		ObservableList<TodoItem> todoItems = TodoData.getInstance().getTodoItems();
    		SortedList<TodoItem> sortedList = new SortedList<>( todoItems, 
    				(TodoItem toDo1, TodoItem toDo2) -> {
    					if( LocalDate.parse(toDo1.getDate(), format).isBefore(LocalDate.parse(toDo2.getDate(), format)) ) {
    						return -1;
    					} else if( LocalDate.parse(toDo1.getDate(), format).isAfter(LocalDate.parse(toDo2.getDate(), format))  ) {
    						return 1;
    					} else if(Integer.parseInt(toDo1.getPriority()) < Integer.parseInt(toDo2.getPriority())) {
    						return -1;
    					} else if(Integer.parseInt(toDo1.getPriority()) > Integer.parseInt(toDo2.getPriority())) {
    						return 1;
    					} else {
    						return 0;
    					}
    				});
    		todoListView.getItems().setAll(sortedList);
    		todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>(){

    			@Override
    			public ListCell<TodoItem> call(ListView<TodoItem> param) {
    				// TODO Auto-generated method stub
    				return new ListCell<TodoItem>(){
    					@Override
    					protected void updateItem(TodoItem myBean, boolean b) {
    						super.updateItem(myBean, b);
    						if (!b) {
    							HBox box = new HBox();
    							Label label = new Label(myBean.getTask());
    							label.setPrefWidth(475);
    							label.setFont(new Font("Tahoma",13.00));
    							Label label1 = new Label(myBean.getPriority());
    							label1.setPrefWidth(75);
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
    		todoListView.setStyle("-fx-font-size: 14.0 ; -fx-font-family: 'Consolas';");
    		TodoData.getInstance().loadCompletedItems(fileName+".txt");
    		ObservableList<TodoItem> completedItems = TodoData.getInstance().getCompletedItems();
    		SortedList<TodoItem> sortedCompletedList = new SortedList<>( completedItems, 
    				(TodoItem toDo1, TodoItem toDo2) -> {
    					if( LocalDate.parse(toDo1.getDate(), format).isBefore(LocalDate.parse(toDo2.getDate(), format)) ) {
    						return -1;
    					} else if( LocalDate.parse(toDo1.getDate(), format).isAfter(LocalDate.parse(toDo2.getDate(), format))  ) {
    						return 1;
    					} else if(Integer.parseInt(toDo1.getPriority()) < Integer.parseInt(toDo2.getPriority())) {
    						return -1;
    					} else if(Integer.parseInt(toDo1.getPriority()) > Integer.parseInt(toDo2.getPriority())) {
    						return 1;
    					} else {
    						return 0;
    					}
    				});
    		completedListView.getItems().setAll(sortedCompletedList);
    		completedListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>(){

    			@Override
    			public ListCell<TodoItem> call(ListView<TodoItem> param) {
    				// TODO Auto-generated method stub
    				return new ListCell<TodoItem>(){
    					@Override
    					protected void updateItem(TodoItem myBean, boolean b) {
    						super.updateItem(myBean, b);
    						if (!b) {
    							HBox box = new HBox();
    							Text tx1 = new Text(myBean.getTask());
    							tx1.setWrappingWidth(475);
    							tx1.setFont(font);
    							tx1.setStrikethrough(true);
    							Text tx2 = new Text(myBean.getPriority());
    							tx2.setWrappingWidth(75);
    							tx2.setFont(font);
    							tx2.setStrikethrough(true);
    							Text tx3;
    							if(LocalDate.parse(myBean.getDate(), format).isBefore(LocalDate.now())) {
    								tx3 = new Text(myBean.getDate());
    								tx3.setWrappingWidth(75);
    								tx3.setFont(font);
    								tx3.setStrikethrough(true);
    								tx3.setFill(Color.RED);
    							} else if(LocalDate.parse(myBean.getDate(), format).isEqual(LocalDate.now())) {
    								tx3 = new Text("Today");
    								tx3.setWrappingWidth(75);
    								tx3.setFont(font);
    								tx3.setStrikethrough(true);
    								tx3.setFill(Color.DARKGREEN);
    							} else {
    								tx3 = new Text(myBean.getDate());
    								tx3.setWrappingWidth(75);
    								tx3.setFont(font);
    								tx3.setStrikethrough(true);
    							}
    							box.getChildren().add(tx1);
    							box.getChildren().add(tx2);
    							box.getChildren().add(tx3);
    							setGraphic(box);
    						} else {
    							setGraphic(null);
    						}
    					}
    				};
    			}

    		});
    		completedListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    		completedListView.getSelectionModel().selectFirst();
    		completedListView.setStyle("-fx-font-size: 14.0 ; -fx-font-family: 'Consolas';");
    	} else {
    		Parent root1 = null;
			try {
				root1 = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    	borderPane.getChildren().setAll(root1);
    		List<TodoItem> todoItems = FXCollections.observableArrayList();
    		todoListView.getItems().setAll(todoItems);
    		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    		todoListView.getSelectionModel().selectFirst();
    		todoListView.setStyle("-fx-font-size: 14.0 ; -fx-font-family: 'Consolas';");
    		completedListView.getItems().setAll(todoItems);
    		completedListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    		completedListView.getSelectionModel().selectFirst();
    		completedListView.setStyle("-fx-font-size: 14.0 ; -fx-font-family: 'Consolas';");
    	}
    	
    }

	@FXML 
	public void addApplication(ActionEvent event) {
		final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FlowPane flow = new FlowPane();
        flow.setPadding(new Insets(20,5,5,10));
        flow.setVgap(10);
        flow.setHgap(10);
        flow.setPrefWrapLength(170);
        flow.setStyle("-fx-background-color: DAE6F3;");
        Button bt = new Button("Add");
        TextField tf2 = new TextField();
        tf2.setMinWidth(20.00);
        flow.getChildren().addAll(new Text("  Application Name:"),tf2);
        flow.getChildren().addAll(new Text("                            "),bt);
        Scene dialogScene = new Scene(flow, 300, 100);
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
        dialog.show();
        
        bt.setOnAction(e -> {
            String applicationName = tf2.getText();
            if("".equals(applicationName) || applicationName.isEmpty()) {
            	final Stage dialog1 = new Stage();
            	dialog1.initModality(Modality.APPLICATION_MODAL);
                FlowPane flow1 = new FlowPane();
                flow1.setPadding(new Insets(20,5,5,10));
                flow1.setStyle("-fx-background-color: DAE6F3;");
                flow1.getChildren().addAll(new Text("Application Name is Empty. Please try again."));
                Scene dialogScene1 = new Scene(flow1, 250, 50);
                dialog1.setScene(dialogScene1);
                dialog1.setResizable(false);
                dialog1.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
                dialog.close();
                dialog1.show();
            } else if(checkExists(applicationName)) {
            	final Stage dialog2 = new Stage();
            	dialog2.initModality(Modality.APPLICATION_MODAL);
                FlowPane flow2 = new FlowPane();
                flow2.setPadding(new Insets(20,5,5,10));
                flow2.setStyle("-fx-background-color: DAE6F3;");
                flow2.getChildren().addAll(new Text("Application Name "+applicationName+" already exists. Please try again."));
                Scene dialogScene2 = new Scene(flow2, 300, 50);
                dialog2.setScene(dialogScene2);
                dialog2.setResizable(false);
                dialog2.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
                dialog.close();
                dialog2.show();
            } else {
            	FileWriter fileWriter = null;
            	try {
            		File file = new File(applicationFileName);
            		if(!file.exists()) {
            			file.createNewFile();
            		}
					fileWriter = new FileWriter(file,true);
					fileWriter.write(applicationName);
					fileWriter.write(System.lineSeparator());
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						fileWriter.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
            	final Stage dialog3 = new Stage();
            	dialog3.initModality(Modality.APPLICATION_MODAL);
                FlowPane flow3 = new FlowPane();
                flow3.setPadding(new Insets(20,5,5,10));
                flow3.setStyle("-fx-background-color: DAE6F3;");
                flow3.getChildren().addAll(new Text("Application Name "+applicationName+" added successfully."));
                Scene dialogScene3 = new Scene(flow3, 300, 50);
                dialog3.setScene(dialogScene3);
                dialog3.setResizable(false);
                dialog3.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
                dialog.close();
                dialog3.show();
                try {
					initialize();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
	}
	
	private boolean checkExists(String applicationName){
		String input;
		BufferedReader br = null;
		try {
			Path path = Paths.get(applicationFileName);
			br = Files.newBufferedReader(path);
			while ((input = br.readLine()) != null) {
				if(input.trim().equals(applicationName.trim())) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
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

	@FXML 
	public void deleteApplication(ActionEvent event) {
		ApplicationItem applicationItem = applicationItemView.getSelectionModel().getSelectedItem();
		if(null != applicationItem) {
			String applicationName = applicationItem.getApplicationName();
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
	        flow.getChildren().addAll(new Text("      Are you sure to delete "+applicationName+" application?"));
	        flow.getChildren().addAll(new Text("                   "),bt,new Text("    "),bt1);
	        Scene dialogScene = new Scene(flow, 250, 100);
	        dialog.setScene(dialogScene);
	        dialog.setResizable(false);
	        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
	        dialog.show();
	        bt.setOnAction(e -> {
	        	File inputFile = new File(applicationFileName);
	        	File tempFile = new File("myTempFile.txt");
	        	BufferedReader reader = null;
	        	BufferedWriter writer = null;
	        	String input;
	        	try {
					reader = new BufferedReader(new FileReader(inputFile));
					writer = new BufferedWriter(new FileWriter(tempFile));
					while((input = reader.readLine()) != null) {
						if(!(applicationName.trim().equals(input.trim()))){
							writer.write(input + System.getProperty("line.separator"));
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
					flow3.getChildren().addAll(new Text("      "+applicationName+" application deleted successfully."));
					Scene dialogScene3 = new Scene(flow3, 250, 70);
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
    		final Stage dialog2 = new Stage();
        	dialog2.initModality(Modality.APPLICATION_MODAL);
            FlowPane flow1 = new FlowPane();
            flow1.setPadding(new Insets(20,5,5,10));
            flow1.setStyle("-fx-background-color: DAE6F3;");
            flow1.getChildren().addAll(new Text("No application has been selected to delete."));
            Scene dialogScene1 = new Scene(flow1, 250, 50);
            dialog2.setScene(dialogScene1);
            dialog2.setResizable(false);
            dialog2.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
            dialog2.show();
    	}
		
		
	}

	@FXML 
	public void addToDo(ActionEvent event) {
		ApplicationItem applicationItem = applicationItemView.getSelectionModel().getSelectedItem();
		if(null != applicationItem) {
			int index = applicationItemView.getSelectionModel().getSelectedIndex();
			String applicationName = applicationItem.getApplicationName();
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Button bt = new Button("Add");
			TextField tf2 = new TextField();
			tf2.setMinWidth(60.00);
			MenuButton menuButton = new MenuButton("1");
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
			dp.setValue(LocalDate.now());
			flow.getChildren().addAll(new Text("          Task adding for "+applicationName+" application"));
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
				} else if(checkTasksExists(taskName,applicationName)){
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
					FileWriter fileWriter = null;
					try {
						File file = new File(applicationName+".txt");
						if(!file.exists()) {
							file.createNewFile();
						}
						fileWriter = new FileWriter(file,true);
						fileWriter.write(taskName+"\t"+priority+"\t"+dueDate+"\t"+notCompleted);
						fileWriter.write(System.lineSeparator());
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						try {
							fileWriter.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					final Stage dialog3 = new Stage();
					dialog3.initModality(Modality.APPLICATION_MODAL);
					FlowPane flow3 = new FlowPane();
					flow3.setPadding(new Insets(20,5,5,10));
					flow3.setStyle("-fx-background-color: DAE6F3;");
					flow3.getChildren().addAll(new Text("Task added successfully for "+applicationName));
					Scene dialogScene3 = new Scene(flow3, 250, 50);
					dialog3.setScene(dialogScene3);
					dialog3.setResizable(false);
					dialog3.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
					dialog.close();
					dialog3.show();
					try {
						initialize();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					applicationItemView.getSelectionModel().clearSelection();
					applicationItemView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
					applicationItemView.getSelectionModel().select(index);
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
	        flow.getChildren().addAll(new Text(" No application is selected for adding to do tasks."));
	        Scene dialogScene = new Scene(flow, 300, 75);
	        dialog.setScene(dialogScene);
	        dialog.setResizable(false);
	        dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
	        dialog.show();
		}
	}

	private boolean checkTasksExists(String taskName, String applicationName) {
		String input;
		BufferedReader br = null;
		try {
			Path path = Paths.get(applicationName+".txt");
			br = Files.newBufferedReader(path);
			while ((input = br.readLine()) != null) {
				String[] tasks = input.split("\t");
				if(tasks[0].trim().equals(taskName.trim())) {
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

	@FXML 
	public void exitApplication() {
		Platform.exit();
	}

	@FXML 
	public void deleteToDo(ActionEvent event) {
		TodoItem toDoItem = completedListView.getSelectionModel().getSelectedItem();
		ApplicationItem applicationItem = applicationItemView.getSelectionModel().getSelectedItem();
		if(null != toDoItem && null != applicationItem) {
			int index = applicationItemView.getSelectionModel().getSelectedIndex();
			String applicationName = applicationItem.getApplicationName();
			String task = toDoItem.getTask();
			String priority = toDoItem.getPriority();
			String date = toDoItem.getDate();
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
			Text tx1 = new Text(" Are you sure to delete task for application "+applicationName);
			flow.getChildren().addAll(tx1);
			flow.getChildren().addAll(new Text("                   "),bt,new Text("    "),bt1);
			Scene dialogScene = new Scene(flow, 320, 100);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
			bt.setOnAction(e -> {
				File inputFile = new File(applicationName+".txt");
				File tempFile = new File("myTempTasksFile.txt");
				BufferedReader reader = null;
				BufferedWriter writer = null;
				String input;
				try {
					reader = new BufferedReader(new FileReader(inputFile));
					writer = new BufferedWriter(new FileWriter(tempFile));
					while((input = reader.readLine()) != null) {
						String taskToDelete = task + "\t" + priority + "\t" + date; 
						if(!(input.contains(taskToDelete))) {
							writer.write(input + System.getProperty("line.separator"));
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
					flow3.getChildren().addAll(new Text("    Task deleted successfully for "+applicationName+" application."));
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
				applicationItemView.getSelectionModel().clearSelection();
				applicationItemView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				applicationItemView.getSelectionModel().select(index);
			});
			bt1.setOnAction(e -> {
				dialog.close();
			});
		} else if(null == applicationItem) {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Text tx1 = new Text(" No application has been selected for deleting completed ToDo tasks");
			flow.getChildren().addAll(tx1);
			Scene dialogScene = new Scene(flow, 400, 75);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
		} else {
			String applicationName = applicationItem.getApplicationName();
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Text tx1 = new Text(" No completed task is selected for application "+applicationName+" to delete");
			flow.getChildren().addAll(tx1);
			Scene dialogScene = new Scene(flow, 350, 75);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
		}
	}

	@FXML 
	public void completeToDo(ActionEvent event) {
		TodoItem toDoItem = todoListView.getSelectionModel().getSelectedItem();
		ApplicationItem applicationItem = applicationItemView.getSelectionModel().getSelectedItem();
		if(null != toDoItem && null != applicationItem) {
			int index = applicationItemView.getSelectionModel().getSelectedIndex();
			String applicationName = applicationItem.getApplicationName();
			String task = toDoItem.getTask();
			String priority = toDoItem.getPriority();
			String date = toDoItem.getDate();
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
			Text tx1 = new Text(" Are you sure to complete task for application "+applicationName);
			flow.getChildren().addAll(tx1);
			flow.getChildren().addAll(new Text("                   "),bt,new Text("    "),bt1);
			Scene dialogScene = new Scene(flow, 320, 100);
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
					flow3.getChildren().addAll(new Text("    Task completed successfully for "+applicationName+" application."));
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
				applicationItemView.getSelectionModel().clearSelection();
				applicationItemView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				applicationItemView.getSelectionModel().select(index);
			});
			bt1.setOnAction(e -> {
				dialog.close();
			});
		} else if(null == applicationItem) {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Text tx1 = new Text(" No application has been selected for completing ToDo task");
			flow.getChildren().addAll(tx1);
			Scene dialogScene = new Scene(flow, 400, 75);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
		} else {
			String applicationName = applicationItem.getApplicationName();
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Text tx1 = new Text(" No task is selected for application "+applicationName+" to complete");
			flow.getChildren().addAll(tx1);
			Scene dialogScene = new Scene(flow, 350, 75);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
		}
	}

	@FXML 
	public void editToDo(ActionEvent event) {
		TodoItem toDoItem = todoListView.getSelectionModel().getSelectedItem();
		ApplicationItem applicationItem = applicationItemView.getSelectionModel().getSelectedItem();
		if(null != toDoItem && null != applicationItem) {
			int index = applicationItemView.getSelectionModel().getSelectedIndex();
			String applicationName = applicationItem.getApplicationName();
			String taskSelected = toDoItem.getTask();
			String prioritySelected = toDoItem.getPriority();
			String dateSelected = toDoItem.getDate();
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
					applicationItemView.getSelectionModel().clearSelection();
					applicationItemView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
					applicationItemView.getSelectionModel().select(index);
				}
			});
		} else if(null == applicationItem) {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Text tx1 = new Text(" No application has been selected for editing ToDo task");
			flow.getChildren().addAll(tx1);
			Scene dialogScene = new Scene(flow, 400, 75);
			dialog.setScene(dialogScene);
			dialog.setResizable(false);
			dialog.getIcons().add(new Image(Main.class.getResourceAsStream("ToDo.png" )));
			dialog.show();
		} else {
			String applicationName = applicationItem.getApplicationName();
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FlowPane flow = new FlowPane();
			flow.setPadding(new Insets(20,5,5,10));
			flow.setVgap(10);
			flow.setHgap(10);
			flow.setPrefWrapLength(170);
			flow.setStyle("-fx-background-color: DAE6F3;");
			Text tx1 = new Text(" No task is selected for application "+applicationName+" to edit");
			flow.getChildren().addAll(tx1);
			Scene dialogScene = new Scene(flow, 350, 75);
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

	@FXML 
	public void todayTasks(ActionEvent event) throws IOException {
		Parent centerNode = FXMLLoader.load(getClass().getResource("centerPane.fxml"));
		borderPane.setCenter(centerNode);
		applicationItemView.getSelectionModel().clearSelection();
	}

}
