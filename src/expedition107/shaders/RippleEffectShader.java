package expedition107.shaders;


//Sprite must have one individual texture!!!
import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.opengl.shader.PositionTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;


import android.opengl.GLES20;

public class RippleEffectShader extends Object{
	
	public float pixelWidth;
	public float pixelHeight;
	public float time;
	
	private RippleEffectShaderProgram  rippleEffectShaderProgram;
	
	public RippleEffectShader(Engine pEngine, float pWidth, float pHeight) {
		pixelWidth = pWidth;
		pixelHeight = pHeight;
		pEngine.registerUpdateHandler(new IUpdateHandler() {
			   public void reset() {
			   }

			   public void onUpdate(float pSecondsElapsed) {
				   RippleEffectShader.this.time += pSecondsElapsed * 0.5;
			   }
			  });
		rippleEffectShaderProgram = new RippleEffectShaderProgram();
	}

	public  RippleEffectShaderProgram getShaderProgram(){		
		return rippleEffectShaderProgram;
	}
	
	public void setResolution(float pWidth, float pHeight){
		this.pixelWidth = pWidth;
		this.pixelHeight = pHeight;
	}

	
	public  class RippleEffectShaderProgram extends ShaderProgram {
		
	public static final String FRAGMENTSHADER = "precision highp float;\n" +
	  "uniform float time;\n" +
	  "uniform vec2 resolution;\n" +
	  "uniform sampler2D " + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ";\n" +
	  "varying mediump vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +
	  "void main(void)\n" +
	  "{\n" +
	  "vec2 halfres = resolution.xy/2.0;\n" +
	  "vec2 cPos = gl_FragCoord.xy;\n" +
	  "cPos.x -= halfres.x*sin(time/2.0)+0.3*halfres.x*cos(time)+halfres.x;\n" +
	  "cPos.y -= halfres.y*sin(time/5.0)+0.3*halfres.y*cos(time)+halfres.y;\n" +
	  "float cLength = length(cPos);\n" +
	  "vec2 uv = gl_FragCoord.xy/resolution.xy+(cPos/cLength)*sin(cLength/30.0-time*10.0)/25.0;\n" +
	  "vec3 col = texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ",uv).xyz;\n" +
	  "gl_FragColor = vec4(col,1.0);\n" +
	  "}\n";

	public  int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
	public  int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
	public  int sUniformTimeLocation = ShaderProgramConstants.LOCATION_INVALID;
	public  int sUniformResolution = ShaderProgramConstants.LOCATION_INVALID;
	
	

	private RippleEffectShaderProgram() {
		super(PositionTextureCoordinatesShaderProgram.VERTEXSHADER,
				FRAGMENTSHADER);
	}

	@Override
	protected void link(final GLState pGLState)
			throws ShaderProgramLinkException {
		GLES20.glBindAttribLocation(this.mProgramID,
				ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION,
				ShaderProgramConstants.ATTRIBUTE_POSITION);
		GLES20.glBindAttribLocation(this.mProgramID,
				ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION,
				ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);

		super.link(pGLState);

		sUniformModelViewPositionMatrixLocation = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
		sUniformTexture0Location = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);
		 
		sUniformResolution = this.getUniformLocation("resolution");
		sUniformTimeLocation = this.getUniformLocation("time");

	}

	@Override
	public void bind(final GLState pGLState,
			final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
		GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(
				sUniformModelViewPositionMatrixLocation, 1,
				false, pGLState.getModelViewProjectionGLMatrix(), 0);
		GLES20.glUniform1i(sUniformTexture0Location, 0);
		GLES20.glUniform1f(sUniformTimeLocation, time);
		GLES20.glUniform2f(sUniformResolution, pixelWidth, pixelHeight);
	}

	@Override
	public void unbind(final GLState pGLState) throws ShaderProgramException {
		GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);
		super.unbind(pGLState);
	}
	
}
}
