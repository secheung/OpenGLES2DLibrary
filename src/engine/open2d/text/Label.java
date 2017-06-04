package engine.open2d.text;

import android.graphics.PointF;

public class Label {
	String uid;
	String text;
	PointF location;
	boolean isVisible;

	public Label(String id){
		uid = id;
		text = "";
		location = new PointF(100,100);
		isVisible = true;
	}
	
	public String getUID() {
		return uid;
	}
	
	public String getText(){
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public PointF getLocation() {
		return location;
	}
	
	public void setLocation(PointF location) {
		this.location = location;
	}
	
	public void setLocation(float xPos, float yPos) {
		this.location.x = xPos;
		this.location.y = yPos;
	}
	
	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
}
