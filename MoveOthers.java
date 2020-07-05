import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.animation.AnimationTimer;

public class MoveOthers {

    private final String[] dirStrings  = {"d", "l", "r", "u"};
    private final String[] kindStrings = {"1", "2", "3", "4"};
    //ひとまず宮崎先生だけ
    private final String pngPathBefore = "png/gate/gate";
    private final String pngPathAfter  = ".png";

    private int posX;
    private int posY;

    private String name;

    private MapData mapData;

    private Image[][] objectImages;
    private ImageView[] objectImageViews;
    private ImageAnimation[] objectImageAnimations;

    private int charaDir;
    public int[][] dl = {{0,1},{-1,0},{1,0},{0,-1}};




    //キャラインスタンス生成時に実行
    MoveOthers(int x, int y, MapData mapData, String othersName){
        this.mapData = mapData;
        this.name = othersName;

        objectImages = new Image[1][4];
        objectImageViews = new ImageView[1];
        objectImageAnimations = new ImageAnimation[1];

        //for (int i=0; i<4; i++) {
        int i = 0;
            objectImages[i] = new Image[4];
            for(int j=0;j < 4; j++){
                objectImages[i][j] = new Image(pngPathBefore /*+ dirStrings[i]*/ + kindStrings[j] + pngPathAfter);
            }
            objectImageViews[i] = new ImageView(objectImages[i][0]);
            objectImageAnimations[i] = new ImageAnimation(objectImageViews[i], objectImages[i] );
        //}

        posX = x;
        posY = y;
        // setCharaDir();

    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return posY;
    }

    public void setCharaDir(){
            objectImageAnimations[0].start();
    }


    //ここら辺は主に猫の動き(よくわかってない)
    public ImageView getObjectImageView(){
        return objectImageViews[charaDir];
    }

    //ここら辺は主に猫の動き(よくわかってない)
    private class ImageAnimation extends AnimationTimer {
        // アニメーション対象ノード
        private ImageView   objectView     = null;
        private Image[]     objectImages   = null;
        private int         index       = 0;

        private long        duration    = 500 * 1000000L;   // 500[ms]
        private long        startTime   = 0;

        private long count = 0L;
        private long preCount;
        private boolean isPlus = true;

        //オーバーライドしてるimageanimationのインスタンスメソッド
        public ImageAnimation( ImageView objectView , Image[] images ) {
            this.objectView   = objectView;
            this.objectImages = images;
            this.index      = 0;
        }

        @Override
        public void handle( long now ) {
            if( startTime == 0 ){ startTime = now; }

            preCount = count;
            count  = ( now - startTime ) / duration;
            if (preCount != count) {
                index++;
                if (index == 3){
                    stop();
                }
                objectView.setImage(objectImages[index]);

            }
        }
    }
}
