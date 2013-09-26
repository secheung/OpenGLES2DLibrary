package engine.open2d.texture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureTool {
	Context context;
	int textureQuality;
	
	public TextureTool(Context context){
		this.context = context;
		textureQuality = 1;
	}
	
	public void setTextureQuality(int textureQuality){
		this.textureQuality = textureQuality;
	}
	
	public Integer loadTexture(Texture texture){
		int[] textureHandle = new int[1];
		
		BitmapFactory.Options loadOptions = new BitmapFactory.Options();
		loadOptions.inSampleSize = textureQuality;
		Bitmap imgSpriteSheet = BitmapFactory.decodeResource(context.getResources(),texture.getResourceId(),loadOptions);
//		Bitmap imgSpriteSheet = BitmapFactory.decodeResource(context.getResources(),texture.getResourceId());


		GLES20.glDeleteTextures(1,textureHandle,0);
		GLES20.glGenTextures(1, textureHandle, 0);

		if(textureHandle[0] == 0){
			throw new RuntimeException("Error loading texture.");
		}

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, imgSpriteSheet, 0);

		imgSpriteSheet.recycle();
		System.gc();
		return textureHandle[0];
	}
}
