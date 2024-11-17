package hr.algebra.greatwesterntrail.utils;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class DocumentationUtils {

    private DocumentationUtils() { }

    private static final String BASE_PATH = "target/classes/";
    private static final String HTML_DOCUMENTATION_FILENAME = "doc/documentation.html";
    private static final String CLASS_FILE_EXTENSION = ".class";

    public static void generateDocumentation() throws RuntimeException {
        try (Stream<Path> paths = Files.walk(Paths.get(BASE_PATH))) {
            List<Class<?>> classes = paths.filter(path -> path.getFileName().toString().endsWith(CLASS_FILE_EXTENSION)
                            && Character.isUpperCase(path.getFileName().toString().charAt(0)))
                    .map(DocumentationUtils::getClassFromPath)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            String htmlCode = generateHtmlCode(classes);
            Files.createDirectories(Path.of("doc"));
            Files.writeString(Path.of(HTML_DOCUMENTATION_FILENAME), htmlCode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate documentation", e);
        }
    }

    private static Class<?> getClassFromPath(Path classPath) {
        String className = classPath.toString()
                .substring(BASE_PATH.length(), classPath.toString().length() - CLASS_FILE_EXTENSION.length())
                .replace("\\", ".");
        try {
            Class<?> clazz = Class.forName(className);
            return (clazz.isAnonymousClass() || clazz.isSynthetic() || clazz.getSimpleName().contains("$")) ? null : clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to process class: " + className, e);
        }
    }

    private static String generateHtmlCode(List<Class<?>> classes) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Great Western Trail Documentation</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        h1 { color: #333; }
                        h2 { color: #555; }
                        h3 { color: #777; }
                        .class-section { margin-bottom: 20px; border: 1px solid #ddd; padding: 10px; border-radius: 8px; }
                        pre { background: #f4f4f4; padding: 10px; border-radius: 5px; }
                    </style>
                </head>
                <body>
                <h1>Great Western Trail Documentation</h1>
                <p>This documentation lists all classes, constructors, fields, methods, and annotations in the application.</p>
                """);

        Map<String, List<Class<?>>> categorizedClasses = categorizeClasses(classes);
        for (Map.Entry<String, List<Class<?>>> entry : categorizedClasses.entrySet()) {
            String section = entry.getKey();
            List<Class<?>> classList = entry.getValue();

            if ("Interface".equals(section)) { sb.append("<h2>Interfaces</h2>"); }
            else if ("Enum".equals(section)) { sb.append("<h2>Enums</h2>"); }
            else if ("Class".equals(section)) { sb.append("<h2>Classes</h2>"); }

            for (Class<?> clazz : classList) {
                sb.append("<div class='class-section'>")
                        .append("<h3>").append(section).append(": ").append(escapeHtml(clazz.getName())).append("</h3>")
                        .append("<pre>").append(escapeHtml(generateClassInfo(clazz))).append("</pre>")
                        .append("</div>");
            }
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    private static Map<String, List<Class<?>>> categorizeClasses(List<Class<?>> classes) {
        Map<String, List<Class<?>>> categorizedClasses = new HashMap<>();
        categorizedClasses.put("Interface", new ArrayList<>());
        categorizedClasses.put("Enum", new ArrayList<>());
        categorizedClasses.put("Class", new ArrayList<>());

        for (Class<?> clazz : classes) {
            if (clazz.isInterface()) { categorizedClasses.get("Interface").add(clazz); }
            else if (clazz.isEnum()) { categorizedClasses.get("Enum").add(clazz); }
            else { categorizedClasses.get("Class").add(clazz); }
        }
        return categorizedClasses;
    }

    private static String generateClassInfo(Class<?> clazz) {
        StringBuilder info = new StringBuilder();
        if (clazz.isEnum()) { appendEnumInfo(clazz, info); }
        else {
            appendClassInfo(clazz, info);
            appendConstructors(clazz, info);
            appendFields(clazz, info);
            appendMethods(clazz, info);
        }
        return info.toString();
    }

    private static void appendClassInfo(Class<?> clazz, StringBuilder info) {
        info.append(Modifier.toString(clazz.getModifiers()))
                .append(" class ")
                .append(clazz.getSimpleName()).append(getInterfaces(clazz))
                .append(System.lineSeparator()).append(System.lineSeparator());
    }

    private static void appendEnumInfo(Class<?> clazz, StringBuilder info) {
        info.append(Modifier.toString(clazz.getModifiers()))
                .append(" enum ").append(clazz.getSimpleName())
                .append(System.lineSeparator()).append(System.lineSeparator());

        Object[] constants = clazz.getEnumConstants();
        if (constants != null) {
            for (Object constant : constants) {
                info.append("   ").append(((Enum<?>) constant).name()).append(System.lineSeparator());
            }
        }
        info.append(System.lineSeparator());
        appendMethods(clazz, info);
    }

    private static String getInterfaces(Class<?> clazz) {
        if (clazz.getInterfaces().length > 0) {
            return " implements " + Arrays.stream(clazz.getInterfaces())
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", "));
        }
        return "";
    }

    private static void appendConstructors(Class<?> clazz, StringBuilder info) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 0) {
            for (Constructor<?> constructor : constructors) {
                info.append("   ").append(Modifier.toString(constructor.getModifiers()))
                        .append(" ").append(clazz.getSimpleName())
                        .append(getParameters(constructor.getParameters()))
                        .append(";").append(System.lineSeparator());
            }
            info.append(System.lineSeparator());
        } else { info.append(""); }
    }

    private static void appendFields(Class<?> clazz, StringBuilder info) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                if (field.getName().equals("$VALUES")) { continue; }
                info.append("   ").append(Modifier.toString(field.getModifiers()))
                        .append(" ").append(field.getType().getSimpleName())
                        .append(" ").append(field.getName())
                        .append(";").append(System.lineSeparator());
            }
            info.append(System.lineSeparator());
        } else { info.append(""); }
    }

    private static void appendMethods(Class<?> clazz, StringBuilder info) {
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length > 0) {
            for (Method method : methods) {
                if (isValidMethod(method)) {
                    info.append("   ").append(Modifier.toString(method.getModifiers()))
                            .append(" ").append(method.getReturnType().getSimpleName())
                            .append(" ").append(method.getName())
                            .append(getParameters(method.getParameters()))
                            .append(";").append(System.lineSeparator());
                }
            }
        } else { info.append(""); }
    }

    private static boolean isValidMethod(Method method) {
        return !method.isSynthetic() && !method.getName().startsWith("$");
    }

    private static String getParameters(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(param -> param.getType().getSimpleName() + " " + param.getName())
                .collect(Collectors.joining(", ", "(", ")"));
    }

    private static String escapeHtml(String input) {
        return input.replace("<", "&lt;").replace(">", "&gt;")
                .replace("&", "&amp;").replace("\"", "&quot;");
    }
}
