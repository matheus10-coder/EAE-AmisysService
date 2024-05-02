package net.abcbs.eae.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
/***********************************************************************************************************************************************************************
 * @Author ABCBS Resource
 * 
 * Description: ClaimLineApplication class will be used as the application driver
 * 
 * Project: NP Pended Claims
 ***********************************************************************************************************************************************************************/
@ApplicationPath("resources")
public class ClaimLineApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(ClaimLineResource.class);
		return classes;	
	}
}