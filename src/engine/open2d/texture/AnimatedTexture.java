package engine.open2d.texture;

import android.util.Log;


public class AnimatedTexture extends Texture{
	public enum Playback{
		PLAY("play"),
		PLAY_ONCE("play_once"),
		PAUSE("pause"),
		REVERSE("reverse");

		public static Playback getPlaybackFromName(String name){
			for(Playback playerState : Playback.values()){
				if(name.equals(playerState.getName())){
					return playerState;
				}
			}
			return null;
		}

		String name;
		Playback(String n){
			name = n;
		}

		public String getName(){
			return name;
		}
	}
	
	Playback playback;
	boolean played;
	
    private float frameWidth;
    private float frameHeight;
    private int rows;
    private int columns;
    private int totalFrames;
    private int currentFrame;
    private int frameRate;
	
	public AnimatedTexture(int resourceId, int rows, int columns){
		super(resourceId);
		
		this.rows = rows;
		this.columns = columns;
		this.frameWidth = 1.0f/rows;
		this.frameHeight = 1.0f/columns;
		this.totalFrames = rows*columns;
		this.currentFrame = 0;
		this.frameRate= 1;
		
		playback = Playback.PLAY;
		played = false;
	}
	
	public void incrementFrame(){
		if(playback == Playback.PAUSE){
			return;
		}

		if(playback == Playback.PLAY || playback == Playback.PLAY_ONCE){
			currentFrame += frameRate;
		} else if(playback == Playback.REVERSE){
			currentFrame -= frameRate;
		}
		
		if(currentFrame >= totalFrames){
			if(playback == Playback.PLAY)
				currentFrame = 0;
			else if(playback == Playback.PLAY_ONCE)
				currentFrame = totalFrames - 1;
			played = true;
		} else if(currentFrame <= 0) {
			currentFrame = totalFrames - 1;
		}
		updateTextureCoord(currentFrame);
	}
	
	public void setFrame(int frame){
		if(currentFrame >= totalFrames || currentFrame <= 0){
			Log.w("Animated Texture", "set frame is out of animated bounds");
			return;
		}
		currentFrame = frame;
		updateTextureCoord(frame);
	}
	
	public int getFrame(){
		return currentFrame;
	}
	
	public int getTotalFrames(){
		return totalFrames;
	}
	
	public void resetAnimation(){
		currentFrame = 1;
		played = false;
		frameRate = 1;
	}
	
	public Playback getPlayback() {
		return playback;
	}

	public void setPlayback(Playback playback) {
		this.playback = playback;
	}

	public boolean isPlayed() {
		return played;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}

	private void updateTextureCoord(int frame){
		
		int row = ((int)frame / columns);
		int column = ((int)frame % columns);
		
		if(!flipped){
			float[] texCoord = {
					frameHeight*column + frameHeight,	frameWidth*row,
					frameHeight*column,					frameWidth*row,
					frameHeight*column,					frameWidth*row + frameWidth,
					frameHeight*column,					frameWidth*row + frameWidth,
					frameHeight*column + frameHeight,	frameWidth*row + frameWidth,
					frameHeight*column + frameHeight,	frameWidth*row
			};
			textureCoord = texCoord;
		} else {
			float[] texCoord = {
				frameHeight*column,					frameWidth*row,
				frameHeight*column + frameHeight,	frameWidth*row,
				frameHeight*column + frameHeight,	frameWidth*row + frameWidth,
				frameHeight*column + frameHeight,	frameWidth*row + frameWidth,
				frameHeight*column,					frameWidth*row + frameWidth,
				frameHeight*column,					frameWidth*row
			};
			textureCoord = texCoord;
		}
		
	}
}
