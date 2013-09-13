package engine.open2d.shader;

public class Shader {
	private String vertexShaderProgram;
	private String fragmentShaderProgram;
	private String attributes[];

	private int shaderProgram;

	public Shader(String vertexShader,String fragmentShader,String[] attributes){
		this.vertexShaderProgram = vertexShader;
		this.fragmentShaderProgram = fragmentShader;
		this.attributes = attributes;
	}

	public int getShaderProgram() {
		return shaderProgram;
	}
	public void setShaderProgram(int shaderProgram) {
		this.shaderProgram = shaderProgram;
	}

	public String getVertexShaderProgram() {
		return vertexShaderProgram;
	}
	public Shader setVertexShaderProgram(String vShaderProgram) {
		this.vertexShaderProgram = vShaderProgram;
		return this;
	}

	public String getFragmentShaderProgram() {
		return fragmentShaderProgram;
	}
	public Shader setFragmentShaderProgram(String fShaderProgram) {
		this.fragmentShaderProgram = fShaderProgram;
		return this;
	}

	public String[] getAttributes() {
		return attributes;
	}
	public Shader setAttributes(String...attributes){
		this.attributes = attributes;
		return this;
	}
}
