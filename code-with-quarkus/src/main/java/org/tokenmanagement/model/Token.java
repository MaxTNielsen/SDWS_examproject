package org.tokenmanagement.model;
public class Token
{
	private String id;
	private String cprNumber;
	private boolean isUsed;
	//public Token() {}
	
	
	public Token (String id, String cprNumber) {
		this.id = id;
		this.cprNumber = cprNumber;
		this.isUsed = true;
	}
	public boolean isUsed() {
		return isUsed;
	}
	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
	public String getId() {
		return id;
	}
	public String getCprNumber() {
		return cprNumber;
	}


	@Override
	public String toString() {
		return "Token [id=" + id + ", cprNumber=" + cprNumber + ", isUsed=" + isUsed + "]";
	}
	
	
	
	


}
