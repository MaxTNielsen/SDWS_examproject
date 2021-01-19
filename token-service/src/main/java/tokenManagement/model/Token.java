package tokenManagement.model;
public class Token
{
	private String id;
	private String userId;
	private boolean isUsed;
	
	public Token (String id, String userId) {
		this.id = id;
		this.userId = userId;
		this.isUsed = false;
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
	public String getUserId () {
		return userId;
	}
	@Override
	public String toString() {
		return "Token [id=" + id + ", userId=" + userId + ", isUsed=" + isUsed + "]";
	}
	

}
