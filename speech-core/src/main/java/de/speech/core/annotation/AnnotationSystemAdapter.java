package de.speech.core.annotation;

import de.speech.core.annotation.mapping.AnnotationMappingCache;

/**
 * This class adapts a {@linkplain AnnotationSystem}. Extends this class to provide specialized functionalities based
 * one the provides {@linkplain AnnotationMappingCache} from {@linkplain AnnotationSystem}.
 */
public abstract class AnnotationSystemAdapter {

    /**
     * Adapted annotation system.
     *
     * In case no parent was given, the {@linkplain AnnotationSystem#getGlobal() global annotation system}
     * is used instead.
     */
    private AnnotationSystem parent;

    public AnnotationSystemAdapter() {
        this(null);
    }

    public AnnotationSystemAdapter(AnnotationSystem parent) {

        this.parent = parent;

        if (this.parent == null) {
            this.parent = AnnotationSystem.getGlobal();
        }
    }

    /**
     * Provides the adapted {@linkplain AnnotationSystem}.
     * @see #parent
     * @return adapted annotation system
     */
    public AnnotationSystem getParent() {
        return parent;
    }
}
