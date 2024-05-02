package net.abcbs.rpa.javabeans;
/***********************************************************************************************************************************************************************
 * @author ABCBS resource
 * 
 * Description: ClaimLineJavaBean class will be used to perform the proper connection with Oracle database and query the correct value required by the user 
 * 
 * Project: NP Pended Claims
 ***********************************************************************************************************************************************************************/

import net.abcbs.rpa.dto.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.abcbs.issh.util.pub.javabeans.IsSharedJavaBean;

public class ClaimLineJavaBean extends IsSharedJavaBean {
	//Local constants
	private static final String INNERJOIN = "INNER JOIN";
	private static Logger logger = LogManager.getLogger(ClaimLineJavaBean.class);
	private static final String MAX = "MAX";
	private static final String SUBSTR = "SUBSTR";
	
	
	public int countCovidClaimLines(String dataSource, String claimNumber)
	{
		this.setDbFunctionDelete(dbFunctionDelete);
		int count = 0; 
		try {
			this.initializeConnection(dataSource, "");

			sqlStatement.append(" SELECT COUNT(claim_nbr)");
			sqlStatement.append(" FROM  service_x");
			sqlStatement.append(" WHERE benefit LIKE '%COVID%' AND claim_nbr = ?");
			logger.info("SQL: {}", sqlStatement);

			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			
			count = resultSet.getInt(1);
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

		return count;
	}
	
	public List<ClaimLineDTO> serviceInfoClaimLines(String dataSource, String claimNumber){
		
		this.setDbFunctionDelete(dbFunctionDelete);
		ArrayList<ClaimLineDTO> arrayList = new ArrayList<>();
		int row = 1;
		
		
		try{
			this.initializeConnection(dataSource, "");

			sqlStatement.append(" SELECT s.claim_nbr as claim_number, substr(s.serv_nbr,13,4) as service_line, s.ymdeff as eff_date, s.location as lc,  s.treatment_type as tt, s.proc_nbr as proc, s.amtallow_p as allow, s.amtbenefit as pay, s.status_x as status, \r\n"
					+ "substr(s.ex_array,0,2) as EX1, substr(s.ex_array,3,2) as EX2, substr(s.ex_array,5,2) as EX3, \r\n"
					+ "substr(s.ex_array,7,2) as EX4, substr(s.ex_array,9,2) as EX5, substr(s.ex_array,11,2) as EX6, paid as py");
			sqlStatement.append(" FROM  service_x s");
			sqlStatement.append(" " + INNERJOIN + "(SELECT claim_nbr, " + MAX +"(" + SUBSTR +"(serv_nbr,15, 2)) as max_service_line FROM service_x WHERE claim_nbr = ? GROUP BY claim_nbr ) t");
			sqlStatement.append(" ON s.claim_nbr = t.claim_nbr AND " + SUBSTR +"(serv_nbr,15, 2) = t.max_service_line");
			sqlStatement.append(" WHERE s.status_x NOT IN( 50,51 )");
			logger.info("SQL: {}", sqlStatement);

			preparedStatement = connection.prepareStatement(sqlStatement.toString());
			preparedStatement.setString(1, claimNumber);
			resultSet = preparedStatement.executeQuery();
	
			
			
			while (resultSet.next()) {
				ClaimLineDTO serviceInfoOutput = new ClaimLineDTO();
				serviceInfoOutput.setRow(row);
				//claim_nbr
				serviceInfoOutput.setClaimNumber(resultSet.getString(1));
				//serv_line
				serviceInfoOutput.setServiceLine(resultSet.getString(2));
				//eff_date
				serviceInfoOutput.setEffectiveDate(resultSet.getString(3));
				//location
				serviceInfoOutput.setLc(resultSet.getString(4));
				//treatment_type
				serviceInfoOutput.setTt(resultSet.getString(5));
				//proc#
				serviceInfoOutput.setProc(resultSet.getString(6));
				//allow
				serviceInfoOutput.setAllow(toLocalCurrency(toDecimal(resultSet.getString(7))));
				//pay
				serviceInfoOutput.setPay(toLocalCurrency(toDecimal(resultSet.getString(8))));
				//ST
				serviceInfoOutput.setSt(resultSet.getString(9));
				//EX
				serviceInfoOutput.setEx1(resultSet.getString(10));//1
				serviceInfoOutput.setEx2(resultSet.getString(11));//2
				serviceInfoOutput.setEx3(resultSet.getString(12));//3
				serviceInfoOutput.setEx4(resultSet.getString(13));//4
				serviceInfoOutput.setEx5(resultSet.getString(14));//5
				serviceInfoOutput.setEx6(resultSet.getString(15));//6
				//PY
				serviceInfoOutput.setPy(resultSet.getString(16));//6
				
				arrayList.add(serviceInfoOutput);
				row++;
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
		return arrayList;
	}
	
	
	
	public List<ClaimLineDTO> exceptionMessage (){
		ArrayList<ClaimLineDTO> arrayList = new ArrayList<>();
		
		ClaimLineDTO errorMessage = new ClaimLineDTO();
		errorMessage.setError("An exception has been encountered with the claim entered! Please, make sure you enter a valid claim according to the desired environment");
		arrayList.add(errorMessage);
		return arrayList;
	}
	
	
	
	public static String toDecimal (String origStr) {

        if (!"".equals(origStr)){
            int totalSize = origStr.length();
            if (totalSize > 2 ) {
                return origStr.substring(0, totalSize - 2) + "." + origStr.substring(totalSize - 2);
            }
            else {
                return "0." + origStr;
            }
        }
        return "Error: Current string is null, decimal manipulation cannot be done. Please, review input variable";
    }
	
	
	
	public static String toLocalCurrency (String money) {

        if (!"".equals(money)) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            Double value = Double.parseDouble(money);
            return formatter.format(value);
        } else {
            return "Error: Current string is null, currency manipulation cannot be done. Please, review input variable\"";
        }
    }
	

}


