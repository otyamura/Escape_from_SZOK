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
//音楽を再生するためのもの

import java.io.IOException;
import java.io.File;

import javafx.scene.media.AudioClip;

import java.net.MalformedURLException;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.scene.input.MouseEvent;

public class MapGameController implements Initializable {
  public MapData mapData;
  public MoveChara chara;
  public MoveEnemy enemy;
  public MoveOthers gate;
  public GridPane mapGrid;
  public ImageView[] mapImageViews;
  public Label item;
  public Label playerhp;
  public int hp = 100;
  public Label playerscore;
  public Label labelStage;

  public AudioClip bgm;
  public AudioClip se1, se2, se3, se4;

  private int clearCount = 1;
  private int intStage = 1;
  public Timeline t;
  public int score = 100000;
  public int i;

  @FXML
  public Label time;

  @FXML
  public Label textLabel;

  @FXML
  public ImageView enemyImage;

  public int secondCount = 0;
  public int minuteCount = 0;
  public int enemyNum = 0;
  public boolean alrTalk = false;
  public boolean clearTalk = false;

  public int totalTani = 0;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

    t = new Timeline(
        /* 1000 milli sec 間隔で実行 */
        new KeyFrame(Duration.millis(1000), event -> {
          secondCount++;
          if (secondCount > 59) {
            secondCount = 0;
            minuteCount++;

          }
          time.setText(String.format("%02d:%02d", minuteCount, secondCount));
          if (!(enemy.getIsAlly())) {
            enemy.move();
          } else {
            if (alrTalk) {
            } else {
              allyTalk(enemyNum);
              alrTalk = true;
            }
          }
          mapPrint(chara, mapData, enemy, gate);
        }));
    /* 何回アニメーションを実行するかの設定(ここでは無限ループ) */
    t.setCycleCount(Animation.INDEFINITE);
    t.play();

    mapData = new MapData(21, 15);
    chara = new MoveChara(1, 1, mapData);
    if (mapData.getMap(19, 12) == MapData.TYPE_WALL) {
      enemy = new MoveEnemy(18, 13, mapData, chara, "ark3", false);
      enemy.setCharaDir(MoveEnemy.TYPE_LEFT);
    } else {
      enemy = new MoveEnemy(19, 12, mapData, chara, "ark3", false);
    }
    gate = new MoveOthers(19, 13, mapData, "gate");
    // bgmを再生

    bgm = new AudioClip(new File("BGM/BGM_0.wav").toURI().toString());

    bgm.setCycleCount(AudioClip.INDEFINITE); // 無限ループ

    bgm.play();

    // mapData.putEnemy(enemy.getPosX(),enemy.getPosY());
    // mapGroups = new Group[mapData.getHeight()*mapData.getWidth()];
    mapImageViews = new ImageView[mapData.getHeight() * mapData.getWidth()];
    // enemys = new MoveEnemy[4];
    // enemys[1] =

    // マップデータを1次元配列で管理
    for (int y = 0; y < mapData.getHeight(); y++) {
      for (int x = 0; x < mapData.getWidth(); x++) {
        int index = y * mapData.getWidth() + x;
        mapImageViews[index] = mapData.getImageView(x, y);
      }
    }
    mapPrint(chara, mapData, enemy, gate);
    changeEnemy(enemyNum);

    /* タイマーの設定 */
  }

  // 一文字ずつ出力するメソッド
  public void showPerText(String showText, int i) {
    textLabel.setText(showText.substring(0, i));
  }

  public void showText(String showText) {
    i = 0;
    Timeline timeline = new Timeline(new KeyFrame(new Duration(100), (event) -> {
      i += 1;
      showPerText(showText, i);
      if (enemyNum == 3) {
        AudioClip sans = new AudioClip(new File("BGM/sans.wav").toURI().toString());
        sans.play();

      }
    }));
    timeline.setCycleCount(showText.length());
    timeline.play();
  }

  // マップ上にブロックを出力する
  public void mapPrint(MoveChara c, MapData m, MoveEnemy e, MoveOthers g) {
    // キャラが今いる場所
    int cx = c.getPosX();
    int cy = c.getPosY();

    // 敵が今いる場所
    int ex = e.getPosX();
    int ey = e.getPosY();

    // 扉の場所
    int gx = g.getPosX();
    int gy = g.getPosY();

    // 味方１
    // int a1x = a1.getPosX();
    // int a1y = a1.getPosY();

    // System.out.println(enemy.getPosX()+":"+enemy.getPosY());
    // マップの出力をいったんリセット
    mapGrid.getChildren().clear();

    // マップにブロック出力
    for (int y = 0; y < mapData.getHeight(); y++) {
      for (int x = 0; x < mapData.getWidth(); x++) {
        int index = y * mapData.getWidth() + x;
        if (x == cx && y == cy) {
          mapGrid.add(c.getCharaImageView(), x, y);
          // mapGrid.add(a1.getCharaImageView(), x, y);
        } else if (x == ex && y == ey) {
          mapGrid.add(e.getCharaImageView(), x, y);
        } else if (x == gx && y == gy) {
          mapGrid.add(g.getObjectImageView(), x, y);
        } else {
          mapImageViews[index] = mapData.getImageView(x, y);
          mapGrid.add(mapImageViews[index], x, y);
        }
      }
    }
    printItemNumber();
    printClearCount();
    setNumberLabel();
  }

  // スクリーン上のボタンの動作
  // ボタンが押されたらスタート画面に戻る
  public void func1ButtonAction(ActionEvent event) {
    try {
      t.stop();
      bgm.stop();
      // ボタンを押して画面が切り替わるときの効果音
      se1 = new AudioClip(new File("BGM/PushFunc.wav").toURI().toString());
      se1.setVolume(1.0);
      se1.play();

      MapGame.changeSceneByFXML("fxml/Start.fxml");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void func2ButtonAction(ActionEvent event) {
    try {
      // ボタンを押して画面が切り替わるときの効果音
      se1 = new AudioClip(new File("BGM/PushFunc.wav").toURI().toString());
      se1.setVolume(1.0);
      se1.play();

      t.stop();
      bgm.stop();
      MapGame.changeSceneByFXML("fxml/GameClear.fxml");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void func3ButtonAction(ActionEvent event) {
    try {
      t.stop();
      bgm.stop();
      // ボタンを押して画面が切り替わるときの効果音
      se1 = new AudioClip(new File("BGM/PushFunc.wav").toURI().toString());
      se1.setVolume(1.0);
      se1.play();

      MapGame.changeSceneByFXML("fxml/GameOver.fxml");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void func4ButtonAction(ActionEvent event) {
    try {
      // MapGame.showSecondWindow();
      se1 = new AudioClip(new File("BGM/PushFunc.wav").toURI().toString());
      se1.setVolume(1.0);
      se1.play();

      // NewStageTest nst = new NewStageTest( MapGame.stage );

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean canAtk = true;

  public void func5ButtonAction(ActionEvent event) {

    if (canAtk) {
      canAtk = false;
      // int[][] dirInt = {{1,0},{0,-1},{0,1},{-1,0}};
      //// 周りに敵がいるか
      int cx = chara.getPosX();
      int cy = chara.getPosY();
      int ex = enemy.getPosX();
      int ey = enemy.getPosY();
      int dx = cx, dy = cy;
      int atkDir = chara.getCharaDir();
      switch (atkDir) {
        case 0:// down
          dy++;
          break;
        case 1:// left
          dx--;
          break;
        case 2:// right
          dx++;
          break;
        case 3:// up
          dy--;
          break;
      }
      int oldType = mapData.getMap(dx, dy);
      mapData.setMap(dx, dy, MapData.TYPE_REPORT1);
      mapData.setImageViews();
      mapPrint(chara, mapData, enemy, gate);
      final int tx = dx, ty = dy;// なんかわからんけど置き換えなきゃいけない
      Timeline atk = new Timeline(
          /* 300 milli sec 間隔で実行 */
          new KeyFrame(new Duration(2000), (atkEvent) -> {
            mapData.setMap(tx, ty, oldType);
            mapData.setImageViews();
            mapPrint(chara, mapData, enemy, gate);
            canAtk = true;
          }));
      atk.setCycleCount(1);
      atk.play();
    }
  }

  // キーアクションの設定

  int count = 0;
  boolean goalFlag = true;

  public void keyAction(KeyEvent event) {
    KeyCode key = event.getCode();
    if (key == KeyCode.DOWN) {
      downButtonAction();
      count++;
      score -= 100;
    } else if (key == KeyCode.RIGHT) {
      rightButtonAction();
      count++;
      score -= 100;
    } else if (key == KeyCode.UP) {
      upButtonAction();
      count++;
      score -= 100;
    } else if (key == KeyCode.LEFT) {
      leftButtonAction();
      count++;
      score -= 100;
    }

    // score表示
    playerscore.setText("score: " + score + "/100000");

    if (count <= 0) {
      System.out.println("Game Over");
      // func3ButtonAction();と同じ処理
      try {
        bgm.stop();
        // ボタンを押して画面が切り替わるときの効果音
        se2 = new AudioClip(new File("BGM/CountOver.wav").toURI().toString());
        se2.setVolume(1.0);
        se2.play();

        MapGame.changeSceneByFXML("fxml/GameOver.fxml");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (canGoal()) {
      totalTani += chara.getCountItem();
      System.out.println(totalTani);
      if (intStage >= 4) {
        try {
          // ボタンを押して画面が切り替わるときの効果音
          se1 = new AudioClip(new File("BGM/PushFunc.wav").toURI().toString());
          se1.setVolume(1.0);
          se1.play();
          clearTalk = true;
          talkGoal();
          t.stop();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        intStage++;
        enemyNum++;
        alrTalk = false;
        mapData = new MapData(21, 15);
        chara = new MoveChara(1, 1, mapData);
        if (mapData.getMap(19, 12) == MapData.TYPE_WALL) {
          if (intStage == 2) {
            enemy = new MoveEnemy(18, 13, mapData, chara, "ako3", false);
            // ally1 = new MoveEnemy(1, 1, mapData, chara, "monster", true);
            enemy.setCharaDir(MoveEnemy.TYPE_LEFT);
          } else if (intStage == 3) {
            enemy = new MoveEnemy(18, 13, mapData, chara, "myzk3", false);
            enemy.setCharaDir(MoveEnemy.TYPE_LEFT);
            mapData.putItem();
            mapData.putItem();
            mapData.setImageViews();
          } else if (intStage == 4) {
            enemy = new MoveEnemy(18, 13, mapData, chara, "monster", false);
          }
        } else {
          if (intStage == 2) {
            enemy = new MoveEnemy(19, 12, mapData, chara, "ako3", false);
            // ally1 = new MoveEnemy(1, 1, mapData, chara, "monster", true);
          } else if (intStage == 3) {
            enemy = new MoveEnemy(19, 12, mapData, chara, "myzk3", false);
            mapData.putItem();
            mapData.putItem();
            mapData.setImageViews();
          } else if (intStage == 4) {
            enemy = new MoveEnemy(19, 12, mapData, chara, "monster", false);
          }
        }
        gate = new MoveOthers(19, 13, mapData, "gate");
        goalFlag = true;
        // mapGroups = new Group[mapData.getHeight()*mapData.getWidth()];
        mapImageViews = new ImageView[mapData.getHeight() * mapData.getWidth()];

        // マップデータを1次元配列で管理
        for (int y = 0; y < mapData.getHeight(); y++) {
          for (int x = 0; x < mapData.getWidth(); x++) {
            int index = y * mapData.getWidth() + x;
            mapImageViews[index] = mapData.getImageView(x, y);
          }
        }
        mapPrint(chara, mapData, enemy, gate);
        changeEnemy(enemyNum);

      }
    }
  }

  public boolean canGoal() {
    if (chara.getCountItem() >= 3 && goalFlag) {
      se4 = new AudioClip(new File("BGM/GateOpen.wav").toURI().toString());
      se4.setVolume(1.0);
      se4.play();
      System.out.println("can goal");
      goalFlag = false;
      gate.setCharaDir();
    }
    if (chara.getCountItem() >= 3 && chara.getPosX() == 19 && chara.getPosY() == 13) {
      System.out.println("goal");
      return true;
    } else {
      return false;
    }
  }

  // コマンドライン上の表記
  public void outputAction(String actionString) {
    System.out.println("Select Action: " + actionString);
  }

  public void printItemNumber() {
    item.setText("you have" + chara.getCountItem() + "item(s)");
  }

  public void printClearCount() {
    labelStage.setText("STAGE " + intStage);
  }

  // hpを表示させるメソッド
  public void setNumberLabel() {
    if (chara.getPosX() == enemy.getPosX() && chara.getPosY() == enemy.getPosY()) {
      hp -= 10;
      se3 = new AudioClip(new File("BGM/Battle.wav").toURI().toString());
      se3.setVolume(1.0);
      se3.play();
    }
    playerhp.setText("HP: " + hp + "/100");

    // ゲームオーバー
    try {
      if (hp <= 0 || score <= 0) {
        t.stop();
        bgm.stop();
        MapGame.changeSceneByFXML("fxml/GameOver.fxml");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // downButtonActionの呼び出し
  public void downButtonAction() {
    outputAction("DOWN");
    chara.setCharaDir(MoveChara.TYPE_DOWN);
    chara.move(0, 1);
    if (enemy.getIsAlly()) {
      enemy.move();
    }
    // deleteEnemy();
    // enemy.move();
    // setEnemy();
    mapPrint(chara, mapData, enemy, gate);
  }

  public void downButtonAction(ActionEvent event) {
    downButtonAction();
  }

  // rightButtonActionの呼び出し
  public void rightButtonAction() {
    outputAction("RIGHT");
    chara.setCharaDir(MoveChara.TYPE_RIGHT);
    chara.move(1, 0);
    // deleteEnemy();
    if (enemy.getIsAlly()) {
      enemy.move();
    }
    // enemy.move();
    // setEnemy();
    mapPrint(chara, mapData, enemy, gate);
  }

  public void rightButtonAction(ActionEvent event) {
    rightButtonAction();
  }

  // upButtonActionの呼び出し
  public void upButtonAction() {
    outputAction("UP");
    chara.setCharaDir(MoveChara.TYPE_UP);
    chara.move(0, -1);
    // deleteEnemy();
    // enemy.move();
    if (enemy.getIsAlly()) {
      enemy.move();
    }
    // setEnemy();
    mapPrint(chara, mapData, enemy, gate);
  }

  public void upButtonAction(ActionEvent event) {
    upButtonAction();
  }

  // leftButtonActionの呼び出し
  public void leftButtonAction() {
    outputAction("LEFT");
    chara.setCharaDir(MoveChara.TYPE_LEFT);
    chara.move(-1, 0);
    // deleteEnemy();
    if (enemy.getIsAlly()) {
      enemy.move();
    }
    // enemy.move();
    // setEnemy();
    mapPrint(chara, mapData, enemy, gate);
  }

  public void leftButtonAction(ActionEvent event) {
    leftButtonAction();
  }

  public void changeEnemy(int type) {
    String eName = "ark";
    switch (type) {
      case 0:
        eName = "ark";
        showText("今週のクイズを開始するので筆記用具を\n除いて鞄の中にしまって下さい。");
        break;
      case 1:
        eName = "ako";
        showText("あずにゃんprpr");
        break;
      case 2:
        eName = "miya";
        showText("それでは前回の分の課題を提出してく\nださいねー");
        break;
      case 3:
        eName = "kgr";
        showText("ﾎﾞｿﾎﾞｿ\n（声が小さすぎて何を言ってるか\n聞き取れない）");
        break;
    }
    Image img = new Image(new File("png/" + eName + ".png").toURI().toString());
    enemyImage.setImage(img);

  }

  public void allyTalk(int type) {
    switch (type) {
      case 0:
        showText("単位を認めます。共にこの過酷なSZOK\n大学から脱出しましょう。");
        break;
      case 1:
        showText("あずにゃんprpr");
        break;
      case 2:
        showText("二回言ったんだけどまだいましたね。\nギリギリセーフとしましょう");
        break;
      case 3:
        showText("ﾎﾞｿﾎﾞｿ\n（声が小さすぎて何を言ってるか\n聞き取れない）");
        break;
    }

  }

  public void funcMouseAction(MouseEvent event) {
    if (clearTalk) {
      t.stop();
      bgm.stop();
      try {
        clearTalk = false;
        MapGame.changeSceneByFXML("fxml/GameClear.fxml");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void talkGoal() {
    float gpa = (float) totalTani / 18 * 100;
    String mes = "\n素晴らしい成績だ。\n〇〇研究室にこないか？";
    System.out.println(gpa);
    showText("あなたのGPAは" + gpa + mes);
  }

}