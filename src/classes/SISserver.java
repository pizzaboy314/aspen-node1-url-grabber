package classes;

public class SISserver implements Comparable<Object> {
	
	public String serverstring;
	public String deploymentID;
	
	public SISserver(String serverstring, String deploymentID){
		this.serverstring = serverstring;
		this.deploymentID = deploymentID;
	}

	public String getServerstring() {
		return serverstring;
	}

	public void setServerstring(String serverstring) {
		this.serverstring = serverstring;
	}

	public String getDeploymentID() {
		return deploymentID;
	}

	public void setDeploymentID(String deploymentID) {
		this.deploymentID = deploymentID;
	}
	
	@Override
	public int compareTo(Object o) {
		return (this.deploymentID).compareTo(((SISserver) o).getDeploymentID());
	}
}
