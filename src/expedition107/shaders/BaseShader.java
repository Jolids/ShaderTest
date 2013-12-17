package expedition107.shaders;

import org.andengine.opengl.shader.PositionTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import android.opengl.GLES20;

public class BaseShader extends Object{

	private NoEffectShaderProgram NoEffectShaderProgram;
	
	public BaseShader() {
		this.NoEffectShaderProgram = new NoEffectShaderProgram();
	}
	
	public NoEffectShaderProgram getShaderProgram(){		
		return NoEffectShaderProgram;
	}

	public class NoEffectShaderProgram extends ShaderProgram {

	public  int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
	public  int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;


	
	private NoEffectShaderProgram() {
		super(PositionTextureCoordinatesShaderProgram.VERTEXSHADER,
				PositionTextureCoordinatesShaderProgram.FRAGMENTSHADER);
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
	}

	@Override
	public void unbind(final GLState pGLState) throws ShaderProgramException {
		GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);
		super.unbind(pGLState);
	}
	
	}
}
