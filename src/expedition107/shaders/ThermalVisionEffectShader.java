package expedition107.shaders;

import org.andengine.opengl.shader.PositionTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import android.opengl.GLES20;

public class ThermalVisionEffectShader extends Object{
	public float vx_offset;
	
	private ThermalVisionEffectShaderProgram thermalVisionEffectShaderProgram;

	public ThermalVisionEffectShader(float pvx_offset) {
		this.vx_offset = pvx_offset;
		thermalVisionEffectShaderProgram = new ThermalVisionEffectShaderProgram();
	}

	public  ThermalVisionEffectShaderProgram getShaderProgram(){		
		return thermalVisionEffectShaderProgram;
	}

	public class ThermalVisionEffectShaderProgram extends ShaderProgram {
	
	public static final String FRAGMENTSHADER =  "precision mediump float;\n"
			+ "varying mediump vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" 
			+ "uniform sampler2D " + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ";\n" // 0
			+ "uniform float vx_offset;\n"
			+ "void main() \n"
			+ "{ \n"
			+ "vec2 uv = " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".xy;\n"
  
			+ "vec3 tc = vec3(1.0, 0.0, 0.0);\n"
			+ "if (uv.x < (vx_offset-0.005))\n"
			+ "{\n"
			+ "vec3 pixcol = texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", uv).rgb;\n"
			+ "vec3 colors0 = vec3(0.0,0.0,1.0);\n"
			+ "vec3 colors1 = vec3(1.0,1.0,0.0);\n"
			+ "vec3 colors2 = vec3(1.0,0.0,0.0);\n"
			+ "float lum = dot(vec3(0.30, 0.59, 0.11), pixcol.rgb);\n"
			+ "if (lum < 0.5){\n"
			+ "tc = mix(colors0,colors1,lum/0.5);\n"
			+ "}\n"
			+ "else {\n"
			+ "tc = mix(colors1,colors2,(lum-0.5)/0.5);\n"
			+ "}\n"
			
			+ "}\n"
			+ "else if (uv.x>=(vx_offset+0.005))\n"
			+ "{\n"
			+ "tc = texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", uv).rgb;\n"
			+ "}\n"
			+ "gl_FragColor = vec4(tc, 1.0);\n"
			+ "}\n";

	public  int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
	public  int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
	public  int sUniformTextureVx_OffsetLocation = ShaderProgramConstants.LOCATION_INVALID;

	private ThermalVisionEffectShaderProgram() {
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
		sUniformTextureVx_OffsetLocation = this
		.getUniformLocation("vx_offset");
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
		GLES20.glUniform1f(sUniformTextureVx_OffsetLocation, vx_offset);
		
	}

	@Override
	public void unbind(final GLState pGLState) throws ShaderProgramException {
		GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);
		super.unbind(pGLState);
	}
	}
}
