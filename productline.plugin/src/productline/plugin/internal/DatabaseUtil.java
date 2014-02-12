/*package productline.plugin.internal;

import java.util.Properties;

import diploma.productline.HibernateUtil;

public class DatabaseUtil {

	public static Properties getHibernateProperties(Properties properties){

		Properties hibernateProp = new Properties();
		hibernateProp.setProperty(HibernateUtil.URL, properties
				.getProperty(ConfigurationKeys.CONNECTION_URL_KEY));
		hibernateProp.setProperty(HibernateUtil.USERNAME,
				properties.getProperty(ConfigurationKeys.USERNAME_KEY));
		hibernateProp.setProperty(HibernateUtil.PASSWORD,
				properties.getProperty(ConfigurationKeys.PASSWORD_KEY));
		
		return hibernateProp;
	}
}
*/