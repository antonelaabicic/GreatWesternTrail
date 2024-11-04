package hr.algebra.greatwesterntrail.utils;

import java.lang.reflect.*;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DocumenationUtils {
    private DocumenationUtils() {
    }

    public static void readClassInfo(Class<?> clazz, StringBuilder classInfo) {
        appendPackage(clazz, classInfo);
        appendModifiers(clazz, classInfo);
        classInfo
                .append(" ")
                .append(clazz.getSimpleName());
        appendParent(clazz, classInfo, true);
        appendInterfaces(clazz, classInfo);
    }

    private static void appendPackage(Class<?> clazz, StringBuilder classInfo) {
        classInfo
                .append(clazz.getPackage())
                .append(System.lineSeparator())
                .append(System.lineSeparator());
    }

    private static void appendModifiers(Class<?> clazz, StringBuilder classInfo) {
        classInfo.append(Modifier.toString(clazz.getModifiers()));
    }

    private static void appendParent(Class<?> clazz, StringBuilder classInfo, boolean first) {
        Class<?> parent = clazz.getSuperclass();
        if (parent == null) {
            return;
        }
        classInfo
                .append(first ? " extends " : " -> ")
                .append(parent.getSimpleName());
        appendParent(parent, classInfo, false);
    }

    private static void appendInterfaces(Class<?> clazz, StringBuilder classInfo) {
        if (clazz.getInterfaces().length > 0) {
            classInfo
                    .append(Stream.of(clazz.getInterfaces())
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", ", " implements ", "")));
        }
    }

    public static void readClassAndMembersInfo(Class<?> clazz, StringBuilder classAndMembersInfo) {
        readClassInfo(clazz, classAndMembersInfo);
        appendFields(clazz, classAndMembersInfo);
        appendMethods(clazz, classAndMembersInfo);
        appendConstructors(clazz, classAndMembersInfo);
    }

    private static void appendFields(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Field[] fields = clazz.getDeclaredFields();
        classAndMembersInfo
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(Stream.of(fields)
                        .map(Objects::toString)
                        .collect(Collectors.joining(System.lineSeparator())));
    }

    private static void appendMethods(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            classAndMembersInfo
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            appendAnnotations(method, classAndMembersInfo);
            classAndMembersInfo
                    .append(System.lineSeparator())
                    .append(Modifier.toString(method.getModifiers()))
                    .append(" ")
                    .append(method.getReturnType())
                    .append(" ")
                    .append(method.getName());
            appendParameters(method, classAndMembersInfo);
            appendExceptions(method, classAndMembersInfo);
        }
    }

    private static void appendConstructors(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            classAndMembersInfo
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            appendAnnotations(constructor, classAndMembersInfo);
            classAndMembersInfo
                    .append(System.lineSeparator())
                    .append(Modifier.toString(constructor.getModifiers()))
                    .append(" ")
                    .append(constructor.getName());
            appendParameters(constructor, classAndMembersInfo);
            appendExceptions(constructor, classAndMembersInfo);
        }
    }


    private static void appendAnnotations(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append(
                Stream.of(executable.getAnnotations())
                        .map(Objects::toString)
                        .collect(Collectors.joining(System.lineSeparator())));
    }

    private static void appendParameters(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append(
                Stream.of(executable.getParameters())
                        .map(Objects::toString)
                        .collect(Collectors.joining(", ", "(", ")"))
        );
    }

    private static void appendExceptions(Executable executable, StringBuilder classAndMembersInfo) {
        if (executable.getExceptionTypes().length > 0) {
            classAndMembersInfo.append(
                    Stream.of(executable.getExceptionTypes())
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", ", " throws ", "")));
        }
    }

}
