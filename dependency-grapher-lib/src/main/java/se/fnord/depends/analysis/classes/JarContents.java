package se.fnord.depends.analysis.classes;

import java.util.Objects;
import java.util.Set;

public class JarContents {
    private final String classFile;
    private final int modifiers;
    private final String exportedType;
    private final Set<String> references;

    public JarContents(String classFile, int modifiers, String exportedType, Set<String> references) {
        this.classFile = classFile;
        this.modifiers = modifiers;
        this.exportedType = exportedType;
        this.references = references;
    }

    public String getExportedType() {
        return exportedType;
    }

    public Set<String> getReferences() {
        return references;
    }

    public int getModifiers() {
        return modifiers;
    }

    public String getClassFile() {
        return classFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JarContents)) return false;
        JarContents jarContents = (JarContents) o;
        return Objects.equals(modifiers, jarContents.modifiers) &&
                Objects.equals(classFile, jarContents.classFile) &&
                Objects.equals(exportedType, jarContents.exportedType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classFile, modifiers, exportedType);
    }
}
