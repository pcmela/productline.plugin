package productline.plugin.internal;

import java.util.Properties;

public class Configuration {

	private String connectionString;
	private String username;
	private String password;
	private int id;
	private boolean isDirty = false;
	
	public String getConnectionString() {
		return connectionString;
	}
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isDirty() {
		return isDirty;
	}
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
	public void setDataLocal(Properties properties){
		id = Integer.parseInt(properties.getProperty(ConfigurationKeys.PRODUCTLINE_ID_KEY));
		connectionString = properties.getProperty(ConfigurationKeys.CONNECTION_URL_KEY);
		username = properties.getProperty(ConfigurationKeys.USERNAME_KEY);
		password = properties.getProperty(ConfigurationKeys.PASSWORD_KEY);
	}
	
	public static Properties getProperties(Configuration local){
		Properties properties = new Properties();
		//local
		properties.setProperty(ConfigurationKeys.PRODUCTLINE_ID_KEY, String.valueOf(local.getId()));
		properties.setProperty(ConfigurationKeys.CONNECTION_URL_KEY, local.getConnectionString());
		properties.setProperty(ConfigurationKeys.USERNAME_KEY, local.getUsername());
		properties.setProperty(ConfigurationKeys.PASSWORD_KEY, local.getPassword());
				
		
		return properties;
	}
}
