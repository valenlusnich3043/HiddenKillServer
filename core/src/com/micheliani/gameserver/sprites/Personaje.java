package com.micheliani.gameserver.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.micheliani.gameserver.HiddenKIllServer;
import com.micheliani.gameserver.pantallas.PantallaJuego;
import com.micheliani.gameserver.red.HiloServidor;

public class Personaje extends Sprite {
	public enum State {
		FALLING, JUMPING, STANDING, RUNNING, DEAD
	}

	public State currentState;
	public State previousState;
	public World world;
	public Body b2body;
	private Animation<TextureRegion> CyborgStand;
	private Animation<TextureRegion> CyborgRun;
	private Animation<TextureRegion> CyborgJump;
	private float stateTimer;
	private boolean runningRight;
	private boolean muerteJugador;
	
	private HiloServidor hs;
	private int nroPersonaje;
	
	
	public Personaje(World world, PantallaJuego screen, HiloServidor hs, int nroPersonaje) {
		super(screen.getAtlas().findRegion("Cyborg_idle"));
		this.hs = hs;
		this.world = world;
		this.nroPersonaje = nroPersonaje;
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0;
		runningRight = true;

		Array<TextureRegion> frames = new Array<TextureRegion>();

		for (int i = 0; i < 6; i++) {
			frames.add(new TextureRegion(getTexture(), i * 48, 10, 48, 48));
		}

		CyborgRun = new Animation<>(0.1f, frames);
		frames.clear();

		for (int i = 10; i < 14; i++) {
			frames.add(new TextureRegion(getTexture(), i * 48, 10, 48, 48));
		}
		CyborgJump = new Animation<>(0.1f, frames);
		frames.clear();
		
		for (int i = 6; i < 10; i++) {
            frames.add(new TextureRegion(getTexture(), i * 48, 10, 48, 48));
		}
		CyborgStand = new Animation<>(0.1f, frames);
        frames.clear();

		definePersonaje();
		
		setBounds(0, 0, 30 / HiddenKIllServer.PPM, 20 / HiddenKIllServer.PPM);
	}

	public void update(float dt) {
		setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
		setRegion(getFrame(dt));
	}

	private TextureRegion getFrame(float dt) {
		currentState = getState();

		TextureRegion region;
		switch (currentState) {
		case JUMPING:
			region = CyborgJump.getKeyFrame(stateTimer);			
			
			break;
		case RUNNING:
			region = CyborgRun.getKeyFrame(stateTimer, true);
			break;
		case FALLING:
		case STANDING:
		default:
			region = CyborgStand.getKeyFrame(stateTimer);
			break;
		}

		if(nroPersonaje == 1) {	
			hs.enviarMensajeATodos("ActualizarM!P1!" + currentState);					
		}else {
			hs.enviarMensajeATodos("ActualizarM!P2!" + currentState);				
		} 
		
		if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
			region.flip(true, false);
			runningRight = false;
		} else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
			region.flip(true, false);
			runningRight = true;
		}
		
		stateTimer = currentState == previousState ? stateTimer + dt: 0; 
		previousState = currentState;

		return region;
	}
	
	private State getState() {
		if (b2body.getLinearVelocity().y > 0 || b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING) {
			return State.JUMPING;
		} else if (b2body.getLinearVelocity().y < 0) {
			return State.FALLING;
		} else if (b2body.getLinearVelocity().x != 0) {
			return State.RUNNING;
		} else if (muerteJugador){
			return State.DEAD;
		}else{
			return State.STANDING;
		}

	}
	
	
	public int getNroPersonaje() {
		return nroPersonaje;
	}

	public void jump(){
        if (currentState != State.JUMPING) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }
	
	public void definePersonaje() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(32 / HiddenKIllServer.PPM, 32 / HiddenKIllServer.PPM);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);

		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / HiddenKIllServer.PPM);

		fdef.shape = shape;
		b2body.createFixture(fdef);

	}
	
	
	public boolean personajeMuerto() {
		return muerteJugador;
	}

}
