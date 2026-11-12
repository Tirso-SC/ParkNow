package icai.dtc.isw.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class PropertiesISW extends Properties{
	
	


	private static final long serialVersionUID = 1L;
	private static PropertiesISW prop;
	private static final String path="properties.xml";
	
	
	private PropertiesISW() {
		try {
			this.loadFromXML(getClass().getClassLoader().getResourceAsStream(path));
		} catch (InvalidPropertiesFormatException e) {
			
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		};
	}
	
	public static PropertiesISW getInstance() {
		if (prop==null) {
			prop=new PropertiesISW();
		}
		return prop;
		
	}
	
}
