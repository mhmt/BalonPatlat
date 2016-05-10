package com.mhmtozcann.balon;

import java.sql.Struct;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;




public class Balloon extends ApplicationAdapter {
    private static final String TAG ="InGame";
	public int TIME_ONE_SECOND = 1000000000;
	public int TIME_HALF_SECOND = 500000000;
    long time = 45;
	SpriteBatch batch;
	Texture img;
	int width;
	public Array<Balon> balloons;
	int height;
	public long lastDropTime;
    Music music,pop;
    int id = 0;
    int score = 0;
	@Override
	public void create () {
		batch = new SpriteBatch();
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

        music = Gdx.audio.newMusic(Gdx.files.internal("maintheme.mp3"));
        music.setVolume(0.5f);
        music.setLooping(true);

        pop = Gdx.audio.newMusic(Gdx.files.internal("balloon-pop.mp3"));
        pop.setVolume(0.5f);
        pop.setLooping(false);

        Preferences prefs = Gdx.app.getPreferences("prefs");
		System.out.println("Ses: "+prefs.getBoolean("ses",true));
        if(prefs.getBoolean("ses",true)){
            music.play();
        }else music.stop();



		balloons = new Array<Balon>();
	}
	public void spawnBalloon() {
		balloons.add(new Balon(MathUtils.random(0,4),MathUtils.random(0,width-128),0,id));
        id++;
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        final BitmapFont font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(2,2);
		batch.begin();
        font.draw(batch,"Puan: "+score,10,height-10);
        font.draw(batch,"Süre: "+time,width-150,height-10);
		for(Balon raindrop: balloons) {
            try{
                if(raindrop.isVisible()) {
                    batch.draw(raindrop.getImg(), raindrop.getX(), raindrop.getY());
                }

            }catch (Exception e){
                //System.out.println(TAG+ " render: "+ e.toString() );
            }

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
                        if(x > balon.getX() && x<balon.getX()+128){
                            if(height-y > balon.getY() && height-y < balon.getY()+128){
                                System.out.println("Balon Patladı: "+balon.getId());
                                balon.setVisible(false);
                                score += balon.getScore();
                                pop.play();
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
            spawnBalloon();
            time--;
            if(time < 0) Gdx.app.exit();

        }


		Iterator<Balon> iter = balloons.iterator();
		while(iter.hasNext()) {
			Balon balloon = iter.next();
			balloon.setY((int)(200 * Gdx.graphics.getDeltaTime()));// += 200 * Gdx.graphics.getDeltaTime();
			if(balloon.getY() + 128 < 0){
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
		private int x,y,score,height,width,id;
		//private Rectangle balon;
        private boolean Visible = true;

		public Balon(int type,int x,int y,int id){
			/**
			 @param type: 0 RED, 1 GREEN, 2 YELLOW, 3 BLACK
			 **/
			this.x = x;
			this.y = y;
			this.height = 128;
			this.width = 128;
            this.id = id;

			switch (type){
				case 0:
					this.img  = new Texture(Gdx.files.internal("balloon-red.png"));
					this.score = 10;

					break;
				case 1:
					this.img  = new Texture(Gdx.files.internal("balloon-green.png"));
					this.score = 5;
					break;
				case 2:
					this.img  = new Texture(Gdx.files.internal("balloon-yellow.png"));
					this.score = 20;
					break;
				case 3:
					this.img  = new Texture(Gdx.files.internal("balloon-black.png"));
					this.score = -10;
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
            this.y += y;
            if(y + 64 < 0) this.Visible = false;
        }
    }


}
