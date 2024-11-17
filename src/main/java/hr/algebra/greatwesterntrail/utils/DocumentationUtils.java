package hr.algebra.greatwesterntrail.utils;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public final class DocumentationUtils {

    private DocumentationUtils() { }

    private static final String BASE_PATH = "target/classes/";
    private static final String HTML_DOCUMENTATION_FILENAME = "doc/documentation.html";
    private static final String CLASS_FILE_EXTENSION = ".class";

    public static void generateDocumentation() throws RuntimeException {
        try (Stream<Path> paths = Files.walk(Paths.get(BASE_PATH))) {
            List<Path> classList = paths
                    .filter(path -> path.getFileName().toString().endsWith(CLASS_FILE_EXTENSION)
                            && Character.isUpperCase(path.getFileName().toString().charAt(0)))
                    .toList();

            String htmlCode = generateHtmlCode(classList);
            Files.createDirectories(Path.of("doc"));
            Files.writeString(Path.of(HTML_DOCUMENTATION_FILENAME), htmlCode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate documentation", e);
        }
    }

    private static String generateHtmlCode(List<Path> classList) {
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
                <h2>Interfaces:</h2>
                """);

        Set<String> processedClasses = new HashSet<>();
        List<Class<?>> interfaces = new ArrayList<>();
        List<Class<?>> enums = new ArrayList<>();
        List<Class<?>> classes = new ArrayList<>();

        for (Path classPath : classList) {
            String className = classPath
                    .toString()
                    .substring(BASE_PATH.length(), classPath.toString().length() - CLASS_FILE_EXTENSION.length())
                    .replace("\\", ".");

            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnonymousClass() || clazz.isSynthetic() || clazz.getSimpleName().contains("$")) {
                    continue;
                }
                if (!processedClasses.add(clazz.getName())) { continue; }
                if (clazz.isInterface()) { interfaces.add(clazz); }
                else if (clazz.isEnum()) { enums.add(clazz); }
                else { classes.add(clazz); }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to process class: " + className, e);
            }
        }

        for (Class<?> interfaceClass : interfaces) {
            sb.append("<div class='class-section'>")
                    .append("<h3>Interface: ").append(escapeHtml(interfaceClass.getName())).append("</h3>");
            StringBuilder classInfo = new StringBuilder();
            appendInterfaceInfo(interfaceClass, classInfo);
            sb.append("<pre>").append(escapeHtml(classInfo.toString())).append("</pre>");
            sb.append("</div>");
        }

        sb.append("<h2>Enums:</h2>");
        for (Class<?> enumClass : enums) {
            sb.append("<div class='class-section'>")
                    .append("<h3>Enum: ").append(escapeHtml(enumClass.getName())).append("</h3>");
            StringBuilder classInfo = new StringBuilder();
            appendEnumInfo(enumClass, classInfo);
            sb.append("<pre>").append(escapeHtml(classInfo.toString())).append("</pre>");
            sb.append("</div>");
        }

        sb.append("<h2>Classes:</h2>");
        for (Class<?> clazz : classes) {
            sb.append("<div class='class-section'>")
                    .append("<h3>Class: ").append(escapeHtml(clazz.getName())).append("</h3>");
            StringBuilder classInfo = new StringBuilder();
            readClassAndMembersInfo(clazz, classInfo);
            sb.append("<pre>").append(escapeHtml(classInfo.toString())).append("</pre>");
            sb.append("</div>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    public static void readClassAndMembersInfo(Class<?> clazz, StringBuilder classAndMembersInfo) {
        appendClassInfo(clazz, classAndMembersInfo);
    }

    private static void appendClassInfo(Class<?> clazz, StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append(Modifier.toString(clazz.getModifiers()))
                .append(" class ")
                .append(clazz.getSimpleName());

        if (clazz.getInterfaces().length > 0) {
            classAndMembersInfo.append(" implements ");
            for (Class<?> intf : clazz.getInterfaces()) {
                classAndMembersInfo.append(intf.getSimpleName()).append(", ");
            }
            classAndMembersInfo.delete(classAndMembersInfo.length() - 2, classAndMembersInfo.length());
        }
        classAndMembersInfo.append(System.lineSeparator());
        appendConstructors(clazz, classAndMembersInfo);
        appendFields(clazz, classAndMembersInfo);
        appendMethods(clazz, classAndMembersInfo);
    }

    private static void appendInterfaceInfo(Class<?> interfaceClass, StringBuilder classInfo) {
        classInfo.append(Modifier.toString(interfaceClass.getModifiers()))
                .append(" interface ")
                .append(interfaceClass.getSimpleName())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        for (Method method : interfaceClass.getDeclaredMethods()) {
            if (!Modifier.isPrivate(method.getModifiers())) {
                classInfo.append("   ").append(Modifier.toString(method.getModifiers()))
                        .append(" ").append(method.getReturnType().getSimpleName())
                        .append(" ").append(method.getName());
                appendParameters(method, classInfo);
                classInfo.append(";").append(System.lineSeparator());
            }
        }
    }

    private static void appendEnumInfo(Class<?> enumClass, StringBuilder classInfo) {
        classInfo.append(Modifier.toString(enumClass.getModifiers()))
                .append(" enum ")
                .append(enumClass.getSimpleName())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        Enum<?>[] enumConstants = (Enum<?>[]) enumClass.getEnumConstants();
        for (Enum<?> constant : enumConstants) {
            classInfo.append("   ").append(constant.name()).append(System.lineSeparator());
        }
        appendMethods(enumClass, classInfo);
    }

    private static void appendMethods(Class<?> clazz, StringBuilder classInfo) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (isValidMethod(method)) {
                classInfo.append(System.lineSeparator())
                        .append("   ").append(Modifier.toString(method.getModifiers()))
                        .append(" ").append(method.getReturnType().getSimpleName())
                        .append(" ").append(method.getName());
                appendParameters(method, classInfo);
                classInfo.append(";");
            }
        }
    }

    private static void appendConstructors(Class<?> clazz, StringBuilder classInfo) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            classInfo.append(System.lineSeparator())
                    .append("   ").append(Modifier.toString(constructor.getModifiers()))
                    .append(" ").append(clazz.getSimpleName());
            appendParameters(constructor, classInfo);
            classInfo.append(";").append(System.lineSeparator());
        }
        classInfo.append(System.lineSeparator());
    }

    private static void appendParameters(Executable executable, StringBuilder classInfo) {
        Parameter[] parameters = executable.getParameters();
        classInfo.append("(");
        if (parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                classInfo.append(parameters[i].getType().getSimpleName())
                        .append(" ").append(parameters[i].getName());
                if (i < parameters.length - 1) {
                    classInfo.append(", ");
                }
            }
        }
        classInfo.append(")");
    }

    private static boolean isValidMethod(Method method) {
        return !method.isSynthetic() && !method.getName().startsWith("$");
    }

    private static void appendFields(Class<?> clazz, StringBuilder classInfo) {
        for (Field field : clazz.getDeclaredFields()) {
            classInfo.append("   ").append(Modifier.toString(field.getModifiers()))
                    .append(" ").append(field.getType().getSimpleName())
                    .append(" ").append(field.getName()).append(";")
                    .append(System.lineSeparator());
        }
    }

    private static String escapeHtml(String input) {
        return input.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("&", "&amp;")
                .replace("\"", "&quot;");
    }
}
