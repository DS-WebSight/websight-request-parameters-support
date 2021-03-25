package pl.ds.websight.request.parameters.support.annotations;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to be used on either methods, fields or constructor parameters to
 * let Sling Models inject a Wrapper of primitive types, an Enum, Collection or List of them based on request parameter.
 * Enum constant is recognized by {@link Enum#toString()} method.
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@InjectAnnotation
@Source("request-parameters")
public @interface RequestParameter {
    /**
     * Specifies the name of the request parameter. If empty or not set, then
     * the name is derived from the method or field.
     */
    String name() default "";

    /**
     * If set to true, the model can be instantiated even if there is no request
     * parameter with the given name found. Default = true.
     *
     * @deprecated Use {@link InjectionStrategy} instead.
     */
    @Deprecated
    boolean optional() default true;

    /**
     * if set to REQUIRED injection is mandatory, if set to OPTIONAL injection
     * is optional, in case of DEFAULT the standard annotations
     * ({@link org.apache.sling.models.annotations.Optional}, {@link org.apache.sling.models.annotations.Required})
     * are used. If even those are not available the default injection strategy
     * defined on the {@link org.apache.sling.models.annotations.Model} applies.
     * Default value = OPTIONAL (validation is performed by JSR 303
     * implementation).
     */
    InjectionStrategy injectionStrategy() default InjectionStrategy.OPTIONAL;
}
