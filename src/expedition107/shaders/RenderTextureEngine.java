package expedition107.shaders;

import org.andengine.engine.Engine;
import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.PixelFormat;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.render.RenderTexture;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

public class RenderTextureEngine extends Engine {
	private RenderTexture mRenderTexture;
	private Sprite mRenderSprite;
	private boolean isRenderTextureInit = false;
	
	float mPosX;
	float mPosY;
	int mTexturePosX;
	int mTexturePosY;
	int mTextureRegionWidth;
	int mTextureRegionHeight;
	
	boolean doReInit = false;
	
	ShaderProgram mShaderProgram;
	
	public RenderTextureEngine(EngineOptions pEngineOptions) {
		super(pEngineOptions);
		
	}
	
	@Override
	public void onDrawFrame(GLState pGLState)
			throws InterruptedException {
		if (doReInit){
			doReInit = false;
			mRenderSprite.dispose();
			mRenderSprite = null;
			mRenderTexture.flush(pGLState);
		}
		
		if (!isRenderTextureInit){
			initRenderTexture(pGLState);
			isRenderTextureInit = true;
		}
		
		mRenderTexture.begin(pGLState, false, false, Color.TRANSPARENT);
		{		
			super.onDrawFrame(pGLState);
		}
		mRenderTexture.end(pGLState);
		
		if (mRenderSprite != null) {
			
			super.onDrawFrame(pGLState);
			
			pGLState.pushProjectionGLMatrix();
			pGLState.orthoProjectionGLMatrixf(0, mCamera.getSurfaceWidth(), 0,
					mCamera.getSurfaceHeight(), -1, 1);
			{
				mRenderSprite.onDraw(pGLState, mCamera);
			}
			pGLState.popProjectionGLMatrix();
		} else {
			super.onDrawFrame(pGLState);
		}
	}
	
	private void initRenderTexture(GLState pGLState){
		mRenderTexture = new RenderTexture(this.getTextureManager(),(int)mCamera.getSurfaceWidth(), (int)mCamera.getSurfaceHeight(), PixelFormat.RGBA_8888);
		mRenderTexture.init(pGLState);
		mRenderSprite = new Sprite(mPosX, mPosY, TextureRegionFactory.extractFromTexture(mRenderTexture, mTexturePosX, mTexturePosY, mTextureRegionWidth, mTextureRegionHeight), this.getVertexBufferObjectManager());
		mRenderSprite.setFlippedVertical(true);
		if (mShaderProgram != null){
			mRenderSprite.setShaderProgram(mShaderProgram);
		}else{
			//throw new IllegalStateException("ShaderProgram not initialized! Check setShaderProgramToRenderSprite!");
		}
	}

	public RenderTexture getRenderTexture(){
		if (mRenderTexture.isInitialized())
			return mRenderTexture;
		else
			return null;
	}
	
	private void setRenderSpriteTextureRegionCoordinates(float pPosX, float pPosY, int pTexturePosX, int pTexturePosY, int pTextureRegionWidth, int pTextureRegionHeight){
		mPosX = pPosX;
		mPosY = pPosY;
		mTexturePosX = pTexturePosX;
		mTexturePosY = pTexturePosY;
		mTextureRegionWidth = pTextureRegionWidth;
		mTextureRegionHeight = pTextureRegionHeight;
	}
	
	public void setRenderSpriteTextureRegionCoordinates(float pPosX, float pPosY, int pTextureRegionWidth, int pTextureRegionHeight){
		setRenderSpriteTextureRegionCoordinates(pPosX, pPosY, (int)(pPosX - pTextureRegionWidth/2), (int)(pPosY - pTextureRegionHeight/2), pTextureRegionWidth, pTextureRegionHeight);
	}
	
	public void setShaderProgramToRenderSprite(ShaderProgram pShaderProgram){
		mShaderProgram = pShaderProgram;
	}

	public void reInit() {
		if (isRenderTextureInit){
			isRenderTextureInit = false;
			doReInit = true;
		}
		
	}
	
}
