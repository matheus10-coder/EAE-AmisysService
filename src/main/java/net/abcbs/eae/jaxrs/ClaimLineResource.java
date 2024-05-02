package net.abcbs.eae.jaxrs;
import javax.ws.rs.GET;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import net.abcbs.issh.util.pub.common.IsSharedApplicationDataObject;
import net.abcbs.issh.util.pub.common.ValidateUtilities;
import net.abcbs.rpa.dto.ClaimLineDTO;
import net.abcbs.rpa.dto.PEightDTO;
import net.abcbs.rpa.dto.AmisysDTO;
import net.abcbs.rpa.javabeans.AmisysJavaBean;
import net.abcbs.rpa.javabeans.ClaimLineJavaBean;

/***********************************************************************************************************************************************************************
 * @Author mfribeiro
 * 
 * Body for all the methods used for the REST Web service
 * 
 * Description: ClaimLineResource class is the application resource level which the main methods will be called in order to return the message to the user 
 * 
 * Project: NP Pended Claims
 ***********************************************************************************************************************************************************************/
@Path("/claims")
@OpenAPIDefinition(
		servers = {
				@Server(
					description = "localhost",
					url = "localhost:9080/RPAAmisysClaimLineService/resources"),
				@Server(
					description = "development",
					url = "https://isshareddev.abcbs.net/RPAAmisysClaimLineService/resources"),
				@Server(
						description = "test",
						url = "https://issharedtst.abcbs.net/RPAAmisysClaimLineService/resources"),
				@Server(
					description = "stage",
					url = "https://issharedstg.abcbs.net/RPAAmisysClaimLineService/resources"),
				@Server(
					description = "production",
					url = "https://isshared.abcbs.net/RPAAmisysClaimLineService/resources")
		})
@SecurityScheme(name = "basicAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "basic",
		description = "Username and Password are used for authorization")

public class ClaimLineResource {
	
    /**
     * Private method
     * 
     * Data object to get database information
     * 
     * Utilizing isSharedApplication class
     * 
     */
	private static Logger logger = LogManager.getLogger(ClaimLineResource.class);
	private static IsSharedApplicationDataObject isSharedApplicationDataObject = null;
	private static String jndiBa = "oracleBaNodeDB";
	private static String jndiHa = "oracleHaNodeDB";
	
	
	static {
		try {
			isSharedApplicationDataObject = new IsSharedApplicationDataObject(Constants.SYSTEM_NAME, Constants.AUTH_KEY, Constants.AUTH_PASS_PHRASE_DEV);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	/**
     * Public method
     * 
     * Method to output successful message from the server
     * 
     * Includes brief instruction on how to use this service
     * 
     * @return string value
     * 
     */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Service base endpoint that you can hit to get a response from the server",
			security = @SecurityRequirement(name = "basicAuth"),
			description = "A base endpoint for this service",
			responses = {
					@ApiResponse(
							description = "JSON response",
							content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ClaimLineMessage.class))),
					@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)	
	public String serviceLineMessage(){
		return "{\"message\": \"This web service is designed to retrieve data values from Amisys databases. IssharedApplication object is responsible to establish the dbconnection. BlueAdvantage Db2jndiName: " + isSharedApplicationDataObject.getOracleBaJndiName() + ". HeathAdvantage Db2jndiName: " + isSharedApplicationDataObject.getOracleHaJndiName() + ". Please use claim number as the unique identifier during the request.  \"}";
	}
	
	/**
     * Public method
     * 
     * Method to output successful message from the server
     * 
     * Includes brief instruction on how to use this service
     * 
     * @return string value
     * 
     */
	@GET
	@Path("/p8")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Service base endpoint that you can hit to get a response from the server",
			security = @SecurityRequirement(name = "basicAuth"),
			description = "A base endpoint for this service",
			responses = {
					@ApiResponse(
							description = "JSON response",
							content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ClaimLineMessage.class))),
					@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)	
	public String pEightBaseEndpointMessage(){
		return "{\"message\": \"This web service finds information which is also presented in P8 application. IssharedApplication object is responsible to establish the dbconnection. BlueAdvantage Db2jndiName: " + isSharedApplicationDataObject.getOracleBaJndiName() + ". HeathAdvantage Db2jndiName: " + isSharedApplicationDataObject.getOracleHaJndiName() + ". Please use claim number as the unique identifier during the request.  \"}";
	}
	
	/**
     * Public method
     * 
     * Calculates the total count of lines for *COVID* given a claim number. 
     * 
     * Message ID is a string type that will be required from the user during the call 
     * 
     * It will go through the claim number validation security guidelines in order to retrieve the final service line count (*COVID*) for that particular claim 
     * 
     * @return returns the total count of lines for *COVID* given a claim number
     * 
     */
	@GET
	@Path("/totalservicelines/{messageId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Purpose for this GET method is to retrieve total count of service lines for COVID benefit claims only",
	security = @SecurityRequirement(name = "basicAuth"),
	description = "Returns counter",
	responses = {
		@ApiResponse(
				description = "A method to simply test your credentials against the web service to see if you are authorized",
				content = @Content(mediaType = "application/json")),
		@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)
	public String covidServiceLineTotal (
			@Parameter(description = "Idetify the string and perform input validation. {messageId} refers to the 'claim number' in BAOracle database",
			required = true)
			@PathParam("messageId") String id) {
		ClaimLineMessage info = new ClaimLineMessage();
		String claimNumber = info.getMessage(id);
		
		if (!invalidInput(claimNumber)){
			ClaimLineJavaBean claimLineJavaBean = new ClaimLineJavaBean();
			return String.valueOf(claimLineJavaBean.countCovidClaimLines("oracleBaNodeDB",claimNumber));
		}
		else {
			return "{\"message\": \"Invalid claim number!\"}";
		}
		
	}
	
	/**
     * Public method
     * 
     * Retrieve mainly pay and effective date but also allowance amt and st code from the services lines for each claim indicated. 
     * 
     * Claim Number is a string type that will be required from the user during the request time 
     * 
     * It will go through the claim number validation security guidelines in order to retrieve the json response
     * 
     * @return all the service line pay and effective dates where pay is greater than 0
     * 
     */
	@GET
	@Path("/serviceinfo/{claimNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Purpose for this GET method is retrieves claim detail info per each  service line, main info required are pay and effective date",
	security = @SecurityRequirement(name = "basicAuth"),
	description = "Returns service line pay amount and effective date",
	responses = {
		@ApiResponse(
				description = "A method to simply test your credentials against the web service to see if you are authorized",
				content = @Content(mediaType = "application/json",
				array = @ArraySchema(schema = @Schema(implementation = ClaimLineDTO.class)))),
		@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)
	public List<ClaimLineDTO> serviceLinePayandEffDate (
			@Parameter(description = "Identify the string and perform input validation. {claimNumber} refers to the 'claim number' in BAOracle database",
			required = true)
			@PathParam("claimNumber") String id) {
		ClaimLineMessage info = new ClaimLineMessage();
		String claimNumber = info.getMessage(id);
		
		ClaimLineJavaBean claimLineJavaBean = new ClaimLineJavaBean();
		
		if (!invalidInput(claimNumber)){
			
			return claimLineJavaBean.serviceInfoClaimLines(isSharedApplicationDataObject.getOracleBaJndiName(), claimNumber);
			//check if result is zero we have no data for that claim
		}
		else {
			
			return claimLineJavaBean.exceptionMessage();
					
		}
		
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
	private boolean invalidInput(String input) {
		boolean hasError = false; 
	       if ("".equalsIgnoreCase(input) || input == null) {
	           hasError = true;
	           logger.info(input,"{} Field Required!");
	       }
	       if (!hasError) {
	           if (!ValidateUtilities.isValidateMaxStringLength(input, 15)) {
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
	       }
       return hasError; 	    
       }

	/**
     * Public method
     * 
     * Performs credential validation 
     * 
     * If user gets a 401 they are unauthorized
     * 
     * @return ClaimLineMessage type message 
     * 
     */
	@GET
	@Path("/test/authorization") 
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Simple test authorization method",
		security = @SecurityRequirement(name = "basicAuth"),
		description = "Test your credentials",
		responses = {
			@ApiResponse(
					description = "A method to simply test your credentials against the web service to see if you are authorized",
					content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Credentials not authorized")}
	)
	public ClaimLineMessage testAuthorization() {
		ClaimLineMessage message = new ClaimLineMessage();
		message.setMessage("authorization valid");
		return message;
	}
	
	
	/**
     * Public method
     * 
     * 
     * @return original claim counter, member number, ex array, effective date and affiliation number
     * 
     */
	@GET
	@Path("/a1a2/dispatcher/{claimNumber}/{memberNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Purpose for this GET method is retrieves claim detail on the header",
	security = @SecurityRequirement(name = "basicAuth"),
	description = "Returns original claim counter, member number, ex array, effective date and affiliation number",
	responses = {
		@ApiResponse(
				description = "A method to simply test your credentials against the web service to see if you are authorized",
				content = @Content(mediaType = "application/json",
				array = @ArraySchema(schema = @Schema(implementation = AmisysDTO.class)))),
		@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)
	public AmisysDTO a1a2Dispatcher (
			@Parameter(description = "Identify the string and perform input validation. {claimNumber} refers to the 'claim number' in HAOracle database",
			required = true)
			@PathParam("claimNumber") String id,
			@Parameter(description = "Identify the string and perform input validation. {memberNumber} refers to the 'member number' in HAOracle database",
			required = true)
			@PathParam("memberNumber") String subId){
		
		ClaimLineMessage info = new ClaimLineMessage();
		String claimNumber = info.getMessage(id);
		String memberNumber = info.getMessage(subId);
		
		AmisysJavaBean amisysJavaBean = new AmisysJavaBean();
		
		if (!invalidInput(claimNumber)){
			if (!invalidInput(memberNumber)){
				return amisysJavaBean.queryServiceX(isSharedApplicationDataObject.getOracleHaJndiName(), claimNumber);
				//oracleHaNodeDB
			}
			else{
				return amisysJavaBean.exceptionMessage();
			}
		}
		else {
			
			return amisysJavaBean.exceptionMessage();
					
		}
		
	}
	
	/**
     * Public method
     * 
     * 
     * @return original claim counter, member number, ex array, effective date and affiliation number
     * 
     */
	@GET
	@Path("/a1a2/checkValues/{claimNumber}/{memberNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Purpose for this GET method is retrieves claim detail on the header",
	security = @SecurityRequirement(name = "basicAuth"),
	description = "Returns original claim counter, member number, ex array, effective date and affiliation number",
	responses = {
		@ApiResponse(
				description = "A method to simply test your credentials against the web service to see if you are authorized",
				content = @Content(mediaType = "application/json",
				array = @ArraySchema(schema = @Schema(implementation = AmisysDTO.class)))),
		@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)
	public String a1a2Message (
			@Parameter(description = "Identify the string and perform input validation. {claimNumber} refers to the 'claim number' in HAOracle database",
			required = true)
			@PathParam("claimNumber") String id,
			@Parameter(description = "Identify the string and perform input validation. {memberNumber} refers to the 'member number' in HAOracle database",
			required = true)
			@PathParam("memberNumber") String subId){
		ClaimLineMessage info = new ClaimLineMessage();
		String claimNumber = info.getMessage(id);
		String memberNumber = info.getMessage(subId);
		
		return "User has entered the following values: " + claimNumber + " and " + memberNumber; 
		
	}
	
	/**
     * Public method
     * 
     * 
     * @return original claim counter, member number, ex array, effective date and affiliation number
     * 
     */
	@GET
	@Path("/a1a2/claim/{claimNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Purpose for this GET method is retrieves claim detail on the header",
	security = @SecurityRequirement(name = "basicAuth"),
	description = "Returns original claim counter, member number, ex array, effective date and affiliation number",
	responses = {
		@ApiResponse(
				description = "A method to simply test your credentials against the web service to see if you are authorized",
				content = @Content(mediaType = "application/json",
				array = @ArraySchema(schema = @Schema(implementation = AmisysDTO.class)))),
		@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)
	public AmisysDTO a1a2Message (
			@Parameter(description = "Identify the string and perform input validation. {claimNumber} refers to the 'claim number' in HAOracle database",
			required = true)
			@PathParam("claimNumber") String id){
		ClaimLineMessage info = new ClaimLineMessage();
		String claimNumber = info.getMessage(id);
		
		AmisysJavaBean amisysJavaBean = new AmisysJavaBean();
		
		return amisysJavaBean.queryMemberNumber(isSharedApplicationDataObject.getOracleHaJndiName(), claimNumber);
	}
	
	
	
	/**
	 * ************************************************************************************
	 * P8 - Amisys High Dollar Injectables & Device Intensive - Start *********************
	 * ************************************************************************************
	 */
	
	/**
     * Public method
     * High Dollar Injectables: Looking at professional claims to be retrieved claim info (P8)
     * @return JSON Format of ICN | Service Line | Data of Service | Revenue Code | HCPCS | Service Charge Amount | 
     * Service Unit Count | NPI  
     * 
     */
	@GET
	@Path("/p8/high-dollar-injectables/prof/{claimNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Purpose for this GET method is retrieves claim detail info currently displayed in P8, main info required are HCPCS, effective date and Service count",
	security = @SecurityRequirement(name = "basicAuth"),
	description = "Returns professional claims information currently displayed in P8 for high dollar injectable",
	responses = {
		@ApiResponse(
				description = "A method to simply test your credentials against the web service to see if you are authorized",
				content = @Content(mediaType = "application/json",
				array = @ArraySchema(schema = @Schema(implementation = ClaimLineDTO.class)))),
		@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)
	public List<PEightDTO> highDollarInjecProfessional (
			@Parameter(description = "Identify the string and perform input validation. {claimNumber} refers to the 'claim number' in BA or HA Oracle database",
			required = true)
			@PathParam("claimNumber") String id) {
		ClaimLineMessage info = new ClaimLineMessage();
		String claimNumber = info.getMessage(id);
		
		AmisysJavaBean amisysJavaBean = new AmisysJavaBean();
		
		if (!invalidInput(claimNumber)){
			
			if (claimNumber.length() == 12) {
				return amisysJavaBean.professionalClaims(isSharedApplicationDataObject.getOracleHaJndiName(),isSharedApplicationDataObject.getOracleBaJndiName(),claimNumber);
			}
			else {
				if (!StringUtils.isBlank(claimNumber)) {
					return amisysJavaBean.fepClaims(isSharedApplicationDataObject.getDb2JndiName(), isSharedApplicationDataObject.getDb2Schema(), claimNumber);
				}
				else {
					return amisysJavaBean.exceptionProfFacMessage("An exception has been encountered with the professional claim entered! Please, make sure you enter a valid 12 digits claim number");
				}
				
			}
			
		}
		else {
			
			return amisysJavaBean.exceptionProfFacMessage("An exception has been encountered with the professional claim entered! Please, make sure you enter a valid claim according to the desired environment");
					
		}
		
	}
	
	
	/**
     * Public method
     * 
     * High Dollar Injectables: Looking at facility claims to be retrieved claim info (P8)
     * 
     * @return JSON Format of ICN | Service Line | Data of Service | Revenue Code | HCPCS | Service Charge Amount | 
     * Service Unit Count | NPI 
     * 
     */
	@GET
	@Path("/p8/high-dollar-injectables/fac/{claimNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Purpose for this GET method is retrieves claim detail info currently displayed in P8, main info required are HCPCS, effective date and Service count",
	security = @SecurityRequirement(name = "basicAuth"),
	description = "Returns facility claims information currently displayed in P8 for high dollar injectable",
	responses = {
		@ApiResponse(
				description = "A method to simply test your credentials against the web service to see if you are authorized",
				content = @Content(mediaType = "application/json",
				array = @ArraySchema(schema = @Schema(implementation = ClaimLineDTO.class)))),
		@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)
	public List<PEightDTO> highDollarInjecInstitutional (
			@Parameter(description = "Identify the string and perform input validation. {claimNumber} refers to the 'claim number' in BA OR HA Oracle database",
			required = true)
			@PathParam("claimNumber") String id) {
		ClaimLineMessage info = new ClaimLineMessage();
		String claimNumber = info.getMessage(id);
		
		AmisysJavaBean amisysJavaBean = new AmisysJavaBean();
		
		if (!invalidInput(claimNumber)){
			
			if (claimNumber.length() == 12) {
				return amisysJavaBean.facilityClaims(isSharedApplicationDataObject.getOracleHaJndiName(),isSharedApplicationDataObject.getOracleBaJndiName(),claimNumber);
			}
			else {
				if (!StringUtils.isBlank(claimNumber)) {
					return amisysJavaBean.fepClaims(isSharedApplicationDataObject.getDb2JndiName(), isSharedApplicationDataObject.getDb2Schema(), claimNumber);
				}
				else {
					return amisysJavaBean.exceptionProfFacMessage("An exception has been encountered with the facility claim entered! Please, make sure you enter a valid 12 digits claim number");
				}
				
			}
		}
		else {
			
			return amisysJavaBean.exceptionProfFacMessage("An exception has been encountered with the facility claim entered! Please, make sure you enter a valid claim according to the desired environment");
					
		}
		
	}
	
	
	/**
     * Public method
     * 
     * Device Intensive: Looking at professional claims to be retrieved claim info (P8)
     * 
     * @return JSON Format of ICN | Service Line | Data of Service | Revenue Code | HCPCS | Service Charge Amount | 
     * Service Unit Count | NPI | Total Charges 
     * 
     */
	@GET
	@Path("/p8/device-intensive/prof/{claimNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Purpose for this GET method is retrieves claim detail info currently displayed in P8, main info required are HCPCS, effective date and Total charges",
	security = @SecurityRequirement(name = "basicAuth"),
	description = "Returns professional claims information currently displayed in P8 for device intensive",
	responses = {
		@ApiResponse(
				description = "A method to simply test your credentials against the web service to see if you are authorized",
				content = @Content(mediaType = "application/json",
				array = @ArraySchema(schema = @Schema(implementation = ClaimLineDTO.class)))),
		@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)
	public List<PEightDTO> deviceIntensiveProfessional (
			@Parameter(description = "Identify the string and perform input validation. {claimNumber} refers to the 'claim number' in BAOracle database",
			required = true)
			@PathParam("claimNumber") String id) {
		ClaimLineMessage info = new ClaimLineMessage();
		String claimNumber = info.getMessage(id);
		
		AmisysJavaBean amisysJavaBean = new AmisysJavaBean();
		
		if (!invalidInput(claimNumber)){
			
			if (claimNumber.length() == 12) {
				return amisysJavaBean.professionalClaims(isSharedApplicationDataObject.getOracleHaJndiName(),isSharedApplicationDataObject.getOracleBaJndiName(),claimNumber);
			}
			else {
				if (!StringUtils.isBlank(claimNumber)) {
					return amisysJavaBean.fepClaims(isSharedApplicationDataObject.getDb2JndiName(), isSharedApplicationDataObject.getDb2Schema(), claimNumber);
				}
				else {
					return amisysJavaBean.exceptionProfFacMessage("An exception has been encountered with the professional claim entered! Please, make sure you enter a valid 12 digits claim number");
				}
				
				
			}
		}
		else {
			
			return amisysJavaBean.exceptionProfFacMessage("An exception has been encountered with the professional claim entered! Please, make sure you enter a valid claim according to the desired environment");
					
		}
		
	}
	
	
	/**
     * Public method
     * 
     * Device Intensive: Looking at facility or institutional claims to be retrieved claim info (P8)
     * 
     * @return JSON Format of ICN | Service Line | Data of Service | Revenue Code | HCPCS | Service Charge Amount | 
     * Service Unit Count | NPI | Total Charges 
     * 
     */
	@GET
	@Path("/p8/device-intensive/fac/{claimNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Purpose for this GET method is retrieves claim detail info currently displayed in P8, main info required are HCPCS, effective date and Total charges",
	security = @SecurityRequirement(name = "basicAuth"),
	description = "Returns facility claims information currently displayed in P8 for device intensive",
	responses = {
		@ApiResponse(
				description = "A method to simply test your credentials against the web service to see if you are authorized",
				content = @Content(mediaType = "application/json",
				array = @ArraySchema(schema = @Schema(implementation = ClaimLineDTO.class)))),
		@ApiResponse(responseCode = "401", description = "Credentials not authorized") }
	)
	public List<PEightDTO> deviceIntensiveInstitutional (
			@Parameter(description = "Identify the string and perform input validation. {claimNumber} refers to the 'claim number' in BAOracle database",
			required = true)
			@PathParam("claimNumber") String id) {
		ClaimLineMessage info = new ClaimLineMessage();
		String claimNumber = info.getMessage(id);
		
		AmisysJavaBean amisysJavaBean = new AmisysJavaBean();
		
		if (!invalidInput(claimNumber)){
			
			if (claimNumber.length() == 12) {
				return amisysJavaBean.facilityClaims(isSharedApplicationDataObject.getOracleHaJndiName(),isSharedApplicationDataObject.getOracleBaJndiName(),claimNumber);
			}
			else {
				if (!StringUtils.isBlank(claimNumber)) {
					return amisysJavaBean.fepClaims(isSharedApplicationDataObject.getDb2JndiName(), isSharedApplicationDataObject.getDb2Schema(), claimNumber);
				}
				else {
					return amisysJavaBean.exceptionProfFacMessage("An exception has been encountered with the facility claim entered! Please, make sure you enter a valid 12 digits claim number");
				}
				
			}
		}
		else {
			
			return amisysJavaBean.exceptionProfFacMessage("An exception has been encountered with the facility claim entered! Please, make sure you enter a valid claim according to the desired environment");
					
		}
		
	}
	
	/**
	 * ************************************************************************************
	 * P8 - Amisys High Dollar Injectables & Device Intensive - End   *********************
	 * ***********************************************************************************
	 */
	
	
}
