package se.fnord.depends.analysis.classes;

import net.openhft.koloboke.collect.set.hash.HashObjSets;
import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.ASM5;

class ClassAnalyser {

    private class FieldReferenceGatherer extends FieldVisitor {
        public FieldReferenceGatherer() {
            super(ASM5);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return annotationVisitor(desc);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor(desc);
        }
    }

    private class MethodReferenceGatherer extends MethodVisitor {
        public MethodReferenceGatherer() {
            super(ASM5);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return annotationVisitor;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return annotationVisitor(desc);
        }

        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor(desc);
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            visitType(desc);
            visitSignature(signature);
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
            return annotationVisitor(desc);
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            return annotationVisitor(desc);
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor(desc);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor(desc);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            visitInternalName(owner);
            visitType(desc);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
            visitType(desc);
            visitHandle(bsm);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            visitInternalName(owner);
            visitType(desc);
        }

        @Override
        public void visitMultiANewArrayInsn(String desc, int dims) {
            visitType(desc);
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            visitInternalName(type);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            visitInternalName(type);
        }

    }

    private class AnnotationReferenceGatherer extends AnnotationVisitor {
        public AnnotationReferenceGatherer() {
            super(ASM5);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            return annotationVisitor(desc);
        }
    }

    private class ClassReferenceGatherer extends ClassVisitor {
        public ClassReferenceGatherer() {
            super(ASM5);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (currentClass != null)
                throw new IllegalStateException();
            currentClass = name;
            currentModifiers = access;

            visitSignature(signature);
            visitInternalName(superName);
            visitInternalNames(interfaces);
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            visitInternalName(name);
            visitInternalName(outerName);
        }

        @Override
        public void visitOuterClass(String owner, String name, String desc) {
            visitInternalName(owner);
            visitType(desc);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return annotationVisitor(desc);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor(desc);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            visitType(desc);
            visitSignature(signature);
            visitInternalNames(exceptions);

            return methodVisitor;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            visitType(desc);
            visitSignature(signature);
            return fieldVisitor;
        }
    }

    private class SignatureReferenceGatherer extends SignatureVisitor {
        private SignatureReferenceGatherer() {
            super(ASM5);
        }

        @Override
        public void visitInnerClassType(String name) {
            visitInternalName(name);
        }

        @Override
        public void visitClassType(String name) {
            visitInternalName(name);
        }
    }

    public ClassAnalyser(ReferenceFilter filter) {
        this.filter = filter;
    }

    private void visitInternalName(String internalName) {
        if (internalName != null)
            currentReferences.add(internalName);
    }

    private void visitInternalNames(String[] internalNames) {
        if (internalNames != null)
            Collections.addAll(currentReferences, internalNames);
    }

    private void visitHandle(Handle handle) {
        currentReferences.add(handle.getOwner());
        visitType(handle.getDesc());
    }

    private void visitSignature(String signature) {
        if (signature != null)
            new SignatureReader(signature).accept(signatureVisitor);
    }

    private void visitType(Type type) {
        switch (type.getSort()) {
            case Type.OBJECT:
                currentReferences.add(type.getInternalName());
                break;
            case Type.ARRAY:
                visitType(type.getElementType());
                break;
            case Type.METHOD:
                for (Type arg : type.getArgumentTypes()) {
                    visitType(arg);
                }
                visitType(type.getReturnType());
                break;
            default:
        }
    }

    private void visitType(String type) {
        if (type != null)
            visitType(Type.getType(type));
    }

    private AnnotationVisitor annotationVisitor(String desc) {
        visitType(desc);
        return annotationVisitor;
    }

    private JarContents visitClass(String classFile, ClassReader classReader) {
        classReader.accept(classVisitor, 0);
        final ReferenceFilter filter2 = filter.add(currentClass);
        final Set<String> references = currentReferences.stream().
                filter(filter2)
                .collect(Collectors.toSet());
        return new JarContents(classFile, currentModifiers, currentClass, references);
    }

    private final Set<String> currentReferences = HashObjSets.newUpdatableSet();

    private final MethodReferenceGatherer methodVisitor = new MethodReferenceGatherer();
    private final AnnotationReferenceGatherer annotationVisitor = new AnnotationReferenceGatherer();
    private final ClassReferenceGatherer classVisitor = new ClassReferenceGatherer();
    private final FieldReferenceGatherer fieldVisitor = new FieldReferenceGatherer();
    private final SignatureReferenceGatherer signatureVisitor = new SignatureReferenceGatherer();
    private final ReferenceFilter filter;

    private String currentClass;
    private int currentModifiers;

    public static JarContents analyseClass(String classFile, InputStream is, ReferenceFilter filter) throws IOException {
        final ClassReader classReader = new ClassReader(is);
        final ClassAnalyser classAnalyser = new ClassAnalyser(filter);

        return classAnalyser.visitClass(classFile, classReader);
    }
}
