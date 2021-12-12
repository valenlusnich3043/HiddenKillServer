package com.micheliani.gameserver.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.micheliani.gameserver.HiddenKIllServer;
import com.micheliani.gameserver.escenas.Hud;
import com.micheliani.gameserver.herramientas.B2WorldCreator;
import com.micheliani.gameserver.red.HiloServidor;
import com.micheliani.gameserver.sprites.Personaje;
import com.micheliani.gameserver.utiles.Global;
import com.micheliani.gameserver.utiles.Render;

public class PantallaJuego implements Screen{

	private HiddenKIllServer hiddenKill;
	private TextureAtlas atlas;
	
	// basic playscreen variables
	private OrthographicCamera camaraJuego;
	private Viewport gamePort;
	private Hud hud;
	
	
	//Tiled map variables
	private TmxMapLoader maploader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
		
	//Box2d variables
	private World world;
	private Box2DDebugRenderer b2dr;
	
	private Personaje player;
	private Personaje player2;
	
	//Asset Manager
	private Music music;
	
	//Red
	private HiloServidor hs;
	
	public PantallaJuego(HiddenKIllServer hiddenKill) { 
		atlas = new TextureAtlas("personaje.pack");//empieza error video 10
		
		this.hiddenKill = hiddenKill;
		camaraJuego = new OrthographicCamera();
		gamePort = new FitViewport(hiddenKill.ancho / hiddenKill.PPM, hiddenKill.alto / hiddenKill.PPM, camaraJuego);
		
		hud = new Hud(hiddenKill.batch); 
		
		maploader = new TmxMapLoader();
		map = maploader.load("hiddenKillMap.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / hiddenKill.PPM);
		
		//centrar la camara correctamente al inicio del juego 
		camaraJuego.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

		world = new World(new Vector2(0, -10), true);
		b2dr = new Box2DDebugRenderer();
		
		new B2WorldCreator(world, map);
		
		player = new Personaje(world, this);
//		player2 = new Personaje(world,this);
		
		music = HiddenKIllServer.manager.get("audio/musica/musica.ogg",  Music.class);
		music.setVolume(0.08f);
		music.setLooping(true);
		music.play();
		
		hs = new HiloServidor(this);
	    hs.start();
	}
	
	public TextureAtlas getAtlas() {
		return atlas;
	}
	
	@Override
	public void show() {
		
	}
	
	public void handleInput(float dt) {
		
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
			HiddenKIllServer.manager.get("audio/sonidos/salto1.wav",  Sound.class).play();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) 
			player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) 
			player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);

	}
	
	public void update(float dt) {
		//handle user input first
		handleInput(dt);
		
		world.step(1/60f, 6, 2);
		
		player.update(dt);
		hud.update(dt);
		camaraJuego.position.x = player.b2body.getPosition().x;
		
		//update our gamecam with correct coordinates after changes
		camaraJuego.update();
		//tell our renderer to draw only what our camera can see in our game world 	
		renderer.setView(camaraJuego);
		
		//Determino los finales del juego
		if(player.getY() < 0 || player.getX() > 87) {
			player.currentState = Personaje.State.DEAD;
		}
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();

		if (!Global.empieza) {
			Render.begin();
//			espera.dibujar();
			Render.end();
		} else {
			// separate our update logic from render
			update(delta);

			// limpiar pantalla
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			// render del mapa
			renderer.render();

			// renderer our Box2DDebugLines
			b2dr.render(world, camaraJuego.combined);

			hiddenKill.batch.setProjectionMatrix(camaraJuego.combined);
			hiddenKill.batch.begin();
			player.draw(hiddenKill.batch);
			hiddenKill.batch.end();

			hiddenKill.batch.setProjectionMatrix(hud.stage.getCamera().combined);
			hud.stage.draw();

			if (gameOver()) {
				hiddenKill.setScreen(new PantallaGameOver(hiddenKill));
				dispose();
			}
		}

	}

	public boolean gameOver() {
		if(player.currentState == Personaje.State.DEAD) {
			return true;
		}
		return false;
	}
	
	@Override
	public void resize(int width, int height) {
		gamePort.update(width, height);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		map.dispose();
		renderer.dispose();
		world.dispose();
		b2dr.dispose();
		hud.dispose();
	}

}
