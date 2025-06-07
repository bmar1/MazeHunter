package view;

import model.*;
import controller.GameController;
import controller.PlayerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GameScreen extends Application {

    GameController gameController;
    PlayerController playerController;
    
    public void start(Stage primaryStage) throws Exception{
    	try {
    	StackPane root = new StackPane();
    	GridPane grid = new GridPane();
    	Group gameLayer = new Group();
    	gameLayer.getChildren().add(grid);

    	root.getChildren().add(gameLayer);
    	StackPane.setAlignment(gameLayer, Pos.CENTER);
		Scene scene = new Scene(root, 980, 980);
		scene.setFill(Color.BLACK);
		
		root.prefWidthProperty().bind(scene.widthProperty());
		root.prefHeightProperty().bind(scene.heightProperty());
		scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
		primaryStage.setScene(scene);
		
		Image Icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
		primaryStage.setTitle("MazeHunter");
		primaryStage.getIcons().add(Icon);
		primaryStage.setResizable(false);
		
		gameController = new GameController(root, gameLayer, grid);
		playerController = new PlayerController(root, gameLayer, grid, gameController);
		
		primaryStage.show();
		
	

		
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    	
   public static void main(String[] args) {
    		launch(args);
    }
}
