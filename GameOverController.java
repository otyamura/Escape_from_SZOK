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
import javafx.scene.input.MouseEvent;

//音楽を再生するためのもの
import java.io.File;
import javafx.scene.media.AudioClip;
import java.net.MalformedURLException;

public class GameOverController implements Initializable {

  public AudioClip bgm;
  public AudioClip se;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

    // bgmを再生
    bgm = new AudioClip(new File("BGM/GameOver.wav").toURI().toString());
    bgm.setCycleCount(AudioClip.INDEFINITE); // 無限ループ
    bgm.setVolume(0.1);
    bgm.play();

  }

  // 迷路のfxmlを読み込む
  public void funcMouseAction(MouseEvent event) {
    try {
      bgm.stop();
      // ボタンを押して画面が切り替わるときの効果音
      se = new AudioClip(new File("BGM/PushButton.wav").toURI().toString());
      se.setVolume(0.1);
      se.play();

      MapGame.changeSceneByFXML("fxml/Start.fxml");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
