package se.fnord.depends.analysis.classes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static se.fnord.depends.analysis.classes.ClassAnalyser.analyseClass;


public class JarAnalyser {

    public static List<JarContents> analyse(InputStream is) throws IOException {
        final JarInputStream jarInputStream = new JarInputStream(is);
        final List<JarContents> results = new ArrayList<>();
        final ReferenceFilter referenceFilter = ReferenceFilter.create("java/");

        JarEntry nextJarEntry = jarInputStream.getNextJarEntry();
        while (nextJarEntry != null) {
            if (!nextJarEntry.isDirectory() && nextJarEntry.getName().endsWith(".class")) {
                 results.add(analyseClass(nextJarEntry.getName(), jarInputStream, referenceFilter));
            }
            nextJarEntry = jarInputStream.getNextJarEntry();
        }

        return results;
    }
}
