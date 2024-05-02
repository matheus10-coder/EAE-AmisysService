package net.abcbs.rpa.javabeans;

import net.abcbs.rpa.dto.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Year;
import net.abcbs.issh.util.pub.common.ValidateUtilities;
import net.abcbs.issh.util.pub.javabeans.IsSharedJavaBean;

public class AmisysJavaBean extends IsSharedJavaBean{

	private static Logger logger = LogManager.getLogger(AmisysJavaBean.class);
	private AmisysDTO serviceInfoOutput = new AmisysDTO();




	public AmisysDTO queryServiceX(String dataSource, String claimNumber){


		//this.setDbFunctionDelete(dbFunctionDelete);

		serviceInfoOutput.setCorrClaimNumber(claimNumber);
		serviceInfoOutput.setExclusion(false);
		String origClaimNumber = null;
		String effDate = null;
		String affNumber = null;
		String firstClaimPayStatus = "";
		String secondClaimPayStatus = "";
		int claimCount = 0;
		int paidStatusCounter = 0;
		boolean yearCheck = false;
		final Pattern pattern = Pattern.compile("\\*[A-Za-z0-9]+", Pattern.CASE_INSENSITIVE);
		Matcher firstClaimPayPend = pattern.matcher(firstClaimPayStatus);
		Matcher secondClaimPayPend = pattern.matcher(secondClaimPayStatus);
		
		try{
			this.initializeConnection(dataSource, "");

			/*
			 * 
			 * QUERY I-a
			 * goal: fetch member number from corrected claim number as it comes from the oracle db with the white space 
			 * part of the a1a2 modernization
			 * 
			 */
			sqlStatement.append(" SELECT member_nbr, service_x.paid, service_x.claim_type");
			sqlStatement.append(" FROM service_x ");
			sqlStatement.append(" WHERE claim_nbr = ? ");

			//Execute query
			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			logger.info("SQL: {}", sqlStatement);

			resultSet = preparedStatement.executeQuery();
			serviceInfoOutput.setMemberNumber(resultSet.getString(1).trim());
			resultSet = preparedStatement.executeQuery();
			serviceInfoOutput.setClaimPend(resultSet.getString(2).trim());
			if (paidStatusCounter < 1) {
				firstClaimPayStatus = serviceInfoOutput.getClaimPend();
				firstClaimPayPend = pattern.matcher(firstClaimPayStatus);
			}
			firstClaimPayStatus = serviceInfoOutput.getClaimPend();
			paidStatusCounter ++;
			/*while (resultSet.next()) {
				serviceInfoOutput.setMemberNumber(resultSet.getString(1).trim());
				serviceInfoOutput.setClaimPend(resultSet.getString(2).trim());
					if (paidStatusCounter < 1) {
						firstClaimPayStatus = serviceInfoOutput.getClaimPend();
						firstClaimPayPend = pattern.matcher(firstClaimPayStatus);
					}
				firstClaimPayStatus = serviceInfoOutput.getClaimPend();
				paidStatusCounter ++;
			}*/
			//// added this check for member number query check
			String memNbr = serviceInfoOutput.getMemberNumber();
			sqlStatement.delete(0, sqlStatement.length());

			if (!invalidInput(serviceInfoOutput.getMemberNumber(), yearCheck) ){
				memNbr = serviceInfoOutput.getMemberNumber();
			} 
			else {
				serviceInfoOutput.setExclusion(true);
				serviceInfoOutput.setError("Member number has not passed input validation. Member # = " + memNbr);
				throw new IllegalArgumentException("member number has not passed input validation. Please review record values");
			}

			/*
			 * 
			 * QUERY I-b
			 * goal: fetch affiliation number and effective date for the corrected claim number retrieved from POWER
			 * 		 
			 * 
			 */


			sqlStatement.append(" SELECT distinct aff_nbr, ymdeff as eff_date");
			sqlStatement.append(" FROM service_x ");
			sqlStatement.append(" WHERE claim_nbr = ? AND member_nbr = '"+ memNbr +"'");
			//Execute query
			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			logger.info("SQL: {}", sqlStatement);

			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				serviceInfoOutput.setAffNumber(resultSet.getString(1));
				serviceInfoOutput.setEffectiveDate(resultSet.getString(2).trim());
				logger.info(serviceInfoOutput.getAffNumber());
			}


			if (!invalidInput(serviceInfoOutput.getEffectiveDate(), true)){
				effDate = serviceInfoOutput.getEffectiveDate();
			}
			else {
				serviceInfoOutput.setExclusion(true);
				serviceInfoOutput.setError("Effective date has not passed input validation. Please review if eff_date is not null and doesn't contain any special characters");
				throw new IllegalArgumentException("Effective date has not passed input validation. Please review record values");
			}

			//Input validation check
			if (!invalidInput(serviceInfoOutput.getAffNumber(), yearCheck) ){
				affNumber = serviceInfoOutput.getAffNumber();
			} 
			else {
				serviceInfoOutput.setExclusion(true);
				serviceInfoOutput.setError("Affiliation number has not passed input validation. Affiliation number = " + serviceInfoOutput.getAffNumber() +  " Effective date " + serviceInfoOutput.getEffectiveDate()
				+ " Corrected claim # = " + serviceInfoOutput.getCorrClaimNumber() + ". Member # = " + memNbr);
				throw new IllegalArgumentException("Affiliation number has not passed input validation. Please review record values");
			}


			/*
			 * 
			 * QUERY II
			 * goal: fetch distinct claim number, paid status and start the exclusion checks
			 *  
			 */
			sqlStatement.setLength(0);
			sqlStatement.append(" SELECT distinct claim_nbr, service_x.paid");
			sqlStatement.append(" FROM  service_x ");
			sqlStatement.append(" WHERE member_nbr = '"+ memNbr +"' AND substr(aff_nbr,1,length('"+affNumber+"'))='" + affNumber + "' AND ymdeff = '" + effDate + "' AND claim_nbr <> ?");

			//Execute query
			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			resultSet = preparedStatement.executeQuery();
			logger.info("SQL: {}", sqlStatement);

			while (resultSet.next()) {
				serviceInfoOutput.setOrigClaimNumber(resultSet.getString(1).trim());
				serviceInfoOutput.setClaimPend(resultSet.getString(2).trim());

				claimCount++;

			}
			if (claimCount == 1) {
				secondClaimPayStatus = serviceInfoOutput.getClaimPend();
				secondClaimPayPend = pattern.matcher(serviceInfoOutput.getClaimPend());
			}
			//Original Claim Exclude criteria

			if (claimCount < 1 && firstClaimPayPend.matches()) {
				//Case 0
				//Input validation success
				if (!invalidInput(serviceInfoOutput.getCorrClaimNumber(), yearCheck)){
					origClaimNumber = serviceInfoOutput.getCorrClaimNumber();
				}
				else {
					serviceInfoOutput.setExclusion(true);
					serviceInfoOutput.setError("Claim number has not passed input validation! Please review record value for special characters and others");
					throw new IllegalArgumentException("Claim number has not passed input validation! Please review record value for special characters and others");
				}

			}
			else {
				//Case 1
				if (claimCount == 1) {
					//Input validation success
					if (!invalidInput(serviceInfoOutput.getOrigClaimNumber(), yearCheck)){
						origClaimNumber = serviceInfoOutput.getOrigClaimNumber();
					}
					else {
						serviceInfoOutput.setError("Original claim number has not passed input validation! Please review record value for special characters and others");
						throw new IllegalArgumentException("Original claim number has not passed input validation! Please review record value for special characters and others");
					}
				}

				//Multiple Case
				else {
					serviceInfoOutput.setExclusion(true);
					serviceInfoOutput.setError("Claim has been excluded from bot process due to have more than one original claim number found that matches the given corrected claim number or claims are not in *N or other * paid status");
					throw new IllegalArgumentException("Claim has been excluded from bot process due to have more than one original claim number found that matches the given corrected claim number");

				}

			}


			/*
			 * 
			 * QUERY III
			 * goal: fetch original claim amount paid and final use for service x table 
			 * 
			 */
			sqlStatement.setLength(0);
			sqlStatement.append(" SELECT sum(amtpay) as paid_amt");
			sqlStatement.append(" FROM  service_x ");
			sqlStatement.append(" WHERE claim_nbr = '"+ origClaimNumber +"'");

			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			resultSet = preparedStatement.executeQuery();
			logger.info("SQL: {}", sqlStatement);

			while (resultSet.next()) {
				serviceInfoOutput.setPaidAmt(resultSet.getString(1).trim());

			}
			//Set the current results to decimals
			serviceInfoOutput.setPaidAmt(ClaimLineJavaBean.toDecimal(serviceInfoOutput.getPaidAmt()));

			/*
			 * 
			 * QUERY IV
			 * goal: fetch claim status, claim paid code, lock, and claim type and continue with the claim exclusions 
			 * 
			 */
			sqlStatement.setLength(0);
			sqlStatement.append(" SELECT resolution, paid, lock_x, claim_type");
			sqlStatement.append(" FROM  claim ");
			sqlStatement.append(" WHERE claim_nbr = '"+ origClaimNumber +"'");
			logger.info("SQL: {}", sqlStatement);


			preparedStatement = connection.prepareStatement(sqlStatement.toString());

			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {

				serviceInfoOutput.setOriginalStatus(resultSet.getString(1).trim());
				serviceInfoOutput.setOriginalPaid(resultSet.getString(2).trim());
				serviceInfoOutput.setLock(resultSet.getString(3).trim());
				serviceInfoOutput.setClaimType(resultSet.getString(4).trim());

			}


			//Parse Original Paid
			String origPaid = serviceInfoOutput.getOriginalPaid().substring(1,2);
			char origPaidchar = origPaid.charAt(origPaid.length()-1);

			//Parse Original Claim
			String tempOrigClaimNumber = origClaimNumber.substring(6,7);
			char origClaimNumberchar = tempOrigClaimNumber.charAt(origPaid.length()-1);

			//Parse Claim Resolution
			String claimStatus = serviceInfoOutput.getOriginalStatus();
			int intClaimStatus = Integer.parseInt(claimStatus);

			//Exclude criteria check
			if (!StringUtils.isAllBlank(serviceInfoOutput.getLock())){
				serviceInfoOutput.setError("Claim has been excluded from bot process due to this claim is being locked");
				serviceInfoOutput.setExclusion(true);
				throw new IllegalArgumentException("Claim has been excluded from bot process due to this claim is being locked");
			}

			if (!firstClaimPayPend.matches() && claimCount < 1) {
				serviceInfoOutput.setError("Claim has been excluded from bot process. Only one claim in the system. Claim Status doesn't have '*'");
				serviceInfoOutput.setExclusion(true);
				throw new IllegalArgumentException("Claim has been excluded from bot process. Only one claim in the system. Claim Status doesn't have '*'");
			} 
			if (firstClaimPayPend.matches() && secondClaimPayPend.matches() && claimCount == 1){
				serviceInfoOutput.setError("Claim has been excluded from bot process due to original and corrected claim paid pend doesn't have an '*'");
				serviceInfoOutput.setExclusion(true);
				throw new IllegalArgumentException("Claim has been excluded from bot process due to original and corrected claim paid pend doesn't have an '*'");
			}
			if ((origPaidchar != 'Y') && (claimCount > 1)) {
				serviceInfoOutput.setError("Claim has been excluded from bot process due this claim is a non-paid claim. Original paid flag is different than Y");
				serviceInfoOutput.setExclusion(true);
				throw new IllegalArgumentException("Claim has been excluded from bot process due this claim is a non-paid claim. Original paid flag is different than Y");
			}
			//checking claim status of original claim 
			if ((intClaimStatus >= 30 && intClaimStatus <= 49) && ((!firstClaimPayPend.matches() && claimCount < 1) || (firstClaimPayPend.matches() && secondClaimPayPend.matches() && claimCount == 1))) {
				serviceInfoOutput.setError("Claim has been excluded from bot process due to this claim is at pended status. Original claim is missing and claim resolution is between 30-49");
				serviceInfoOutput.setExclusion(true);
				throw new IllegalArgumentException("Claim has been excluded from bot process due to this claim is at pended status. Original claim resolution is between 30-49");
			}
			if (origClaimNumberchar == 'T'){
				serviceInfoOutput.setError("Claim has been excluded from bot process due to this claim is an ITS claim");
				serviceInfoOutput.setExclusion(true);
				throw new IllegalArgumentException("Claim has been excluded from bot process due to this claim is an ITS claim");
			}



			/*
			 * 
			 * QUERY V
			 * goal: fetch count for original claim cross reference 
			 * 
			 */
			sqlStatement.setLength(0);
			sqlStatement.append(" SELECT count(*)as orig_xref");
			sqlStatement.append(" FROM  clxref ");
			sqlStatement.append(" WHERE claim_nbr = '" + origClaimNumber +" ' ");
			logger.info("SQL: {}", sqlStatement);

			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			resultSet = preparedStatement.executeQuery();
			String origXref = null; 
			String corrXref = null;


			while (resultSet.next()) {
				serviceInfoOutput.setOrigxref(resultSet.getString(1));
				origXref = xrefParsing(serviceInfoOutput.getOrigxref());

			}
			serviceInfoOutput.setOrigxref(origXref);


			/*
			 * 
			 * QUERY V
			 * goal: fetch count for corrected claim cross reference 
			 *  
			 */
			sqlStatement.setLength(0);
			sqlStatement.append(" SELECT count(*)as cc_xref");
			sqlStatement.append(" FROM  clxref ");
			sqlStatement.append(" WHERE claim_nbr = ?");
			logger.info("SQL: {}", sqlStatement);

			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				serviceInfoOutput.setCorrxref(resultSet.getString(1).trim());
				corrXref = xrefParsing(serviceInfoOutput.getCorrxref());

			}
			serviceInfoOutput.setCorrxref(corrXref);



			//End block
		}
		catch (SQLException se) {
			this.processException(se);
		}
		catch (IllegalArgumentException ex){
			this.processException(ex);
		}
		catch (Exception e) {
			this.processException(e);
		}
		finally {
			displayResults();
			this.closeConnections();
		}
		return serviceInfoOutput;
	}
	
	/**
	 * Public method
	 * 
	 * Retrieve just the member number associate with the current claim number 
	 * 
	 * @return amisys dto member number
	 */
	public AmisysDTO queryMemberNumber(String dataSource, String claimNumber){

		this.setDbFunctionDelete(dbFunctionDelete);

		try{
			this.initializeConnection(dataSource, "");

			//Query #1
			sqlStatement.append(" SELECT member_nbr");
			sqlStatement.append(" FROM  service_x ");
			sqlStatement.append(" WHERE claim_nbr = ?");
			logger.info("SQL: {}", sqlStatement);


			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			resultSet = preparedStatement.executeQuery();


			while (resultSet.next()) {
				serviceInfoOutput.setMemberNumber(resultSet.getString(1));
			}

		}
		catch (SQLException se) {
			this.processException(se);
		}
		catch (Exception e) {
			this.processException(e);
		}
		finally {
			displayResults();
			this.closeConnections();
		}
		return serviceInfoOutput;
	}
	
	
	/**
	 * ****************************************************************************************
	 * ****************************************************************************************
	 * P8 - Amisys High Dollar Injectables & Device Intensive - PROFESSIONAL - JavaBean Start *
	 * ****************************************************************************************
	 * ****************************************************************************************
	 */
	public List<PEightDTO> professionalClaims(String haDataSource, String baDataSource, String claimNumber)
	{
		this.setDbFunctionDelete(dbFunctionDelete);
		String system = "";
		ArrayList<PEightDTO> jsonFinalList = new ArrayList<>();
		final String entityIdCd = "82%";
		final String provIdQual = "XX";
		//Exact match to Amisys DB
		claimNumber = claimNumber + "%";
		
		try {
			this.initializeConnection(baDataSource, "");
			
			/*
			 * #1 Check System - BAA
			 * This will prove to be null if no value is fetch
			 */
			sqlStatement.append(" SELECT unique(system_cd) ");
			sqlStatement.append(" FROM as_claim ");
			sqlStatement.append(" WHERE icn_nbr like ?");
			logger.info("SQL: {}", sqlStatement);

			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			resultSet = preparedStatement.executeQuery();

			if(resultSet.next()) {
				system = resultSet.getString(1);
			}
			else {
				this.closeConnections();
				this.initializeConnection(haDataSource, "");
				/*
				 * #2 Check System - BAA
				 * This will prove to be null if no value is fetch
				 */
				sqlStatement.setLength(0);
				sqlStatement.append(" SELECT unique(system_cd) ");
				sqlStatement.append(" FROM as_claim ");
				sqlStatement.append(" WHERE icn_nbr like ?");
				logger.info("SQL: {}", sqlStatement);

				preparedStatement = connection.prepareStatement(sqlStatement.toString());
				preparedStatement.setString(1, claimNumber);
				resultSet = preparedStatement.executeQuery();
				
				if (resultSet.next()) {
					system = resultSet.getString(1);
				}
				else {
					//system is null
					system = "n/a";
				}
			}
			
			//Heath Advantage
			if (system.trim().toLowerCase().contains("habl")) {
				/*
				 * #3 HABL - Retrieve data, proc, charge amt, and unit count
				 * This will prove to be null if any value is fetch
				 */
				sqlStatement.setLength(0);
				sqlStatement.append(" SELECT a.icn_nbr, a.line_seq_nbr, b.date_from, a.sv1_proc_cd, a.sv1_line_charg_amt, a.sv1_svc_unit_count");
				sqlStatement.append(" FROM  as_line_item a, as_date b ");
				sqlStatement.append(" WHERE a.system_cd = '" + system+"'");
				sqlStatement.append(" AND a.icn_nbr LIKE ?");
				sqlStatement.append(" AND a.system_cd = b.system_cd");
				sqlStatement.append(" AND a.icn_nbr = b.icn_nbr");
				sqlStatement.append(" AND a.line_seq_nbr = b.line_seq_nbr");
				sqlStatement.append(" AND b.date_cob_seq NOT LIKE '0%'");
				logger.info("SQL: {}", sqlStatement);
				
				preparedStatement = connection.prepareStatement(sqlStatement.toString());
				preparedStatement.setString(1, claimNumber);
				resultSet = preparedStatement.executeQuery();
				
				while (resultSet.next()) {
					PEightDTO haProfClaimsOutput = new PEightDTO();
					haProfClaimsOutput.setSystemCd(system);
					haProfClaimsOutput.setClaimNumber(resultSet.getString(1).trim());
					haProfClaimsOutput.setServiceLine(resultSet.getString(2).trim());
					haProfClaimsOutput.setDateOfService(resultSet.getString(3).trim());
					haProfClaimsOutput.setHcpcs(resultSet.getString(4).trim());
					haProfClaimsOutput.setServiceChargeAmount(resultSet.getString(5).trim());
					haProfClaimsOutput.setServiceCount(resultSet.getString(6).trim());
					jsonFinalList.add(haProfClaimsOutput);

				}
				/*
				 * #4 Rendering NPI Call
				 * This will prove to be null if any value is fetch
				 */
				sqlStatement.setLength(0);
				sqlStatement.append(" SELECT distinct prov_id");
				sqlStatement.append(" FROM  as_provider ");
				sqlStatement.append(" WHERE system_cd = " + "'" + system + "'");
				sqlStatement.append(" AND icn_nbr LIKE ?");
				sqlStatement.append(" AND entity_id_cd LIKE " +"'"+ entityIdCd + "'");
				sqlStatement.append(" AND prov_id_qual LIKE " + "'" + provIdQual + "'");
				logger.info("SQL: {}", sqlStatement);
				
				preparedStatement = connection.prepareStatement(sqlStatement.toString());
				preparedStatement.setString(1, claimNumber);
				resultSet = preparedStatement.executeQuery();
				
				while (resultSet.next()) {
					PEightDTO haProfClaimsOutput = new PEightDTO();
					haProfClaimsOutput.setNpi(resultSet.getString(1).trim());
					jsonFinalList.add(haProfClaimsOutput);
				}
				
				
			}
			else {
				//Blue Advantage
				if (system.trim().toLowerCase().contains("baa")) {
					/*
					 * #3 BAA - Retrieve data, proc, charge amt, and unit count
					 * This will prove to be null if any value is fetch
					 */
					sqlStatement.setLength(0);
					sqlStatement.append(" SELECT a.icn_nbr, a.line_seq_nbr, b.date_from, a.sv1_proc_cd, a.sv1_line_charg_amt, a.sv1_svc_unit_count");
					sqlStatement.append(" FROM  as_line_item a, as_date b ");
					sqlStatement.append(" WHERE a.system_cd = " + "'" + system + "'");
					sqlStatement.append(" AND a.icn_nbr LIKE ?");
					sqlStatement.append(" AND a.system_cd = b.system_cd");
					sqlStatement.append(" AND a.icn_nbr = b.icn_nbr");
					sqlStatement.append(" AND a.line_seq_nbr = b.line_seq_nbr");
					sqlStatement.append(" AND b.date_cob_seq NOT LIKE '0%'");
					logger.info("SQL: {}", sqlStatement);
					
					preparedStatement = connection.prepareStatement(sqlStatement.toString());
					preparedStatement.setString(1, claimNumber);
					resultSet = preparedStatement.executeQuery();
					
					while (resultSet.next()) {
						PEightDTO baProfClaimsOutput = new PEightDTO();
						baProfClaimsOutput.setSystemCd(system);
						baProfClaimsOutput.setClaimNumber(resultSet.getString(1).trim());
						baProfClaimsOutput.setServiceLine(resultSet.getString(2).trim());
						baProfClaimsOutput.setDateOfService(resultSet.getString(3).trim());
						baProfClaimsOutput.setHcpcs(resultSet.getString(4).trim());
						baProfClaimsOutput.setServiceChargeAmount(resultSet.getString(5).trim());
						baProfClaimsOutput.setServiceCount(resultSet.getString(6).trim());
						jsonFinalList.add(baProfClaimsOutput);

					}
					
					/*
					 * #4 Rendering NPI Call
					 * This will prove to be null if any value is fetch
					 */
					sqlStatement.setLength(0);
					sqlStatement.append(" SELECT distinct prov_id");
					sqlStatement.append(" FROM  as_provider ");
					sqlStatement.append(" WHERE system_cd = " + "'" + system + "'");
					sqlStatement.append(" AND icn_nbr LIKE ?");
					sqlStatement.append(" AND entity_id_cd LIKE " +"'"+ entityIdCd + "'");
					sqlStatement.append(" AND prov_id_qual LIKE " + "'" + provIdQual + "'");
					logger.info("SQL: {}", sqlStatement);
					
					preparedStatement = connection.prepareStatement(sqlStatement.toString());
					preparedStatement.setString(1, claimNumber);
					resultSet = preparedStatement.executeQuery();
					
					while (resultSet.next()) {
						PEightDTO baProfClaimsOutput = new PEightDTO();
						baProfClaimsOutput.setNpi(resultSet.getString(1).trim());
						jsonFinalList.add(baProfClaimsOutput);
					}
					
				}
				else {
					//System is NULL
					PEightDTO nullProfClaimsOutput = new PEightDTO();
					nullProfClaimsOutput.setSystemCd("n/a");
					nullProfClaimsOutput.setClaimNumber("n/a");
					nullProfClaimsOutput.setServiceLine("n/a");
					nullProfClaimsOutput.setDateOfService("n/a");
					nullProfClaimsOutput.setHcpcs("n/a");
					nullProfClaimsOutput.setServiceChargeAmount("n/a");
					nullProfClaimsOutput.setServiceCount("n/a");
					nullProfClaimsOutput.setNpi("n/a");
					jsonFinalList.add(nullProfClaimsOutput);
				}
			}
		}
		catch (SQLException se) {
			this.processException(se);
		}
		catch (Exception e) {
			this.processException(e);
		}
		finally {
			displayResults();
			this.closeConnections();
		}

		return jsonFinalList;
	}
	
	
	
	/**
	 * ************************************************************************************
	 * ************************************************************************************
	 * P8 - Amisys High Dollar Injectables & Device Intensive - FACILITY - JaveBean Start *
	 * ************************************************************************************
	 * ************************************************************************************
	 */
	
	public List<PEightDTO> facilityClaims(String haDataSource,String baDataSource,String claimNumber)
	{
		this.setDbFunctionDelete(dbFunctionDelete);
		String system = "";
		ArrayList<PEightDTO> jsonFinalList = new ArrayList<>();
		final String entityIdCd = "85%";
		final String provIdQual = "XX";
		//Exact match to Amisys DB
		claimNumber = claimNumber + "%";
		
		try {
			this.initializeConnection(baDataSource, "");
			
			/*
			 * #1 Check System - BAA
			 * This will prove to be null if no value is fetch
			 */
			sqlStatement.append(" SELECT unique(system_cd) ");
			sqlStatement.append(" FROM as_claim ");
			sqlStatement.append(" WHERE icn_nbr like ?");
			logger.info("SQL: {}", sqlStatement);

			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			resultSet = preparedStatement.executeQuery();

			if(resultSet.next()) {
				system = resultSet.getString(1);
			}
			else {
				this.closeConnections();
				this.initializeConnection(haDataSource, "");
				/*
				 * #2 Check System - BAA
				 * This will prove to be null if no value is fetch
				 */
				sqlStatement.setLength(0);
				sqlStatement.append(" SELECT unique(system_cd) ");
				sqlStatement.append(" FROM as_claim ");
				sqlStatement.append(" WHERE icn_nbr like ?");
				logger.info("SQL: {}", sqlStatement);

				preparedStatement = connection.prepareStatement(sqlStatement.toString());
				preparedStatement.setString(1, claimNumber);
				resultSet = preparedStatement.executeQuery();
				
				if (resultSet.next()) {
					system = resultSet.getString(1);
				}
				else {
					//system is null
					system = "n/a";
				}
			}
			
			//Heath Advantage
			if (system.trim().toLowerCase().contains("habl")) {
				/*
				 * #3 HABL - Retrieve data, proc, charge amt, and unit count
				 * This will prove to be null if any value is fetch
				 */
				sqlStatement.setLength(0);
				sqlStatement.append(" SELECT a.icn_nbr, a.line_seq_nbr, b.date_from, a.sv2_revenue_cd, a.sv2_proc_cd, a.sv2_line_charg_amt, a.sv2_svc_unit_count");
				sqlStatement.append(" FROM  as_line_item a, as_date b ");
				sqlStatement.append(" WHERE a.system_cd = '" + system +"'");
				sqlStatement.append(" AND a.icn_nbr LIKE ?");
				sqlStatement.append(" AND a.system_cd = b.system_cd");
				sqlStatement.append(" AND a.icn_nbr = b.icn_nbr");
				sqlStatement.append(" AND a.line_seq_nbr = b.line_seq_nbr");
				sqlStatement.append(" AND b.date_cob_seq NOT LIKE '0%'");
				logger.info("SQL: {}", sqlStatement);
				
				preparedStatement = connection.prepareStatement(sqlStatement.toString());
				preparedStatement.setString(1, claimNumber);
				resultSet = preparedStatement.executeQuery();
				
				while (resultSet.next()) {
					PEightDTO haProfClaimsOutput = new PEightDTO();
					haProfClaimsOutput.setSystemCd(system);
					haProfClaimsOutput.setClaimNumber(resultSet.getString(1).trim());
					haProfClaimsOutput.setServiceLine(resultSet.getString(2).trim());
					haProfClaimsOutput.setDateOfService(resultSet.getString(3).trim());
					haProfClaimsOutput.setRevenueCd(resultSet.getString(4).trim());
					haProfClaimsOutput.setHcpcs(resultSet.getString(5).trim());
					haProfClaimsOutput.setServiceChargeAmount(resultSet.getString(6).trim());
					haProfClaimsOutput.setServiceCount(resultSet.getString(7).trim());
					jsonFinalList.add(haProfClaimsOutput);

				}
				/*
				 * #4 Rendering NPI Call
				 * This will prove to be null if any value is fetch
				 */
				sqlStatement.setLength(0);
				sqlStatement.append(" SELECT distinct prov_id");
				sqlStatement.append(" FROM  as_provider ");
				sqlStatement.append(" WHERE system_cd = " + "'" + system + "'");
				sqlStatement.append(" AND icn_nbr LIKE ?");
				sqlStatement.append(" AND entity_id_cd LIKE " +"'"+ entityIdCd + "'");
				sqlStatement.append(" AND prov_id_qual LIKE " + "'" + provIdQual + "'");
				logger.info("SQL: {}", sqlStatement);
				
				preparedStatement = connection.prepareStatement(sqlStatement.toString());
				preparedStatement.setString(1, claimNumber);
				resultSet = preparedStatement.executeQuery();
				
				while (resultSet.next()) {
					PEightDTO haProfClaimsOutput = new PEightDTO();
					haProfClaimsOutput.setNpi(resultSet.getString(1).trim());
					jsonFinalList.add(haProfClaimsOutput);
				}
				/*
				 * #5 Get total claim charges
				 * This will prove to be null if any value is fetch
				 */
				sqlStatement.setLength(0);
				sqlStatement.append(" SELECT total_clm_charges");
				sqlStatement.append(" FROM  as_claim ");
				sqlStatement.append(" WHERE icn_nbr LIKE ? ");
				logger.info("SQL: {}", sqlStatement);
				
				preparedStatement = connection.prepareStatement(sqlStatement.toString());
				preparedStatement.setString(1, claimNumber);
				resultSet = preparedStatement.executeQuery();
				
				while (resultSet.next()) {
					PEightDTO haProfClaimsOutput = new PEightDTO();
					haProfClaimsOutput.setTotalCharges(resultSet.getString(1).trim());
					jsonFinalList.add(haProfClaimsOutput);
				}
				
				
			}
			else {
				//Blue Advantage
				if (system.trim().toLowerCase().contains("baa")) {
					/*
					 * #3 BAA - Retrieve data, proc, charge amt, and unit count
					 * This will prove to be null if any value is fetch
					 */
					sqlStatement.setLength(0);
					sqlStatement.append(" SELECT a.icn_nbr, a.line_seq_nbr, b.date_from, a.sv2_revenue_cd, a.sv2_proc_cd, a.sv2_line_charg_amt, a.sv2_svc_unit_count");
					sqlStatement.append(" FROM  as_line_item a, as_date b ");
					sqlStatement.append(" WHERE a.system_cd = '" + system +"'");
					sqlStatement.append(" AND a.icn_nbr LIKE ?");
					sqlStatement.append(" AND a.system_cd = b.system_cd");
					sqlStatement.append(" AND a.icn_nbr = b.icn_nbr");
					sqlStatement.append(" AND a.line_seq_nbr = b.line_seq_nbr");
					sqlStatement.append(" AND b.date_cob_seq NOT LIKE '0%'");
					logger.info("SQL: {}", sqlStatement);
					
					preparedStatement = connection.prepareStatement(sqlStatement.toString());
					preparedStatement.setString(1, claimNumber);
					resultSet = preparedStatement.executeQuery();
					
					while (resultSet.next()) {
						PEightDTO baProfClaimsOutput = new PEightDTO();
						baProfClaimsOutput.setSystemCd(system);
						baProfClaimsOutput.setClaimNumber(resultSet.getString(1).trim());
						baProfClaimsOutput.setServiceLine(resultSet.getString(2).trim());
						baProfClaimsOutput.setDateOfService(resultSet.getString(3).trim());
						baProfClaimsOutput.setRevenueCd(resultSet.getString(4).trim());
						baProfClaimsOutput.setHcpcs(resultSet.getString(5).trim());
						baProfClaimsOutput.setServiceChargeAmount(resultSet.getString(6).trim());
						baProfClaimsOutput.setServiceCount(resultSet.getString(7).trim());
						jsonFinalList.add(baProfClaimsOutput);

					}
					
					/*
					 * #4 Rendering NPI Call
					 * This will prove to be null if any value is fetch
					 */
					sqlStatement.setLength(0);
					sqlStatement.append(" SELECT distinct prov_id");
					sqlStatement.append(" FROM  as_provider ");
					sqlStatement.append(" WHERE system_cd = " + "'" + system + "'");
					sqlStatement.append(" AND icn_nbr LIKE ?");
					sqlStatement.append(" AND entity_id_cd LIKE " +"'"+ entityIdCd + "'");
					sqlStatement.append(" AND prov_id_qual LIKE " + "'" + provIdQual + "'");
					logger.info("SQL: {}", sqlStatement);
					
					preparedStatement = connection.prepareStatement(sqlStatement.toString());
					preparedStatement.setString(1, claimNumber);
					resultSet = preparedStatement.executeQuery();
					
					while (resultSet.next()) {
						PEightDTO baProfClaimsOutput = new PEightDTO();
						baProfClaimsOutput.setNpi(resultSet.getString(1).trim());
						jsonFinalList.add(baProfClaimsOutput);
					}
					/*
					 * #5 Get total claim charges
					 * This will prove to be null if any value is fetch
					 */
					sqlStatement.setLength(0);
					sqlStatement.append(" SELECT total_clm_charges");
					sqlStatement.append(" FROM  as_claim ");
					sqlStatement.append(" WHERE icn_nbr LIKE ? ");
					logger.info("SQL: {}", sqlStatement);
					
					preparedStatement = connection.prepareStatement(sqlStatement.toString());
					preparedStatement.setString(1, claimNumber);
					resultSet = preparedStatement.executeQuery();
					
					while (resultSet.next()) {
						PEightDTO baProfClaimsOutput = new PEightDTO();
						baProfClaimsOutput.setTotalCharges(resultSet.getString(1).trim());
						jsonFinalList.add(baProfClaimsOutput);
					}
					
				}
				else {
					//System is null for all
					PEightDTO nullProfClaimsOutput = new PEightDTO();
					nullProfClaimsOutput.setSystemCd("n/a");
					nullProfClaimsOutput.setClaimNumber("n/a");
					nullProfClaimsOutput.setServiceLine("n/a");
					nullProfClaimsOutput.setDateOfService("n/a");
					nullProfClaimsOutput.setHcpcs("n/a");
					nullProfClaimsOutput.setServiceChargeAmount("n/a");
					nullProfClaimsOutput.setServiceCount("n/a");
					nullProfClaimsOutput.setNpi("n/a");
					jsonFinalList.add(nullProfClaimsOutput);
				}
			}
		}
		catch (SQLException se) {
			this.processException(se);
		}
		catch (Exception e) {
			this.processException(e);
		}
		finally {
			displayResults();
			this.closeConnections();
		}

		return jsonFinalList;
	}
	
	/**
	 * ************************************************************************************
	 * ************************************************************************************
	 * FEP Claims - ProvWeb - DB2 - JaveBean Start*
	 * 
	 * Description: This method is responsible to fetch FEP claims from DB2 under the V05AS_* schema
	 * Properties for DB2 connection coming from isshared prop files
	 * This method will be called by the driver method once amisys claim with 12 length 
	 * digits is not found. This can also be in a separated endpoint. 
	 * ************************************************************************************
	 * ************************************************************************************
	 */
	public List<PEightDTO> fepClaims(String dataSource,String scheme,String claimNumber){
		ArrayList<PEightDTO> jsonFinalList = new ArrayList<>();
		
		try {
			this.initializeConnection(dataSource, "");

			sqlStatement.append(" SELECT SYSTEM_CD, ICN_NBR, TOTAL_CLM_CHARGES");
			sqlStatement.append(" FROM " + scheme + ".V05AS_CLAIM" );
			sqlStatement.append(" WHERE ICN_NBR = ?");
			logger.info("SQL: {}", sqlStatement);

			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				PEightDTO fepClaimsOutput = new PEightDTO();
				fepClaimsOutput.setSystemCd(resultSet.getString(1));
				fepClaimsOutput.setClaimNumber(resultSet.getString(2).trim());
				fepClaimsOutput.setTotalCharges(resultSet.getString(3).trim());
				jsonFinalList.add(fepClaimsOutput);

			}
			
			
		}
		catch (SQLException se) {
			this.processException(se);
		}
		catch (Exception e) {
			this.processException(e);
		}
		finally {
			displayResults();
			this.closeConnections();
		}

		return jsonFinalList;
	}



	/**
	 * ****************************************************************************************
	 * ****************************************************************************************
	 * JavaBean support methods - Exception messages; String Parsing; Input validation*
	 * ****************************************************************************************
	 * ****************************************************************************************
	 */
	
	/**
	 * Public method
	 * 
	 * For any error message we throw this method 
	 * 
	 * @return amisys dto error message
	 */
	public List<PEightDTO> exceptionProfFacMessage (String error){
		ArrayList<PEightDTO> arrayList = new ArrayList<>();
		
		PEightDTO errorMessage = new PEightDTO();
		errorMessage.setError(error);
		arrayList.add(errorMessage);
		return arrayList;
	}
	

	/**
	 * Public method
	 * 
	 * For any error message we throw this method 
	 * 
	 * @return amisys dto error message
	 */
	public AmisysDTO exceptionMessage (){

		AmisysDTO errorMessage = new AmisysDTO();
		errorMessage.setError("An exception has been encountered with the claim entered! Please, make sure you enter a valid claim according to the desired environment");
		return errorMessage;
	}



	/**
	 * Public method
	 * 
	 * Performs xref check and parsing 
	 * 
	 * @return string for true or false
	 */
	public String xrefParsing (String xref){
		int xrefInt = Integer.parseInt(xref);
		String newXref;


		if (xrefInt > 0){
			newXref = "TRUE";
		}
		else {
			newXref = "FALSE";
		}

		return newXref;
	}



	/**
	 * Private method
	 * 
	 * Performs input validation following basic java guidelines. 
	 * 
	 * Validate a claim number before it sends the request for data base connections 
	 * 
	 * @return boolean for true has error or false
	 * 
	 */
	private boolean invalidInput(String input, boolean yearCheck) {
		boolean hasError = false; 
		if ("".equalsIgnoreCase(input) || input == null) {
			hasError = true;
			logger.info(input,"{} Field Required!");
		}
		if (!hasError) {
			input = input.replaceAll(" ","");
			Year currentYear = Year.now();
			if (!ValidateUtilities.isValidateMaxStringLength(input.trim(), 14)) { // &&  serviceInfoOutput.getClaimType().equals("H")) {
				hasError = true;
				logger.info("Invalid {} Length! ",input);   
			}
			else if (!input.matches("[a-zA-Z0-9]+")){
				hasError = true;
				logger.info(input, "{} It is not Alphanumeric!");
			}
			else if (!ValidateUtilities.isValidateCharactersWithHyphen(input)) {
				hasError = true;
				logger.info(input,"{} Contains Invalid Characters!");                
			}
			else if (yearCheck ) {
				if (Integer.valueOf(input.substring(0, 4)) <= Integer.valueOf(currentYear.minusYears(3).toString())) {
					hasError = true;
					logger.info(input," Date of service in 2020, possibility of original claim being archived");
				}
			}
		}

		return hasError; 	    
	}
}


