package net.abcbs.rpa.dto;

/***********************************************************************************************************************************************************************
 * @author mfribeiro
 * 
 * Description: ClaimLineDTO class used to perform core task such as create and construct the object used in ClaimLineJavaBean
 * 
 * Project: RPA Amisys Claim Service
 ***********************************************************************************************************************************************************************/
public class ClaimLineDTO {
	private String claimNumber;
	private String effectiveDate;
	private String allow;
	private String pay;
	private String st;
	private String serviceLine;
	private String lc; //location
	private String tt; //treatment
	private String proc; //proc#
	private String ex1;
	private String ex2;
	private String ex3;
	private String ex4;
	private String ex5;
	private String ex6;
	private String py; //paid
	private String error;
	
	private int row;
	
	//getters and setters
	public String getClaimNumber() {
		return claimNumber;
	}
	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getAllow() {
		return allow;
	}
	public void setAllow(String allow) {
		this.allow = allow;
	}
	public String getPay() {
		return pay;
	}
	public void setPay(String pay) {
		this.pay = pay;
	}
	public String getSt() {
		return st;
	}
	public void setSt(String st) {
		this.st = st;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getServiceLine() {
		return serviceLine;
	}
	public void setServiceLine(String serviceLine) {
		this.serviceLine = serviceLine;
	}
	public String getEx1() {
		return ex1;
	}
	public void setEx1(String ex1) {
		this.ex1 = ex1;
	}
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getTt() {
		return tt;
	}
	public void setTt(String tt) {
		this.tt = tt;
	}
	public String getProc() {
		return proc;
	}
	public void setProc(String proc) {
		this.proc = proc;
	}
	public String getEx2() {
		return ex2;
	}
	public void setEx2(String ex2) {
		this.ex2 = ex2;
	}
	public String getEx3() {
		return ex3;
	}
	public void setEx3(String ex3) {
		this.ex3 = ex3;
	}
	public String getEx4() {
		return ex4;
	}
	public void setEx4(String ex4) {
		this.ex4 = ex4;
	}
	public String getEx5() {
		return ex5;
	}
	public void setEx5(String ex5) {
		this.ex5 = ex5;
	}
	public String getEx6() {
		return ex6;
	}
	public void setEx6(String ex6) {
		this.ex6 = ex6;
	}
	public String getPy() {
		return py;
	}
	public void setPy(String py) {
		this.py = py;
	}

}


