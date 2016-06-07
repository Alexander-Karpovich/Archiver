import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.*;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main extends Application{
	ListView<File> box;
	File curDirectory;

	public static void main(String[] args) {

		//Archiver.archivate(new File("./pack"));

		//Archiver.dearchivate(new File("нес.exe.arch"), "unpack.exe");
		launch(args);

	}

	@Override
	public void start(Stage mainStage) throws Exception {
		GridPane root = new GridPane();
		root.setGridLinesVisible(true);

		Scene mainScene = new Scene(root);
		Button pack = new Button("Упаковать");
		pack.setDisable(true);
		root.add(pack,0,1);

		Button unpack = new Button("Распаковать");
		unpack.setDisable(true);
		root.add(unpack, 1,1);
		box = new ListView<>();
		mainStage.setWidth(300);
		mainStage.setHeight(400);

		curDirectory = new File(".").getAbsoluteFile().getParentFile();

		box.getItems().addAll(curDirectory);

		File files[] = curDirectory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) box.getItems().add(file);
		}
		for (File file : files) {
			if (!file.isDirectory()) box.getItems().add(file);
		}

		//box.seton


		box.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
			@Override
			public ListCell<File> call(ListView<File> param) {
				return new ListCell<File>(){
					@Override
					protected void updateItem(File t, boolean bln){
						super.updateItem(t, bln);
						if (t!= null){

							if(t.isDirectory())
								setText("/" + t.getName());
							else setText(t.getName());
							if(t.equals(curDirectory)){
								setText("..");
							}
						}

					}
				};
			}
		});

//		box.converterProperty().set(new StringConverter<File>() {
//			@Override
//			public String toString(File object) {
//				return object.getName();
//			}
//
//			@Override
//			public File fromString(String string) {
//				return new File(string);
//			}
//		});
		ColumnConstraints columnConstraints = new ColumnConstraints(30,10000, Double.MAX_VALUE);


		RowConstraints rowConstraints = new RowConstraints(35, 10000, Double.MAX_VALUE);
		System.out.println(new File(".").list()[0]);
		root.getColumnConstraints().addAll(columnConstraints,columnConstraints,columnConstraints);
		root.getRowConstraints().add(0,rowConstraints);
		root.getRowConstraints().addAll(new RowConstraints(25));
		root.add(box,0,0,3,1);

		box.getSelectionModel().select(0);
		box.refresh();
		box.setOnMouseClicked(event -> {
			ListView<File> source = (ListView<File>) event.getSource();
			source.refresh();
			File selected = source.getSelectionModel().getSelectedItem();
			if(selected == null) return;
			System.out.println(selected.getAbsolutePath());
			if(selected.isDirectory()){
				source.refresh();
				if(curDirectory.equals(selected)){
					if (curDirectory.getParentFile() == null) return;
					curDirectory = curDirectory.getParentFile();
				}else {
					curDirectory = selected;
				}
				refresh();
				pack.setDisable(true);
				unpack.setDisable(true);
				return;
			}
			Pattern p = Pattern.compile(".*\\.arch");
			Matcher m = p.matcher(selected.getName());
		    if(m.matches()){
			    System.out.println("lala");
			    pack.setDisable(true);
			    unpack.setDisable(false);
		    }else {
			    pack.setDisable(false);
			    unpack.setDisable(true);
		    }
		//	refresh();
			source.refresh();

			source.setItems(source.getItems());
		});

		pack.setOnAction(event -> {
			Archiver.archivate(box.getSelectionModel().getSelectedItem());
			refresh();
		});
		unpack.setOnAction(event -> {
			Archiver.dearchivate(box.getSelectionModel().getSelectedItem());
			refresh();
		});

		Button refreshButton = new Button("Обновить");
		refreshButton.setOnAction(event -> refresh());

		root.add(refreshButton, 2, 1);


		mainStage.setScene(mainScene);
		mainStage.show();

		//box.show();
	}

	void refresh(){
		File infiles[] = curDirectory.listFiles();
		//File cur = box.getSelectionModel().getSelectedItem();
		box.getItems().clear();
		if(curDirectory.getParentFile() != null)
			box.getItems().addAll(curDirectory);
		for (File infile : infiles) {
			if (infile.isDirectory()) box.getItems().add(infile);
		}
		for (File infile : infiles) {
			if (!infile.isDirectory()) box.getItems().add(infile);
		}
		//if (cur != null) box.getSelectionModel().select(cur);
	}
}
