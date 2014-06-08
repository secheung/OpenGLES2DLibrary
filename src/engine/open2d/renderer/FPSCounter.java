package engine.open2d.renderer;

import android.util.Log;

public class FPSCounter {
	long startTime = System.nanoTime();
	int frames = 0;
	boolean drawFPSCounter = false;
	
	public void logFrame() {
		frames++;
		if(System.nanoTime() - startTime >= 1000000000) {
			Log.d("FPSCounter", "fps: " + frames);
			frames = 0;
			startTime = System.nanoTime();
		}
	}

	public boolean isDrawFPSCounter() {
		return drawFPSCounter;
	}

	public void setDrawFPSCounter(boolean drawFPSCounter) {
		this.drawFPSCounter = drawFPSCounter;
	}
}