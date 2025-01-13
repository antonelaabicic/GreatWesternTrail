package hr.algebra.greatwesterntrail.model;

import hr.algebra.greatwesterntrail.jndi.InitialDirContextCloseable;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class ConfigurationReader {

    private static Properties properties;

    static {
        properties = new Properties();

        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        environment.put(Context.PROVIDER_URL, "file:C:\\nela master\\antonela\\Nela master 2021\\algebra\\3. godina\\Java 2\\projekt\\GreatWesternTrail\\conf");

        try (InitialDirContextCloseable context = new InitialDirContextCloseable(environment)){
            Object configurationObject = context.lookup("app.conf");
            properties.load(new FileReader(configurationObject.toString()));
        } catch (NamingException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String getStringValueForKey(ConfigurationKey key) {
        return properties.getProperty(key.getKey());
    }

    public static Integer getIntegerValueForKey(ConfigurationKey key) {
        return Integer.parseInt(getStringValueForKey(key));
    }
}
