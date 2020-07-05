import java.util.Optional;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.fxml.FXML;

//音楽を再生するためのもの
import java.io.File;
import javafx.scene.media.AudioClip;
import java.net.MalformedURLException;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class ArkController implements Initializable {

  // public AudioClip bgm;
  // public AudioClip se;

  public int i;
  @FXML
  public Label textLabel;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

    // bgmを再生
    // bgm = new AudioClip(new File("BGM/GameStart.wav").toURI().toString());
    // bgm.setCycleCount(AudioClip.INDEFINITE); //無限ループ
    // bgm.setVolume(0.3);
    // bgm.play();

  }

  public void funcButtonAction(ActionEvent event) {
    showText("ボタンが押されました");
  }

  public void showPerText(String showText, int i) {
    textLabel.setText(showText.substring(0, i));
  }

  public void showText(String showText) {
    i = 0;
    Timeline timeline = new Timeline(new KeyFrame(new Duration(100),
        /*
         * new EventHandler<ActionEvent>(){
         *
         * @Override public void handle(ActionEvent event){ i+=1; showPerText(showText,
         * i); } }
         */
        (event) -> {
          i += 1;
          showPerText(showText, i);
        }));
    timeline.setCycleCount(showText.length());
    timeline.play();
  }

}
