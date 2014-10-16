package engine.open2d.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import engine.open2d.draw.DrawObject;
import engine.open2d.draw.Plane;
import engine.open2d.shader.Shader;
import engine.open2d.shader.ShaderTool;
import engine.open2d.texture.Texture;
import engine.open2d.texture.TextureTool;

public class WorldRenderer implements GLSurfaceView.Renderer{
	private final static String LOG_PREFIX = "WORLD_RENDERER";
	private final static String ITEM_EXISTS_WARNING = "Item exists in world renderer.  No Item added.";
	private final static String NO_ITEM_EXISTS_WARNING = "No Item exists in ";
	
	public final static String WORLD_SHADER = "world_shader";

	Context activityContext;
	RendererTool rendererTool;
	FPSCounter fpsCounter;
	
	ShaderTool shaderTool;
	TextureTool textureTool;

	private float bgRed = 0.0f;
	private float bgGreen = 0.0f;
	private float bgBlue = 0.0f;
	private float bgAlpha = 0.0f;
	
	private LinkedHashMap<String,Shader> shaders;
	//private LinkedHashMap<String,DrawObject> drawObjects;
	private LinkedHashSet<DrawObject> drawObjects;
	private SparseIntArray textureMap;

    public WorldRenderer(final Context activityContext) {
    	this.activityContext = activityContext;
    	rendererTool = new RendererTool();
    	fpsCounter = new FPSCounter();
    	shaderTool = new ShaderTool(activityContext);
    	textureTool = new TextureTool(activityContext);
    	
    	shaders = new LinkedHashMap<String,Shader>();
    	drawObjects = new LinkedHashSet<DrawObject>();
    	textureMap = new SparseIntArray();
    }

    public void setTrackFPS(boolean drawFPS){
    	fpsCounter.setTrackFPS(drawFPS);
    }

    public int getFPS(){
    	return fpsCounter.getFPS();
    }
    
    public void setTextureQuality(int quality){
    	textureTool.setTextureQuality(quality);
    }
    
	public void addDrawShape(DrawObject shape){
		if(drawObjects.contains(shape)){
			Log.w(LOG_PREFIX, ITEM_EXISTS_WARNING+" [shape : "+shape.name+"]");
			return;
		}

		synchronized(drawObjects){
			drawObjects.add(shape);
		}
    }
	
	public void removeDrawShape(DrawObject shape){
		synchronized(drawObjects){
			drawObjects.remove(shape);
		}
	}
	
    public void addCustomShader(String ref, int vertResourceId, int fragResourceId, String...attributes){
    	if(shaders.containsKey(ref)){
			Log.w(LOG_PREFIX, ITEM_EXISTS_WARNING+" [shader: "+ref+"]");
			return;
		}
    	
    	String vertShader = shaderTool.getShaderFromResource(vertResourceId);
    	String fragShader = shaderTool.getShaderFromResource(fragResourceId);
    	
    	Shader shader = new Shader(vertShader,fragShader,attributes);
    	
    	shaders.put(ref, shader);
    }

    public void setBackground(float red, float green, float blue, float alpha){
    	bgRed = red;
    	bgGreen = green;
    	bgBlue = blue;
    	bgAlpha = alpha;
    }
    
	public void initSetup(){

		GLES20.glClearColor(bgRed, bgGreen, bgBlue, bgAlpha);

		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		rendererTool.setLookAt(	0,
								0.0f, 0.0f, 0.0f,
								0.0f, 0.0f, -1.0f,
								0.0f, 1.0f, 0.0f);

		buildShaders();
		buildObjectTextures();
	}
	
	private void buildShaders(){
	    if(shaders == null || shaders.isEmpty()){
			Log.w(LOG_PREFIX, NO_ITEM_EXISTS_WARNING +" shaders");
			return;
	    }

	    for(Shader shader : shaders.values())
	    	shaderTool.buildShaderProgram(shader);
	}

	private void buildObjectTextures(){
		if(drawObjects == null || drawObjects.isEmpty()){
			Log.w(LOG_PREFIX, NO_ITEM_EXISTS_WARNING+ " textures");
			return;
	    }
		
		Iterator<DrawObject> iterator = drawObjects.iterator();
		
	    while(iterator.hasNext() ){
	    	DrawObject shape = iterator.next();
	    	Texture texture = ((Plane)shape).getTexture();
	    	if(texture != null){
	    		textureMap.put(texture.getResourceId(), textureTool.loadTexture(texture));
	    	}
	    }
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		initSetup();
	}
	
	public void updateDrawObject(DrawObject drawObject){
		drawObject.update();
	}
	
	public void updateDrawObject(DrawObject drawObj, float x, float y, float z){
		drawObj.setTranslationX(x);
		drawObj.setTranslationY(y);
		drawObj.setTranslationZ(z);
		
		drawObj.update();
	}

	public void passTouchEvents(MotionEvent e){
	} 
	
	public Plane getSelectedPlane(float xCoord ,float yCoord){
		float x = xCoord;
		float y = rendererTool.getViewportHeight() - yCoord;
		float closestdepth = -1;
		Plane objSelected=null;
		
		synchronized(drawObjects){
			Iterator<DrawObject> iterator = drawObjects.iterator();
			while(iterator.hasNext()){
				DrawObject drawObj = iterator.next();
				if(!drawObj.isDrawEnabled()){
					continue;
				}
				if(!drawObj.isUnprojectEnabled()){
					continue;
				}
				
				float[] projectedPoints = rendererTool.screenProjectPlane((Plane)drawObj);
	
				if(x > projectedPoints[0] && x < projectedPoints[0]+projectedPoints[2] && y > projectedPoints[1] && y < projectedPoints[1]+projectedPoints[3]){
					if(projectedPoints[4] >= closestdepth){
						closestdepth = projectedPoints[4];
						objSelected = (Plane)drawObj;
					}
				}
			}
			
			return objSelected;
		}
	}
	
	public float[] getUnprojectedPoints(float x, float y, DrawObject drawObj){
		return rendererTool.screenUnProjection(x,y,drawObj.getTranslationZ());
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		if(fpsCounter.isDrawFPSCounter())
			fpsCounter.logFrame();
		
		int worldShaderProgram = shaders.get(WORLD_SHADER).getShaderProgram();

		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glUseProgram(worldShaderProgram);

		rendererTool.setHandles(shaders.get(WORLD_SHADER));

		synchronized(drawObjects){
			List<DrawObject> sortedList = new ArrayList<DrawObject>(drawObjects);
			if(!sortedList.isEmpty()){
				Collections.sort(sortedList, rendererTool.zSorter);
				for(DrawObject drawObject : sortedList){
					if(drawObject.isDrawEnabled()){
						drawShape(drawObject);
					}
				}
			}
		}
	}

	private void drawShape(DrawObject drawObject){
		float[] positionData = drawObject.getPositionData();
		float[] colorData = drawObject.getColorData();
		float[] normalData = drawObject.getNormalData();

		//TODO Textures
		//TODO MAKE SO NOT HARDCODED
		Map<String,Integer> handles = rendererTool.getHandles();
		
		if(drawObject instanceof Plane){
			rendererTool.enableHandles("a_Position", positionData, Plane.POSITION_DATA_SIZE);
			rendererTool.enableHandles("a_Color", colorData, Plane.COLOR_DATA_SIZE);
			rendererTool.enableHandles("a_Normal", normalData, Plane.NORMAL_DATA_SIZE);
			rendererTool.enableHandles("a_useTexture",((Plane)drawObject).getUseTexture(),Plane.USE_TEXTURE_SIZE);
			
			Plane plane = (Plane)drawObject;
		    if(!(plane.getTexture() == null)){
				if(textureMap.get(plane.getTexture().getResourceId()) == 0){
					Log.w(LOG_PREFIX,"the plane " + plane.getRefName() + " was not preloaded during properly");
					return;
				}
		    	
		    	int textureUniformHandle = handles.get("u_Texture");
	
			    //TODO needs object index on active and uniform
			    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureMap.get(plane.getTexture().getResourceId()));
		    	GLES20.glUniform1i(textureUniformHandle,0);
		    	
		    	Texture shapeTexture = plane.getTexture();
		    	float[] textureData = shapeTexture.getTextureCoord();
		    	rendererTool.enableHandles("a_TexCoordinate", textureData, Plane.TEXTURE_DATA_SIZE);
		    }
	    }

	    int mvMatrixHandle = handles.get("u_MVMatrix");
	    int mvpMatrixHandle = handles.get("u_MVPMatrix");
        
	    rendererTool.translateModelMatrix(drawObject.getTranslationX(),drawObject.getTranslationY(),drawObject.getTranslationZ());
	    
		GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, rendererTool.getMVMatrix(), 0);
		GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, rendererTool.getMVPMatrix(), 0);

	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

	}
	
	public void setCamera(float x, float y, float z){
		rendererTool.setLookAt(	0,
								x, y, z,
								x, y, z-1.0f,
								0.0f, 1.0f, 0.0f);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		rendererTool.setViewportWidth(width);
		rendererTool.setViewportHeight(height);
		
		final float ratio = (float) width/height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;

		rendererTool.setFrustum(0, left, right, bottom, top, near, far);
	}
}