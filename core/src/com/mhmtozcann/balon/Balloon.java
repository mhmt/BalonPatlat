package com.mhmtozcann.balon;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;




public class Balloon extends ApplicationAdapter {
	public int TIME_ONE_SECOND = 1000000000;
	public int TIME_HALF_SECOND = 500000000;
	SpriteBatch batch;
	Texture img;
	int width;
	public Array<Rectangle> balloons;
	int height;
	public long lastDropTime;
	@Override
	public void create () {
		batch = new SpriteBatch();
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
        img = new Texture(Gdx.files.internal("balloon-red.png"));
        Preferences prefs = Gdx.app.getPreferences("prefs");
		System.out.println("Ses: "+prefs.getBoolean("ses",false));
		balloons = new Array<Rectangle>();
	}
	public void spawnBalloon() {
		Rectangle balloon = new Rectangle();
		balloon.x = MathUtils.random(0, 800-64);
		balloon.y = 0;
		balloon.width = 64;
		balloon.height = 64;
		balloons.add(balloon);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for(Rectangle raindrop: balloons) {
			batch.draw(img, raindrop.x, raindrop.y);
		}
		batch.end();



		if(TimeUtils.nanoTime() - lastDropTime > TIME_ONE_SECOND) spawnBalloon();


		Iterator<Rectangle> iter = balloons.iterator();
		while(iter.hasNext()) {
			Rectangle balloon = iter.next();
			balloon.y += 200 * Gdx.graphics.getDeltaTime();
			if(balloon.y + 64 < 0) iter.remove();

		}

	}


}
