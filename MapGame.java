import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class MapGame extends Application {
  public static Stage stage;

  @Override
  public void start(Stage primaryStage) throws IOException {
    stage = primaryStage;
    primaryStage.setTitle("MAP GAME");
    changeSceneByFXML("./fxml/Start.fxml");
  }

  // 読み込むfxmlを変えるためのもの
  public static void changeSceneByFXML(String name) throws IOException {
    changeScene(new Scene(getFXMLLoader(name).load()));
  }

  // 読み込んだら変えるやつ
  public static void changeScene(Scene s) {
    stage.setScene(s);
    stage.show();
  }

  public static Scene getCurrentScene() {
    return stage.getScene();
  }

  public static FXMLLoader getFXMLLoader(String name) {
    return new FXMLLoader(MapGame.class.getResource(name));
  }

  public static void main(String[] args) {
    launch(args);
  }

}
