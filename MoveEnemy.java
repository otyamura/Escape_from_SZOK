import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.animation.AnimationTimer;

//http://momope8.blog67.fc2.com/blog-entry-2.htmlこれ使う

//効果音再生のためのもの
import java.io.File;
import javafx.scene.media.AudioClip;
import java.net.MalformedURLException;

public class MoveEnemy {
    public static final int TYPE_DOWN  = 0;
    public static final int TYPE_LEFT  = 1;
    public static final int TYPE_RIGHT = 2;
    public static final int TYPE_UP    = 3;

    private final String[] dirStrings  = { "d", "l", "r", "u" };
    private final String[] kindStrings = { "1", "2", "3" };
    //ひとまず宮崎先生だけ
    private final String pngPathBefore;
    private final String pngPathAfter  = ".png";

    private int posX;
    private int posY;

    private int pastPosX;
    private int pastPosY;

    private MapData mapData;
    private MoveChara chara;

    private Image[][] charaImages;
    private ImageView[] charaImageViews;
    private ImageAnimation[] charaImageAnimations;

    private int charaDir;

    private boolean isAlly;

    public AudioClip se;

    private int costMap[][] = new int[15][21];
    public int[][] dl = {{0,1},{-1,0},{1,0},{0,-1}};


    //キャラインスタンス生成時に実行
    MoveEnemy(int startX, int startY, MapData mapData, MoveChara chara, String name, boolean allyOrNot){
        this.mapData = mapData;
        this.chara = chara;
        this.pngPathBefore = "png/" + name + "/" + name;
        this.isAlly = allyOrNot;

        charaImages = new Image[4][3];
        charaImageViews = new ImageView[4];
        charaImageAnimations = new ImageAnimation[4];

        if(name.equals("myzk3")){
            for (int i=0; i<4; i++) {
                charaImages[i] = new Image[3];
                for(int j=0;j < 3; j++){
                    charaImages[i][j] = new Image(pngPathBefore +  pngPathAfter);
                }
                charaImageViews[i] = new ImageView(charaImages[i][0]);
                charaImageAnimations[i] = new ImageAnimation( charaImageViews[i], charaImages[i] );
            }
        }else{
            for (int i=0; i<4; i++) {
                charaImages[i] = new Image[3];
                for(int j=0;j < 3; j++){
                    charaImages[i][j] = new Image(pngPathBefore + dirStrings[i] + kindStrings[j] + pngPathAfter);
                }
                charaImageViews[i] = new ImageView(charaImages[i][0]);
                charaImageAnimations[i] = new ImageAnimation( charaImageViews[i], charaImages[i] );
            }
        }
        //キャラ初期位置
        posX = startX;
        posY = startY;
        pastPosX = posX;
        pastPosY = posY;

        //キャラの方向設定
        setCharaDir(TYPE_UP);
    }

    public int getPosX(){
        return posX;
    }
    public int getPosY(){
        return posY;
    }
    public void setPosX(int x){
        posX = x;
    }
    public void setPosY(int y){
        posY = y;
    }
    public int getPastPosX(){
        return pastPosX;
    }
    public int getPastPosY(){
        return pastPosY;
    }

    public void setCharaDir(int cd){
        charaDir = cd;
        for (int i=0; i<4; i++) {
            if (i == charaDir) {
                charaImageAnimations[i].start();
            } else {
                charaImageAnimations[i].stop();
            }
        }
    }
    public void getReport(){
        if(mapData.getMap(posX, posY) == MapData.TYPE_REPORT1){
            //アイテムを拾ったときの効果音
            se = new AudioClip(new File("BGM/GetProf.wav").toURI().toString());
            se.play();
            System.out.println("get");

            mapData.setMap(posX, posY, MapData.TYPE_NONE);
            mapData.setImageViews();
            setIsAlly(true);
        }
    }
    public void setIsAlly(boolean boo){
        isAlly = boo;
        System.out.println("教授が仲間になった！！");
    }
    public boolean getIsAlly(){
        return isAlly;
    }

    //敵キャラを移動させる
    public void move(){
        if(pastPosX != posX || pastPosY != posY){
            pastPosX = posX;
            pastPosY = posY;
        }
        if(isAlly){
            //味方になった時の移動
            posX = chara.getPastPosX();
            posY = chara.getPastPosY();
            setCharaDir(chara.getPastCharaDir());
        }else{
            //敵の時の移動
            //主人公の位置を特定
            int aPosx = chara.getPosX();
            int aPosy = chara.getPosY();

            //マップデータ初期化
            for(int i = 0; i<15; i++){
                for(int n = 0; n<21; n++){
                    costMap[i][n] = 0;
                }
            }

            //その位置から数字を数えていく
            setCost(aPosx, aPosy, 1);

            //自分の周りの数字を評価して小さい方へ移動
            for(int i=0; i<4; i++){
                int moveX = posX + dl[i][0];
                int moveY = posY + dl[i][1];
                if(costMap[moveY][moveX] < costMap[posY][posX] && costMap[moveY][moveX] != 0){
                    if(aPosx == moveX && aPosy == moveY){
                        return;
                    }
                    posX = moveX;
                    posY = moveY;
                    setCharaDir(i);
                    getReport();
                    return;
                }
            }
        }
    }
    public void setCost(int cx, int cy, int cost){

        //データ代入
        int c = mapData.getMap(cx, cy);
        if(c == MapData.TYPE_WALL){
            return;
        }
        if(costMap[cy][cx] < cost && costMap[cy][cx] != 0){
            return;
        }
        costMap[cy][cx] = cost;
        for(int i=0; i<4; i++){
            setCost(cx+dl[i][0], cy+dl[i][1], cost+1);
        }
    }




    //ここら辺は主に猫の動き(よくわかってない)
    public ImageView getCharaImageView(){
        return charaImageViews[charaDir];
    }

    //ここら辺は主に猫の動き(よくわかってない)
    private class ImageAnimation extends AnimationTimer {
        // アニメーション対象ノード
        private ImageView   charaView     = null;
        private Image[]     charaImages   = null;
        private int         index       = 0;

        private long        duration    = 500 * 1000000L;   // 500[ms]
        private long        startTime   = 0;

        private long count = 0L;
        private long preCount;
        private boolean isPlus = true;

        //オーバーライドしてるimageanimationのインスタンスメソッド
        public ImageAnimation( ImageView charaView , Image[] images ) {
            this.charaView   = charaView;
            this.charaImages = images;
            this.index      = 0;
        }

        @Override
        public void handle( long now ) {
            if( startTime == 0 ){ startTime = now; }

            preCount = count;
            count  = ( now - startTime ) / duration;
            if (preCount != count) {
                if (isPlus) {
                    index++;
                } else {
                    index--;
                }
                if ( index < 0 || 2 < index ) {
                    index = 1;
                    isPlus = !isPlus; // true == !false, false == !true
                }
                charaView.setImage(charaImages[index]);
            }
        }
    }
}
