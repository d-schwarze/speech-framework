package de.speech.core.annotation.reflection;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom annotation visitor for getting information of an annotation that is present on a particular class file.
 * For instance changed annotation values are read.
 */
public class SpeechAnnotationVisitor extends AnnotationVisitor {

    /**
     * All explicit change annotation values.
     */
    private Map<String, Object> annotationValues;

    public SpeechAnnotationVisitor() {
        super(Opcodes.ASM9);

        annotationValues = new HashMap<>();
    }

    @Override
    public void visit(String name, Object value) {

        annotationValues.put(name, value);

    }

    public Map<String, Object> getAnnotationValues() {
        return annotationValues;
    }
}
