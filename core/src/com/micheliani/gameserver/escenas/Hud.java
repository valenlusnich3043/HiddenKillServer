package com.micheliani.gameserver.escenas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.micheliani.gameserver.HiddenKIllServer;

public class Hud implements Disposable{
	
	public Stage stage;
	private Viewport viewport;
	
	private Integer cuentaRegresiva;
	private float timeCount;
//	private Integer lista;
	
	Table table;
	
	Label tiempoLabel;
	Label cuentaRegresivaLabel;
	
//	Label asesinatosLabel;
//	Label listaLabel;
	
	
	public Hud(SpriteBatch sb) {
		cuentaRegresiva = 300;
//		lista = 0;
		
		viewport = new FitViewport(HiddenKIllServer.ancho, HiddenKIllServer.alto, new OrthographicCamera());
		stage = new Stage(viewport, sb);
		
		table = new Table();
		table.top();
		table.setFillParent(true);
		
		tiempoLabel = new Label("TIEMPO", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
//		asesinatosLabel = new Label("ASESINATOS", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		cuentaRegresivaLabel = new Label(String.format("%03d", cuentaRegresiva), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
//		listaLabel = new Label(String.format("25-%02d", lista), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		
		
		
		table.add(tiempoLabel).expandX().padTop(10);
//		table.add(asesinatosLabel).expandX().padTop(10);
		table.row();
		table.add(cuentaRegresivaLabel).expandX();
//		table.add(listaLabel).expandX();
		stage.addActor(table);
		
	}
	
	public void update(float dt) {
		timeCount += dt;
		if(timeCount >= 1) {
			cuentaRegresiva--;
			cuentaRegresivaLabel.setText(String.format("%03d", cuentaRegresiva));
			timeCount = 0;
		}
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}

}
