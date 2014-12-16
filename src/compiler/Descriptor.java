package compiler;

public class Descriptor{

	private Type type;
	
	public Descriptor(Type type){
		this.type = type;
	}
	
	public Type getType(){
		return type;
	}
	
	public void setType(Type type){
		this.type = type;
	}
}