package com.micheliani.gameserver.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.micheliani.gameserver.HiddenKIllServer;
import com.micheliani.gameserver.red.HiloServidor;

public class PantallaGameOver implements Screen{

	private Viewport viewport;
	private Stage stage;
	
	private Game game; 
	
	private HiloServidor hs;
	private int jugador;

	public PantallaGameOver(Game game, int jugador) {
		this.game = game;
		this.jugador = jugador;
		viewport = new FitViewport(HiddenKIllServer.ancho, HiddenKIllServer.alto, new OrthographicCamera());
		stage = new Stage(viewport, ((HiddenKIllServer) game).batch);
		
		Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
		
		Table table = new Table();
		table.center();
		table.setFillParent(true);
		
		Label gameOverLabel = new Label("FIN DEL JUEGO", font);
		Label ganador = new Label("Pierde el jugador" + this.jugador, font);
		Label juegarDeNuevoLabel = new Label("Haz click en cualquier parte de la pantalla para iniciar de vuelta", font);
		
		table.add(gameOverLabel).expandX();
		table.row();
		table.row();
		table.add(ganador).expandX();
		table.row();
		table.row();
		table.add(juegarDeNuevoLabel).expandX().padTop(10f);
		
		stage.addActor(table);
	}
	
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		
//		if(Gdx.input.justTouched()) {
//			if(p == 1) {
//				game.setScreen(new PantallaJuego((HiddenKIllServer) game));
//				hs.enviarMensajeATodos("Fin-P1");
//			}else {
//				game.setScreen(new PantallaJuego((HiddenKIllServer) game));
//				hs.enviarMensajeATodos("Fin-P2");
//			}
//			dispose();
//		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
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
		// TODO Auto-generated method stub
		
	}

}
