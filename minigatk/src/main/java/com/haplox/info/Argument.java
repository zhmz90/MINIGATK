
package com.haplox.info;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Argument {
    /**
     * The full name of the command-line argument.  Full names should be
     * prefixed on the command-line with a double dash (--).
     * @return Selected full name, or "" to use the default.
     */
    String fullName() default "";

    /**
     * Specified short name of the command.  Short names should be prefixed
     * with a single dash.  Argument values can directly abut single-char
     * short names or be separated from them by a space.
     * @return Selected short name, or "" for none.
     */
    String shortName() default "";

    /**
     * Documentation for the command-line argument.  Should appear when the
     * --help argument is specified. 
     * @return Doc string associated with this command-line argument.
     */
    String doc() default "Undocumented option";

    /**
     * Is this argument required.  If true, the command-line argument system will
     * make a best guess for populating this argument based on the type descriptor,
     * and will fail if the type can't be populated.
     * @return True if the argument is required.  False otherwise.
     */
    boolean required() default true;

    /**
     * Should this command-line argument be exclusive of others.  Should be
     * a comma-separated list of names of arguments of which this should be
     * independent.
     * @return A comma-separated string listing other arguments of which this
     *         argument should be independent.
     */
    String exclusiveOf() default "";

    /**
     * Provide a regexp-based validation string.
     * @return Non-empty regexp for validation, blank otherwise. 
     */
    String validation() default "";

    /**
     * Hard lower bound on the allowed value for the annotated argument -- generates an exception if violated.
     * Enforced only for numeric types whose values are explicitly specified on the command line.
     *
     * @return Hard lower bound on the allowed value for the annotated argument, or Double.NEGATIVE_INFINITY
     *         if there is none.
     */
    double minValue() default Double.NEGATIVE_INFINITY;

    /**
     * Hard upper bound on the allowed value for the annotated argument -- generates an exception if violated.
     * Enforced only for numeric types whose values are explicitly specified on the command line.
     *
     * @return Hard upper bound on the allowed value for the annotated argument, or Double.POSITIVE_INFINITY
     *         if there is none.
     */
    double maxValue() default Double.POSITIVE_INFINITY;

    /**
     * Soft lower bound on the allowed value for the annotated argument -- generates a warning if violated.
     * Enforced only for numeric types whose values are explicitly specified on the command line.
     *
     * @return Soft lower bound on the allowed value for the annotated argument, or Double.NEGATIVE_INFINITY
     *         if there is none.
     */
    double minRecommendedValue() default Double.NEGATIVE_INFINITY;

    /**
     * Soft upper bound on the allowed value for the annotated argument -- generates a warning if violated.
     * Enforced only for numeric types whose values are explicitly specified on the command line.
     *
     * @return Soft upper bound on the allowed value for the annotated argument, or Double.POSITIVE_INFINITY
     *         if there is none.
     */
    double maxRecommendedValue() default Double.POSITIVE_INFINITY;
}
