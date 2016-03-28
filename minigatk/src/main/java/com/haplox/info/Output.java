
package com.haplox.info;

import java.lang.annotation.*;

/**
 * Annotates fields in objects that should be used as command-line arguments.
 * Any field annotated with @Argument can appear as a command-line parameter.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Output {
    /**
     * The full name of the command-line argument.  Full names should be
     * prefixed on the command-line with a double dash (--).
     * @return Selected full name, or "" to use the default.
     */
    String fullName() default "out";

    /**
     * Specified short name of the command.  Short names should be prefixed
     * with a single dash.  Argument values can directly abut single-char
     * short names or be separated from them by a space.
     * @return Selected short name, or "" for none.
     */
    String shortName() default "o";

    /**
     * Documentation for the command-line argument.  Should appear when the
     * --help argument is specified.
     * @return Doc string associated with this command-line argument.
     */
    String doc() default "An output file created by the walker.  Will overwrite contents if file exists";

    /**
     * Is this argument required.  If true, the command-line argument system will
     * make a best guess for populating this argument based on the type, and will
     * fail if the type can't be populated.
     * @return True if the argument is required.  False otherwise.
     */
    boolean required() default false;

    /**
     * If this argument is not required, should it default to use stdout if no
     * output file is explicitly provided on the command-line?
     * @return True if the argument should default to stdout.  False otherwise.
     */
    boolean defaultToStdout() default true;

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
}
