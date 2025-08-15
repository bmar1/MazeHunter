package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import view.HomeScreen;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application{

	public static void main(String[] args) {
		//Call and run the homescreen.
		HomeScreen homeScreen = new HomeScreen();
		homeScreen.main(args);
		
	}

	@Override
	public void start(Stage arg0) throws Exception {
		//run entry frame
				
		
	}
}