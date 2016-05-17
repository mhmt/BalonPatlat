package com.mhmtozcann.balon;

import java.util.Iterator;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Balloon extends ApplicationAdapter {
    private static final String TAG ="InGame";

	public int TIME_ONE_SECOND = 1000000000;
	public int TIME_HALF_SECOND = 500000000;
    long time = 30;

	SpriteBatch batch;

	Texture img;
    boolean ingame = true,yellowShowed=false,won = false;
    boolean red= false;
    boolean green = false;
    boolean black = false;
    boolean yellow = false;
	public Array<Balon> balloons;

	int height;
    int width;
	public long lastDropTime,finishing;

    Music music,pop;
    BitmapFont font;
    int id = 0;
    int score = 0;
    int yellowid;
    int next = 100;
    int lvl = 1;
    Preferences prefs;

    @Override
	public void create () {
		batch = new SpriteBatch();
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
        System.out.println("Height: "+ height+" Width: "+width);

        music = Gdx.audio.newMusic(Gdx.files.internal("maintheme.mp3"));
        music.setVolume(0.5f);
        music.setLooping(true);

        pop = Gdx.audio.newMusic(Gdx.files.internal("balloon-pop.mp3"));
        pop.setVolume(0.5f);
        pop.setLooping(false);

       font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(2,2);

        prefs = Gdx.app.getPreferences("prefs");
		System.out.println("Ses: "+prefs.getBoolean("ses",true));
        if(prefs.getBoolean("ses",true)){
            music.play();
        }else music.stop();


		balloons = new Array<Balon>();
	}
	public void spawnBalloon() {
        int heights;
        if(balloons.size == 0 && lvl == 1){
            heights = 128;
        }else heights = 82;
        if(MathUtils.random(0,5) == 4 ){
           // if (yellowShowed == false) {

                balloons.add(new Balon(this.lvl ,3, MathUtils.random(0, width - heights), MathUtils.random(0, height - heights), id));  // %10 ihtimalle sarı balon oluştur
                yellowid = balloons.size - 1;
                yellowShowed = true;
         //   }
        }else balloons.add(new Balon(this.lvl,MathUtils.random(0,2),MathUtils.random(0,width-heights),0,id));
        id++;
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
        switch (lvl){
            case 1:
                Gdx.gl.glClearColor(79 /255f, 98/255f,142/255f, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                break;
            case 2:
                Gdx.gl.glClearColor(151 /255f, 117/255f,170/255f, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                break;
            case 3:
                Gdx.gl.glClearColor(177 /255f, 89/255f,131/255f, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                break;
            default:
                Gdx.gl.glClearColor(255, 255, 255, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                break;
        }



        batch.begin();
	    if(ingame){
            font.draw(batch,"Puan: "+score+"  Gerekli: "+next,10,height-10);
            font.draw(batch,"Seviye: "+lvl,10,height-50);
            font.draw(batch,"Süre: "+time,width-160,height-10);
            for(Balon raindrop: balloons) {
                try{
                    if(raindrop.isVisible()) {
                        if(raindrop.TYPE == 1 || raindrop.TYPE == 2){
                            if(MathUtils.random(0,10) == 9){
                                switch (raindrop.TYPE) {// 1 YEŞİL  ,  2 SİYAH
                                    case 1:
                                        raindrop.updateBalon(2);
                                        break;
                                    case 2:
                                        raindrop.updateBalon(1);
                                        break;
                                }
                            }
                        }
                        batch.draw(raindrop.getImg(), raindrop.getX(), raindrop.getY());
                    }

                }catch (Exception e){
                    //System.out.println(TAG+ " render: "+ e.toString() );
                }

            }

        }else{
            if(red && green && yellow && black && this.score>=next){
                font.draw(batch,"Skorunuz: "+score,width/2-120,height/2);
                font.draw(batch,"KAZANDINIZ! Sonraki Seviyeye Geçiliyor..",width/2-240,height/2-60);
                prefs.putInteger("enyuksek",score);
                prefs.flush();
                if(lvl != 3)   won = true;
                else won=false;
            }else font.draw(batch,"Oyun Bitti! Skorunuz: "+score,width/2-140,height/2);
        }
        batch.end();
        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override
            public boolean touchDown (int x, int y, int pointer, int button) {
                // your touch down code here
                System.out.println("Touch: "+x+","+(height-y));
                for (Balon balon:balloons) {
                    if(balon.isVisible()){
                        System.out.println("Balon: "+balon.getX()+","+balon.getY());
                        if(x > balon.getX() && x<balon.getX()+balon.height){
                            if(height-y > balon.getY() && height-y < balon.getY()+balon.height){
                                System.out.println("Balon Patladı: "+balon.getId());
                                switch (balon.TYPE){
                                    case 0:
                                        red = true;
                                        break;
                                    case 1:
                                        green = true;
                                        break;
                                    case 2:
                                        black = true;
                                        break;
                                    case 3:
                                        yellow = true;
                                        break;
                                }
                                balon.setVisible(false);
                                score += balon.getScore();
                                if(prefs.getBoolean("ses",true)) pop.play();
                            }
                        }
                    }
                }
                return true; // return true to indicate the event was handled
            }

            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                // your touch up code here
                return true; // return true to indicate the event was handled
            }
        });

		if(TimeUtils.nanoTime() - lastDropTime > TIME_ONE_SECOND){
            if(yellowShowed){
                for (Balon balon:balloons) {
                    if(balon.TYPE == 3){
                        balon.setVisible(false);
                        yellowShowed = false;
                    }
                }
            }

            spawnBalloon();
            time--;
            if(time <= 0){
                ingame = false;

            }
            if(time <= -3){
               if(!won || lvl >= 3){
                   Gdx.app.exit();
               }else {

                       time = 30;
                       next += 100;
                       lvl++;
                       ingame = true;
                       red = false;
                       green = false;
                       yellow = false;
                       black =false;
                       won = false;
                       balloons.clear();

               }
            }

        }



		Iterator<Balon> iter = balloons.iterator();
		while(iter.hasNext()) {
			Balon balloon = iter.next();
            if(this.lvl == 3)  balloon.setY((int) (300 * Gdx.graphics.getDeltaTime()));// += 200 * Gdx.graphics.getDeltaTime();
           else balloon.setY((int) (200 * Gdx.graphics.getDeltaTime()));// += 200 * Gdx.graphics.getDeltaTime();
            if (balloon.getY() + balloon.height < 0) {
                iter.remove();
            }


		}

	}

    @Override
    public void dispose() {
        super.dispose();
        music.dispose();
    }

    public class Balon{
		public Texture img;
        public int TYPE,height,width;
		private int x,y,score,id,lvl=1;

        private boolean Visible = true;

		public Balon(int lvl,int type,int x,int y,int id){
			/**
			 @param type: 0 RED, 1 GREEN,  2 BLACK, 3 YELLOW
			 **/
			this.x = x;
			this.y = y;
            if(lvl < 2){
                this.height = 128;
                this.width = 128;
            }else{
                this.height = 82;
                this.width = 82;
            }

            this.id = id;
            this.lvl = lvl;
			switch (type){
				case 0:
					if(lvl == 1) this.img  = new Texture(Gdx.files.internal("balloon-red.png"));
                    else if(lvl >= 2) this.img  = new Texture(Gdx.files.internal("balloon-red64.png"));
					this.score = 10;
                    this.TYPE = 0;
					break;
				case 1:
                    if(lvl == 1) this.img  = new Texture(Gdx.files.internal("balloon-green.png"));
                    else if(lvl >= 2) this.img  = new Texture(Gdx.files.internal("balloon-green64.png"));
					this.score = 5;
                    this.TYPE = 1;
					break;
                case 2:
                    if(lvl == 1) this.img  = new Texture(Gdx.files.internal("balloon-black.png"));
                    else if(lvl >= 2) this.img  = new Texture(Gdx.files.internal("balloon-black64.png"));
                    this.score = -10;
                    this.TYPE = 2;
                    break;
				case 3:
                    if(lvl == 1) this.img  = new Texture(Gdx.files.internal("balloon-yellow.png"));
                    else if(lvl >= 2) this.img  = new Texture(Gdx.files.internal("balloon-yellow64.png"));
					this.score = 20;
                    this.TYPE = 3;
					break;

			}


		}

        public void lvlUp(){
            this.lvl++;
            this.height = 64;
            this.width = 64;
            switch (this.TYPE){
                case 0:
                    this.img  = new Texture(Gdx.files.internal("balloon-red64.png"));
                    break;
                case 1:
                    this.img  = new Texture(Gdx.files.internal("balloon-green64.png"));
                    break;
                case 2:
                    this.img  = new Texture(Gdx.files.internal("balloon-black64.png"));
                    break;
                case 3:
                    this.img  = new Texture(Gdx.files.internal("balloon-yellow64.png"));
                    break;
            }
        }

        public void updateBalon(int type){
            switch (type){
                case 1:
                    if(this.lvl ==1) this.img  = new Texture(Gdx.files.internal("balloon-green.png"));
                    else if(this.lvl >= 2) this.img  = new Texture(Gdx.files.internal("balloon-green64.png"));
                    this.score = 5;
                    this.TYPE = 1;
                    break;
                case 2:
                    if(this.lvl ==1) this.img  = new Texture(Gdx.files.internal("balloon-black.png"));
                    else if(this.lvl >= 2) this.img  = new Texture(Gdx.files.internal("balloon-black64.png"));
                    this.score = -10;
                    this.TYPE = 2;
                    break;
            }
        }

        public int getScore(){
            return this.score;
        }

        public void setVisible(Boolean vis){
            this.Visible = vis;
        }

        public boolean isVisible() {
            return Visible;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getId() {
            return id;
        }

        public Texture getImg() {
            return img;
        }

        public void setY(int y) {
          if(this.TYPE != 3){
              this.y += y;
              System.out.println("LVL : "+ this.lvl);
          }
            if(this.TYPE == 0) {
                if(MathUtils.random(0,10)== 9) {
                    if (MathUtils.randomBoolean()) this.x += MathUtils.random(20, 80);
                    else this.x -= MathUtils.random(20, 80);
                }
            }

        }
    }

}
