package com.indra.isl.malaga;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para la conversión de modelos de datos. En el se define los siguientes atributos necesarios.
 *
 * @author mcastillog
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Convert {

    /** Referencia a la clase. */
    public String[] source();

    /** Atributos que hara referencia a la clase definida. */
    public String[] sourceAttr();

    /** Metodo que invocaremos. */
    public String[] convertMethod();

}
