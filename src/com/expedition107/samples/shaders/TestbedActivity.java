package com.expedition107.samples.shaders;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationByModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import expedition107.shaders.RenderTextureEngine;
import expedition107.shaders.RippleEffectShader;
import expedition107.shaders.ThermalVisionEffectShader;

public class TestbedActivity extends SimpleBaseGameActivity {

	public static final int CAMERA_WIDTH = 800;
	public static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private Scene mScene;
	private Camera mCamera;
	
	//background & clouds
	private BitmapTexture mSprite1Texture;
	private TextureRegion mSprite1TextureRegion;

	private Sprite sprite1;
	private BitmapTexture mCommonTexture;
	private TextureRegion mCommonTextureRegion;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new BoundCamera(0, 0, CAMERA_WIDTH,
				CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		return engineOptions;
	}
	
	@Override
    public Engine onCreateEngine(final EngineOptions pEngineOptions) {
		return new RenderTextureEngine(pEngineOptions);
	}


	
	@Override
	protected void onCreateResources() throws IOException {
		try {
			mSprite1Texture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {

						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/bgr.png");
						}
					}, TextureOptions.NEAREST);

			mSprite1Texture.load();
			mSprite1TextureRegion = TextureRegionFactory
					.extractFromTexture(mSprite1Texture);
			
			mCommonTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {

						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/top.jpg");
						}
					}, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

			mCommonTexture.load();
			mCommonTextureRegion = TextureRegionFactory.extractFromTexture(mCommonTexture);
			
		}catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected Scene onCreateScene() {
		mScene = new Scene(){
			@Override
			protected void onManagedDraw(final GLState pGLState, final Camera pCamera) {
				super.onManagedDraw(pGLState, pCamera);
			}
		};

		mScene.registerUpdateHandler(new FPSLogger());
		mScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		//создаем статический спрайт (горы)
		Sprite sprite = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2,480,480, mCommonTextureRegion, getVertexBufferObjectManager());
		
		//создаем спрайт который будет вращаться и его можно будет таскать пальцем по экрану
		sprite1 = new Sprite(50, 50, 200,100, mSprite1TextureRegion, getVertexBufferObjectManager()){
			private boolean startMove = false;
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()){
					
					startMove = true;
					return true;
				}
				if (pSceneTouchEvent.isActionMove()){
					if (startMove)
						this.setPosition(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
					return true;
				}
				if (pSceneTouchEvent.isActionUp()){
					
					startMove = false;
					return true;
				}
				
				return true;
			}
		};
		
		//задаем спрайту постоянное вращение для пущего эффекта
		sprite1.registerEntityModifier(new LoopEntityModifier(new RotationByModifier(5.0f, -180)));
		
		
		
		//***********GLOBAL*************
		((RenderTextureEngine)mEngine).setRenderSpriteTextureRegionCoordinates(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, 300, 300);
		 RippleEffectShader rippleEffectShader = new RippleEffectShader(mEngine, CAMERA_WIDTH, CAMERA_HEIGHT);
		((RenderTextureEngine)mEngine).setShaderProgramToRenderSprite(rippleEffectShader.getShaderProgram());
		//***********GLOBAL*************
		
		ThermalVisionEffectShader thermalVisionEffectShader = new ThermalVisionEffectShader(1.0f);
		this.getShaderProgramManager().loadShaderProgram(thermalVisionEffectShader.getShaderProgram());
		sprite1.setShaderProgram(thermalVisionEffectShader.getShaderProgram());
		
		mScene.attachChild(sprite);
		mScene.attachChild(sprite1);
		mScene.registerTouchArea(sprite1);
		return mScene;
	}

}
