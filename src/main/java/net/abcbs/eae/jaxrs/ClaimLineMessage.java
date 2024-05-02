package net.abcbs.eae.jaxrs;
import javax.xml.bind.annotation.*;
/***********************************************************************************************************************************************************************
 * @author mfribeiro
 * 
 * Description: ClaimLineMessage class will be used to print the message back after the web service call
 * 
 * Project: NP Pended Claims
 ***********************************************************************************************************************************************************************/
@SuppressWarnings("unused")
public class ClaimLineMessage {
	private String servLnInfo;
	private int id;
	
	
	public ClaimLineMessage(){
		
	}
	public ClaimLineMessage(String message){
		this.servLnInfo = message;
	}
	public ClaimLineMessage(String message, int id){
		this.servLnInfo = message;
		this.id = id;
	}
	public String printMessage() {
		return servLnInfo;
	}
	
	public String getMessage(String message) {
		return message;
	}

	public void setMessage(String message) {
		this.servLnInfo = message;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
