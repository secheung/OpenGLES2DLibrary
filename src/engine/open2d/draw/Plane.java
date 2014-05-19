package engine.open2d.draw;

import android.util.Log;
import engine.open2d.texture.AnimatedTexture;
import engine.open2d.texture.Texture;
import engine.open2d.texture.AnimatedTexture.Playback;

public class Plane extends DrawObject{
	public final static int POSITION_DATA_SIZE = 3;
	public final static int COLOR_DATA_SIZE = 4;
	public final static int NORMAL_DATA_SIZE = 3;
	public final static int TEXTURE_DATA_SIZE = 2;
	public final static int USE_TEXTURE_SIZE = 1;
	
	private final static float DEFAULT_Z_DISTANCE = -2.0f;
	
	Texture texture;
	private float useTexture;

	//Init single image plane
    public Plane(	int referenceId, String name,
    				float planeWidth, float planeHeight
    			){
    	drawObjectInit(name);
    	planeInitDefault();
		planeInitPos(planeWidth, planeHeight);
		useTexture = 1.0f;
		texture = new Texture(referenceId);
	}

    //Init animated plane
	public Plane(	int referenceId, String name,
					float planeWidth, float planeHeight,
					int rows, int columns){
		
		drawObjectInit(name);
		planeInitDefault();
		planeInitPos(planeWidth, planeHeight);
		useTexture = 1.0f;
		texture = new AnimatedTexture(referenceId, rows, columns);
	}

	//Init single color plane
	public Plane(	String name,
					float planeWidth, float planeHeight,
					float R, float G, float B, float A){

		drawObjectInit(name);
		planeInitDefault();
		planeInitPos(planeWidth, planeHeight);
		useTexture = 0.0f;
		planeInitColor(R,G,B,A);
	}
	
	protected void drawObjectInit(String name){
		this.name = name;
		drawEnabled = false;
		unprojectEnabled = true;
	}
	
	private void planeInitPos(	float planeWidth, float planeHeight){
		float[] box = {
				planeWidth,  planeHeight,	DEFAULT_Z_DISTANCE,
				0.0f,   	 planeHeight,	DEFAULT_Z_DISTANCE,
				0.0f,   	 0.0f,			DEFAULT_Z_DISTANCE,
				0.0f,   	 0.0f,			DEFAULT_Z_DISTANCE,
				planeWidth,  0.0f,			DEFAULT_Z_DISTANCE,
				planeWidth,  planeHeight,	DEFAULT_Z_DISTANCE
		};
		this.positionData = box;
	}
	
	private void planeInitColor(float R, float G, float B, float A){
		float[] colorBox = {
			    // R, G, B, A
				R, G, B, A,
				R, G, B, A,
				R, G, B, A,
				R, G, B, A,
				R, G, B, A,
				R, G, B, A,
			};
		this.colorData = colorBox;
	}
	
	private void planeInitDefault(){
		float[] positionData = {
		    // X, Y, Z,
			1.0f,  1.0f, 0.0f,
			1.0f,  1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			1.0f,  1.0f, 0.0f
		};
		
		float[] colorData = {
		    // R, G, B, A
			1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f
		};
		
			
		float[] normalData= {
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f
		};
		
		this.positionData = positionData;
		this.colorData = colorData;
		this.normalData = normalData;
	}
	
	public void update(){
		if(texture instanceof AnimatedTexture){
			((AnimatedTexture) texture).incrementFrame();
		}
	}
	
	public void flipTexture(boolean flip){
		texture.setFlipped(flip);
	}
	
	public boolean isFlipped(){
		return texture.isFlipped();
	}
	
	public Playback getPlayback() {
		if(texture instanceof AnimatedTexture){
			return ((AnimatedTexture)texture).getPlayback();
		} else {
			Log.w("World Renderer","this texture is not an animated texture");
			return null;
		}
	}

	public void setPlayback(Playback playback) {
		if(texture instanceof AnimatedTexture){
			((AnimatedTexture)texture).setPlayback(playback);
		} else {
			Log.w("World Renderer","this texture is not an animated texture");
		}
	}

	public boolean isPlayed() {
		if(texture instanceof AnimatedTexture){
			return ((AnimatedTexture)texture).isPlayed();
		} else {
			Log.w("World Renderer","this texture is not an animated texture");
			return false;
		}
	}
	
	public void setFrame(int frame){
		if(texture instanceof AnimatedTexture){
			((AnimatedTexture)texture).setFrame(frame);
		} else {
			Log.w("World Renderer","this texture is not an animated texture");
		}
	}
	
	public int getFrame(){
		if(texture instanceof AnimatedTexture){
			return ((AnimatedTexture)texture).getFrame();
		} else {
			Log.w("World Renderer","this texture is not an animated texture");
			return 0;
		}
	}

	public int getTotalFrame(){
		if(texture instanceof AnimatedTexture){
			return ((AnimatedTexture)texture).getTotalFrames();
		} else {
			Log.w("World Renderer","this texture is not an animated texture");
			return 0;
		}
	}
	
	public void resetAnimation(){
		if(texture instanceof AnimatedTexture){
			((AnimatedTexture)texture).resetAnimation();
		} else {
			Log.w("World Renderer","this texture is not an animated texture");
		}
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public float getUseTexture(){
		return useTexture;
	}
}
