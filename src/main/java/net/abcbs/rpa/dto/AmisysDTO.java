package net.abcbs.rpa.dto;

/***********************************************************************************************************************************************************************
 * @author mfribeiro
 * 
 * Description: AmisysDTO class used to perform core task such as create and construct the object used to in java beans
 * 
 * Project: RPA Amisys Claim Service
 ***********************************************************************************************************************************************************************/

public class AmisysDTO {
	
	//local variables
	private String powerId;
	private String powerQueueName;
	private String corrClaimNumber;
	private String origClaimNumber;	
	private String memberNumber;
	private String effectiveDate;
	private String affNumber;
	private String exArray;
	private String claimType;
	private String originalPaid;
	private String originalStatus;
	private String origxref;
	private String corrxref;
	private String pendEx;
	private String error;
	private String lock;
	private String paidAmt;
	private String claimPend;
	private Boolean exclusion;
	private Boolean activeMembership;
	
	
	
	//getters and setters
	public String getPowerId() {
		return powerId;
	}
	public void setPowerId(String powerId) {
		this.powerId = powerId;
	}
	public String getPowerQueueName() {
		return powerQueueName;
	}
	public void setPowerQueueName(String powerQueueName) {
		this.powerQueueName = powerQueueName;
	}
	public String getCorrClaimNumber() {
		return corrClaimNumber;
	}
	public void setCorrClaimNumber(String corrClaimNumber) {
		this.corrClaimNumber = corrClaimNumber;
	}
	public String getOrigClaimNumber() {
		return origClaimNumber;
	}
	public void setOrigClaimNumber(String origClaimNumber) {
		this.origClaimNumber = origClaimNumber;
	}
	public String getMemberNumber() {
		return memberNumber;
	}
	public void setMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getAffNumber() {
		return affNumber;
	}
	public void setAffNumber(String affNumber) {
		this.affNumber = affNumber;
	}
	public String getExArray() {
		return exArray;
	}
	public void setExArray(String exArray) {
		this.exArray = exArray;
	}
	public String getClaimType() {
		return claimType;
	}
	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}
	public String getOriginalPaid() {
		return originalPaid;
	}
	public void setOriginalPaid(String originalPaid) {
		this.originalPaid = originalPaid;
	}
	public String getOriginalStatus() {
		return originalStatus;
	}
	public void setOriginalStatus(String originalStatus) {
		this.originalStatus = originalStatus;
	}
	public String getOrigxref() {
		return origxref;
	}
	public void setOrigxref(String origxref) {
		this.origxref = origxref;
	}
	public String getCorrxref() {
		return corrxref;
	}
	public void setCorrxref(String corrxref) {
		this.corrxref = corrxref;
	}
	public String getPendEx() {
		return pendEx;
	}
	public void setPendEx(String pendEx) {
		this.pendEx = pendEx;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	public String getLock() {
		return lock;
	}
	public String getPaidAmt() {
		return paidAmt;
	}
	public void setLock(String lock) {
		this.lock = lock;
	}
	public void setPaidAmt(String paidAmt) {
		this.paidAmt = paidAmt;
	}
	
	public Boolean getExclusion() {
		return exclusion;
	}
	public void setExclusion(Boolean flag) {
		this.exclusion = flag;
	}
	public Boolean getActiveMembership() {
		return activeMembership;
	}
	public void setActiveMembership(Boolean activeMembership) {
		this.activeMembership = activeMembership;
	}
	public String getClaimPend() {
		return claimPend;
	}
	public void setClaimPend(String claimPend) {
		this.claimPend = claimPend;
	}
	
	
	
	
	

}
