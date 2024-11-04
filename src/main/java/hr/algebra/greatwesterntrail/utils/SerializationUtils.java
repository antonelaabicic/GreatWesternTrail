package hr.algebra.greatwesterntrail.utils;

import java.io.*;

public final class SerializationUtils {

    private SerializationUtils() {
    }

    public static<T extends Serializable> void write(T t, String filename) throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))){
            out.writeObject(t);
        }
    }

    public static<T extends Serializable> T read(String filename) throws IOException, ClassNotFoundException {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))){
            return (T)in.readObject();
        }
    }
}
