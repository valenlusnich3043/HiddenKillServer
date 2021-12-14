package com.micheliani.gameserver.pantallas;

import com.badlogic.gdx.Gdx;
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

public class PantallaJuego implements Screen {

	private HiddenKIllServer hiddenKill;
	private TextureAtlas atlas;

	// basic playscreen variables
	private OrthographicCamera camaraJuego;
	private Viewport gamePort;
	private Hud hud;

	// Tiled map variables
	private TmxMapLoader maploader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;

	// Box2d variables
	private World world;
	private Box2DDebugRenderer b2dr;

	private Personaje player;
	private Personaje player2;

	// Asset Manager
	private Music music;

	// Red
	private HiloServidor hs;
	
	private int jugadorMuerto = 0;
	private float contInicio = 0; 
	private float tiempoParaMorir = 0;

	public boolean isArriba1 = false, isDerecha1 = false, isIzquierda1 = false, isArriba2 = false, isDerecha2 = false,
			isIzquierda2 = false;

//	private int nroPlayer = 1;

	public PantallaJuego(HiddenKIllServer hiddenKill) {
		atlas = new TextureAtlas("personaje.pack");

		this.hiddenKill = hiddenKill;
		camaraJuego = new OrthographicCamera();
		gamePort = new FitViewport(HiddenKIllServer.ancho / HiddenKIllServer.PPM,
				HiddenKIllServer.alto / HiddenKIllServer.PPM, camaraJuego);

		hud = new Hud(hiddenKill.batch);

		maploader = new TmxMapLoader();
		map = maploader.load("hiddenKillMap.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / HiddenKIllServer.PPM);

		// centrar la camara correctamente al inicio del juego
		camaraJuego.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

		world = new World(new Vector2(0, -10), true);
		b2dr = new Box2DDebugRenderer();

		new B2WorldCreator(world, map);

		music = HiddenKIllServer.manager.get("audio/musica/musica.ogg", Music.class);
		music.setVolume(0.08f);
		music.setLooping(true);
		music.play();

		hs = new HiloServidor(this);
		hs.start();

		player = new Personaje(world, this, hs, 1);
		player2 = new Personaje(world, this, hs, 2);
	}

	public TextureAtlas getAtlas() {
		return atlas;
	}

	@Override
	public void show() {

	}

	public void handleInput(float dt) {

		if (isArriba1) {
			player.jump();
//			player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
			HiddenKIllServer.manager.get("audio/sonidos/salto1.wav", Sound.class).play();
		} else if (isArriba2) {
			player2.jump();
//			player2.b2body.applyLinearImpulse(new Vector2(0, 4f), player2.b2body.getWorldCenter(), true);
			HiddenKIllServer.manager.get("audio/sonidos/salto1.wav", Sound.class).play();
		}

		if (isDerecha1) {
			player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
		} else if (isDerecha2) {
			player2.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player2.b2body.getWorldCenter(), true);

		}

		if (isIzquierda1) {
			player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
		} else if (isIzquierda2) {
			player2.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player2.b2body.getWorldCenter(), true);

		}

	}

	public void update(float dt) {
		// handle user input first
		handleInput(dt);

		world.step(1 / 60f, 6, 2);

		player.update(dt);
		player2.update(dt);

		hud.update(dt);

//		camaraJuego.position.x = player.b2body.getPosition().x;
//		camaraJuego.position.x = player2.b2body.getPosition().x;
		
		contInicio += dt;
		if(contInicio>10) {
			camaraJuego.position.x += (1.2)*dt;
		}

		// update our gamecam with correct coordinates after changes
		camaraJuego.update();
		// tell our renderer to draw only what our camera can see in our game world
		renderer.setView(camaraJuego);

		// Determino los finales del juego
	}
	
	

//	public void muere() {
//		
//		if(player.b2body.getPosition().x < 0 || player.b2body.getPosition().y > 87) {
//		player.currentState = Personaje.State.DEAD;
//	}
//	
//	if(player.b2body.getPosition().x < 0 || player.b2body.getPosition().y > 87) {
//		player2.currentState = Personaje.State.DEAD;
//	}
//		
	
	
	public void chequearFueraDeCamara(float delta) {
		if(!camaraJuego.frustum.pointInFrustum(player.b2body.getPosition().x, player.b2body.getPosition().y, 0)) {
			tiempoParaMorir += delta;
			if(tiempoParaMorir > 5f) {
				player.currentState = Personaje.State.DEAD;				
			}
			
		}else if(!camaraJuego.frustum.pointInFrustum(player2.b2body.getPosition().x, player2.b2body.getPosition().y, 0)) {
			tiempoParaMorir += delta;
			if(tiempoParaMorir > 5f) {
				player2.currentState = Personaje.State.DEAD;				
			}		
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
			player2.draw(hiddenKill.batch);

			hs.enviarMensajeATodos("Actualizar-P1-" + player.getX() + "-" + player.getY());
			hs.enviarMensajeATodos("Actualizar-P2-" + player2.getX() + "-" + player2.getY());

			hiddenKill.batch.end();

			hiddenKill.batch.setProjectionMatrix(hud.stage.getCamera().combined);
			hud.stage.draw();
			
			chequearFueraDeCamara(delta);

			if (gameOver()) {
				int x = 0;
				if (player.currentState == Personaje.State.DEAD) {
					x = 1;
				}
				if (player2.currentState == Personaje.State.DEAD) {
					x = 2;
				}
				hiddenKill.setScreen(new PantallaGameOver(hiddenKill, x));
				dispose();
			}
		}

	}

	public boolean gameOver() {
		if (player.currentState == Personaje.State.DEAD) {
			hs.enviarMensajeATodos("Fin-P1");
			return true;
		}
		if (player2.currentState == Personaje.State.DEAD) {
			hs.enviarMensajeATodos("Fin-P2");
			return true;
		}
		return false;
	}
	
	private void gameOver2() {
		// Jugador 1
        // Cuando el personaje se cae en la lava
        if (player.getY() < 0) {
            hs.enviarMensajeATodos("Termino-P1");
        }

        // Usamos la ubicacion del personaje para poder determinar la meta
        if ((player.getX() <= 1.64f && player.getY() >= 1.46f)
                && (player.getX() >= 1.32f && player.getY() <= 1.6f)) {
            servidor.enviarATodos("finalizoCarrera!1");
//            jugador1.llegoSalida();
        }
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
