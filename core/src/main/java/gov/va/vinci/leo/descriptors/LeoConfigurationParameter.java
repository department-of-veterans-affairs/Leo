package gov.va.vinci.leo.descriptors;

import java.lang.annotation.*;

/**
 * Marks a class field variable as a configuration parameter.  Supported types include primitives, Strings, and objects
 * with a single string parameter in the constructor.  Parameter types are set from the field type.  Field array variables
 * are created as multi-valued parameters.
 *
 * User: Thomas Ginter
 * Date: 10/22/14
 * Time: 16:14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface LeoConfigurationParameter {
    /**
     * The default name value if one is not set.
     */
    public static final String FIELD_NAME = "!FIELD_NAME!";

    /**
     * Name of the parameter.  If you do not specify a name then the field name will be used by default.
     *
     * @return name of the parameter.
     */
    String name() default FIELD_NAME;
    /**
     * Description of the parameter. Defaults to the empty string.
     *
     * @return the parameter description.
     */
    String description() default "";
    /**
     * Specifies whether or not this parameter is required. Defaults to false.
     *
     * @return true if the parameter is required.
     */
    boolean mandatory() default false;
}
