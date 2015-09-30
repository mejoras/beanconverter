package com.indra.isl.malaga;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para definir que un atributo no puede ser nulo.
 * Esta anotación puede ser de campo, como de clase.
 *
 * @author mcastillog
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface NotNull {

    /** Referencia a la clase. */
    public String[] source() default {};

    /** Atributos que hara referencia a la clase definida. */
    public String[] sourceAttr() default {};

}
