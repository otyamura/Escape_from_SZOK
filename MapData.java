import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public class MapData {
  public static final int TYPE_NONE = 0;
  public static final int TYPE_WALL = 1;
  public static final int TYPE_ITEM = 2;
  public static final int TYPE_GOAL = 3;
  public static final int TYPE_REPORT1 = 4;
  public static final int TYPE_REPORT2 = 5;
  private static final String mapImageFiles[] = { "png/SPACE.png", "png/WALL.png", "png/ITEM.png", "png/hashigo.png",
      "png/reportr1.png", "png/reportr2.png" };

  private Image[] mapImages;
  private ImageView[][] mapImageViews;
  private int[][] maps;
  private int width;
  private int height;

  // 他ファイルでMapDataインスタンスを生成した時の処理
  MapData(int x, int y) {
    mapImages = new Image[6];
    mapImageViews = new ImageView[y][x];
    for (int i = 0; i < 6; i++) {
      mapImages[i] = new Image(mapImageFiles[i]);
    }

    width = x;
    height = y;
    // typeを入れる場所
    maps = new int[y][x];

    /*
     * fillMap(MapData.TYPE_WALL); digMap(1, 3);
     */
    downMap();
    putItem();
    putGoal();
    setImageViews();
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public int getMap(int x, int y) {
    /*
     * if (x < 0 || width <= x || y < 0 || height <= y) { return -1; }
     */
    return maps[y][x];
  }

  public ImageView getImageView(int x, int y) {
    return mapImageViews[y][x];
  }

  // 引数で与えられた座標に引数で与えられた壁か道を表す数字を代入
  public void setMap(int x, int y, int type) {
    /*
     * if (x < 1 || width <= x-1 || y < 1 || height <= y-1) { return; }
     */
    maps[y][x] = type;
  }

  // それぞれの座標に対応した画像データを渡している
  public void setImageViews() {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        mapImageViews[y][x] = new ImageView(mapImages[maps[y][x]]);
      }
    }
  }

  // とりあえずすべて壁で埋める(そのあと道を掘る)
  public void fillMap(int type) {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        maps[y][x] = type;
      }
    }
  }

  public void downMap() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
          setMap(x, y, TYPE_WALL); // 外周はすべて壁
        } else {
          setMap(x, y, TYPE_NONE); // 外周以外は通路
        }
      }
    }

    Random rnd = new Random();

    for (int x = 2; x < width - 1; x += 2) {
      for (int y = 2; y < height - 1; y += 2) {
        setMap(x, y, TYPE_WALL); // 棒を立てる

        while (true) {
          // 1行目のみ上に倒せる
          int direction;
          if (y == 2) {
            direction = rnd.nextInt(4);
          } else {
            direction = rnd.nextInt(3);
          }

          // 棒を倒す方向を決める
          int wallX = x;
          int wallY = y;
          switch (direction) {
            case 0: // 右
              wallX++;
              break;
            case 1: // 下
              wallY++;
              break;
            case 2: // 左
              wallX--;
              break;
            case 3: // 上
              wallY--;
              break;
          }

          if (getMap(wallX, wallY) != TYPE_WALL) {
            setMap(wallX, wallY, TYPE_WALL);
            break;
          }
        }

      }
    }

  }

  public void digMap(int x, int y) {
    // 引数で与えられた座標の壁を壊す
    setMap(x, y, MapData.TYPE_NONE);
    // x軸とy軸の上下の四方向どちらに壁を掘るか
    int[][] dl = { { 0, 1 }, { 0, -1 }, { -1, 0 }, { 1, 0 } };
    // 変数tmpは値の一時的な置き場
    int[] tmp;

    // 変数dlの配列のrとiを入れ替える(ランダムマップ生成のため)
    for (int i = 0; i < dl.length; i++) {
      int r = (int) (Math.random() * dl.length);
      tmp = dl[i];
      dl[i] = dl[r];
      dl[r] = tmp;
    }

    // この中の再帰呼び出しのdigMapで壊す壁の座標を決定
    for (int i = 0; i < dl.length; i++) {
      int dx = dl[i][0];
      int dy = dl[i][1];
      // (x,y)の上下左右で掘れるところはあるかどうか判断
      // ２マス先が壁かどうか判断
      if (getMap(x + dx * 2, y + dy * 2) == MapData.TYPE_WALL) {
        // 同じ方向へもう１マス掘り進める(コの字のような道を作れるように)
        setMap(x + dx, y + dy, MapData.TYPE_NONE);
        // 再起呼び出し
        digMap(x + dx * 2, y + dy * 2);
      }
    }
  }

  // アイテムを置く
  public void putItem() {
    int setItemCounter = 0;
    while (setItemCounter < 3) {
      int i = (int) (Math.random() * width * height);
      int xi = i % width;
      int yi = i / width;
      if (getMap(xi, yi) == MapData.TYPE_NONE) {
        setMap(xi, yi, MapData.TYPE_ITEM);
        setItemCounter++;
      }
    }
  }

  // ゴールを置く
  public void putGoal() {
    int xGoal = 19;
    int yGoal = 13;
    setMap(xGoal, yGoal, TYPE_GOAL);
  }

  // Noneを置く
  public void putNone(int x, int y) {
    setMap(x, y, TYPE_NONE);
  }

  // 多分使ってない
  public void printMap() {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (getMap(x, y) == MapData.TYPE_WALL) {
          System.out.print("++");
        } else {
          System.out.print("  ");
        }
      }
      System.out.print("\n");
    }
  }

}
