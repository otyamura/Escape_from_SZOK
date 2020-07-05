import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.animation.AnimationTimer;
//効果音再生のためのもの
import java.io.File;
import javafx.scene.media.AudioClip;
import java.net.MalformedURLException;

public class MoveChara {
    public static final int TYPE_DOWN  = 0;
    public static final int TYPE_LEFT  = 1;
    public static final int TYPE_RIGHT = 2;
    public static final int TYPE_UP    = 3;

    private final String[] dirStrings  = { "d", "l", "r", "u" };
    private final int[][]  dirInt      = {{1,0},{0,-1},{0,1},{-1,0}};
    private final String[] kindStrings = { "1", "2", "3" };
    private final String pngPathBefore = "png/main/outputHead";
    private final String pngPathAfter  = ".png";

    private int posX;
    private int posY;

    private MapData mapData;

    private Image[][] charaImages;
    private ImageView[] charaImageViews;
    private ImageAnimation[] charaImageAnimations;

    private int count   = 0;
    private int diffx   = 1;
    private int charaDir;

    private int countItem = 0;
    public AudioClip se;

    private int pastPosX = 0;
    private int pastPosY = 0;
    private int pastCharaDir = TYPE_DOWN;




    //キャラインスタンス生成時に実行
    MoveChara(int startX, int startY, MapData mapData){
        this.mapData = mapData;

        charaImages = new Image[4][3];
        charaImageViews = new ImageView[4];
        charaImageAnimations = new ImageAnimation[4];

        for (int i=0; i<4; i++) {
            charaImages[i] = new Image[3];
            for (int j=0; j<3; j++) {
                charaImages[i][j] = new Image(pngPathBefore + dirStrings[i] + kindStrings[j] + pngPathAfter);
            }
            charaImageViews[i] = new ImageView(charaImages[i][0]);
            charaImageAnimations[i] = new ImageAnimation( charaImageViews[i], charaImages[i] );
        }

        //キャラ初期位置
        posX = startX;
        posY = startY;

        //キャラの方向設定
        setCharaDir(TYPE_DOWN);
    }


    public void changeCount(){
        count = count + diffx;
        if (count > 2) {
            count = 1;
            diffx = -1;
        } else if (count < 0){
            count = 1;
            diffx = 1;
        }
    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return posY;
    }

    public int getPastPosX(){
        return pastPosX;
    }

    public int getPastPosY(){
        return pastPosY;
    }
    public int getPastCharaDir(){
        return pastCharaDir;
    }

    public int getCountItem(){
        return countItem;
    }

    public int getCharaDir(){
        return charaDir;
    }

    public void infinityItem(){
        countItem = 10000;
    }

    public void setCharaDir(int cd){
        pastCharaDir = charaDir;
        charaDir = cd;
        for (int i=0; i<4; i++) {
            if (i == charaDir) {
                charaImageAnimations[i].start();
            } else {
                charaImageAnimations[i].stop();
            }
        }
    }

    //移動できるか確認
    public boolean canMove(int dx, int dy){
        if (mapData.getMap(posX+dx, posY+dy) == MapData.TYPE_WALL){
            return false;
        } else if(mapData.getMap(posX+dx, posY+dy) == MapData.TYPE_REPORT1){
            return false;
        } else{
            return true;
        }
    }


    //キャラを移動させる
    public boolean move(int dx, int dy){
        if (canMove(dx,dy)){
            pastPosX = posX;
            pastPosY = posY;

            posX += dx;
            posY += dy;
            takeItem();
            return true;
        }else {
            return false;
        }
    }

    // //キャラを前に移動
    // public void moveFront(){
    //     move(dirInt[charaDir][1], dirInt[charaDir][0]);
    // }
    //
    // //キャラを後ろに移動
    // public void moveBack(){
    //     move()
    // }
    //
    // //時計回り
    // public void turnRight(){
    //     if(charaDir == TYPE_DOWN){
    //         setCharaDir(TYPE_LEFT);
    //     }else if(charaDir == TYPE_LEFT){
    //         setCharaDir(TYPE_UP);
    //     }else if(charaDir == TYPE_UP){
    //         setCharaDir(TYPE_RIGHT);
    //     }else{
    //         setCharaDir(TYPE_DOWN);
    //     }
    // }
    //
    // public void turnLeft(){
    //     if(charaDir == TYPE_DOWN){
    //         setCharaDir(TYPE_RIGHT);
    //     }else if(charaDir == TYPE_RIGHT){
    //         setCharaDir(TYPE_UP);
    //     }else if(charaDir == TYPE_UP){
    //         setCharaDir(TYPE_LEFT);
    //     }else{
    //         setCharaDir(TYPE_DOWN);
    //     }
    // }


    //アイテムを拾う
    public void takeItem(){
        if(mapData.getMap(posX, posY) == MapData.TYPE_ITEM){
            //アイテムを拾ったときの効果音
            se = new AudioClip(new File("BGM/TakeItem.wav").toURI().toString());
            se.play();

            mapData.setMap(posX, posY, MapData.TYPE_NONE);
            mapData.setImageViews();
            countItem++;
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
