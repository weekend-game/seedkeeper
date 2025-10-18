package game.weekend.seedkeeper.general;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Locally stored application properties.
 */
public class Proper {

	/**
	 * Creating objects of this class is prohibited. The class contains only static
	 * methods.
	 */
	private Proper() {
	}

	/**
	 * Read previously saved application properties.
	 * 
	 * @param name name of the application properties file without specifying the
	 *             type.
	 */
	public static void read(String name) {
		fileName = name.toLowerCase() + ".properties";
		try {
			InputStream inp = new FileInputStream(fileName);
			properties.load(inp);
			inp.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Save application properties.
	 */
	public static void save() {
		OutputStream out;
		try {
			out = new FileOutputStream(fileName);
			properties.store(out, "");
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Сохранить свойство name с целым значением value.
	 * 
	 * @param name  имя свойства.
	 * @param value целое значение.
	 */
	public static void setProperty(String name, int value) {
		properties.setProperty(name, "" + value);
	}

	/**
	 * Получить целое свойство name.
	 * 
	 * @param name имя свойства.
	 * @param def  значение свойства по умолчанию.
	 * @return целочисленное значение свойства.
	 */
	public static int getProperty(String name, int def) {
		return Integer.parseInt(properties.getProperty(name, "" + def));
	}

    public static double getProperty(String name, double def) {
        return Double.parseDouble(properties.getProperty(name, "" + def));
    }

    public static void setProperty(String name, double value) {
        properties.setProperty(name, "" + value);
    }

	/**
	 * Save the property with an integer value.
	 * 
	 * @param name  property name.
	 * @param value integer value.
	 */
	public static void setProperty(String name, String value) {
		properties.setProperty(name, value);
	}

	/**
	 * Get the integer name property.
	 * 
	 * @param name property name.
	 * @param def  default property value.
	 * @return integer value of the property.
	 */
	public static String getProperty(String name, String def) {
		return properties.getProperty(name, def);
	}

	private static Properties properties = new Properties();
	private static String fileName = "application.properties";
}
