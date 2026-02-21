package io.github.qy.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UrlUtil {

    // 扫描某个类下的所有类名字
    public static List<String> scanClasses(Class<?> rootDirClass) {
        String basePackage = rootDirClass.getPackageName();
        return scanClasses(basePackage);
    }

    // 按照包名得到所有class的全限定名
    private static List<String> scanClasses(String basePackage) {
        List<String> classNames = new ArrayList<>();
        try {
            String path = basePackage.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(path);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    File dir = new File(url.toURI());
                    scanDirectory(dir, basePackage, classNames);
                } else if ("jar".equals(protocol)) {
                    scanJar(url, path, classNames);
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return classNames;
    }

    // 扫描文件
    private static void scanDirectory(File dir, String packageName, List<String> classNames) {
        if (!dir.exists()) return;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classNames);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                classNames.add(className);
            }
        }
    }

    // 扫描jar
    private static void scanJar(URL url, String basePath, List<String> classNames) throws IOException {
        JarURLConnection jarConn = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarConn.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith(basePath) && name.endsWith(".class")) {
                String className = name.replace('/', '.').replace(".class", "");
                classNames.add(className);
            }
        }
    }

}
