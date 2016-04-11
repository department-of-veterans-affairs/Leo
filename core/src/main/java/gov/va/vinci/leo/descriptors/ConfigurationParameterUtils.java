package gov.va.vinci.leo.descriptors;

import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Created by thomasginter on 3/10/16.
 */
public class ConfigurationParameterUtils {

    /**
     * Logger object.
     */
    private static Logger logger = Logger.getLogger(ConfigurationParameterUtils.class);

    /**
     * Map of Java type strings to UIMA configuration parameter types.
     */
    protected static final Map<String, String> javaTypeToUimaType = new HashMap<String, String>();

    static {
        javaTypeToUimaType.put(Boolean.class.getName(), ConfigurationParameter.TYPE_BOOLEAN);
        javaTypeToUimaType.put(Float.class.getName(), ConfigurationParameter.TYPE_FLOAT);
        javaTypeToUimaType.put(Long.class.getName(), ConfigurationParameter.TYPE_FLOAT);
        javaTypeToUimaType.put(Double.class.getName(), ConfigurationParameter.TYPE_FLOAT);
        javaTypeToUimaType.put(Integer.class.getName(), ConfigurationParameter.TYPE_INTEGER);
        javaTypeToUimaType.put(String.class.getName(), ConfigurationParameter.TYPE_STRING);
        javaTypeToUimaType.put("boolean", ConfigurationParameter.TYPE_BOOLEAN);
        javaTypeToUimaType.put("float", ConfigurationParameter.TYPE_FLOAT);
        javaTypeToUimaType.put("long", ConfigurationParameter.TYPE_FLOAT);
        javaTypeToUimaType.put("double", ConfigurationParameter.TYPE_FLOAT);
        javaTypeToUimaType.put("int", ConfigurationParameter.TYPE_INTEGER);
    }

    public static <T> Map<ConfigurationParameterImpl, ?> getParamsToValuesMap(T paramObject) {
        Map<ConfigurationParameterImpl, Object> parameterObjectMap = new HashMap<>();

        try {
            ConfigurationParameterImpl[] parameters = getParams(paramObject.getClass());
            for (ConfigurationParameterImpl parameter : parameters) {
                Field field = FieldUtils.getField(paramObject.getClass(), parameter.getName(), true);
                parameterObjectMap.put(parameter, FieldUtils.readField(field, paramObject, true));
            }
        } catch (IllegalAccessException e) {
            logger.error("Error getting parameter list", e);
        }

        return parameterObjectMap;
    }

    public static <T> ConfigurationParameterImpl[] getParams(Class<T> c) throws IllegalAccessException {
        List<ConfigurationParameterImpl> parameterList = new ArrayList<>();

        for (Class<T> cls = c; cls != null; cls = (Class<T>) cls.getSuperclass()) {
            //Get the list of declared fields in this class and check for our Annotation
            for (Field field : cls.getDeclaredFields()) {
                if (!field.isAnnotationPresent(LeoConfigurationParameter.class))
                    continue;
                ConfigurationParameterImpl param = new ConfigurationParameterImpl();
                for (java.lang.annotation.Annotation a : field.getAnnotations()) {
                    if (a instanceof LeoConfigurationParameter) {
                        LeoConfigurationParameter annotation = (LeoConfigurationParameter) a;
                        if (annotation.name().equals(LeoConfigurationParameter.FIELD_NAME)) {
                            param.setName(field.getName());
                        } else {
                            param.setName(annotation.name());
                        }
                        param.setDescription(annotation.description());
                        param.setMandatory(annotation.mandatory());
                        getParameterTypeFromField(field, param);
                        parameterList.add(param);
                        break;
                    }
                }
            }
        }

        //Check for a Param class or an Enum that can be used to pull parameters
        for (Class decl : c.getClass().getDeclaredClasses()) {
            if (decl.isEnum() && Arrays.asList(decl.getInterfaces()).contains(ConfigurationParameter.class)) {
                parameterList.addAll(EnumSet.allOf(decl));
                break;
            } else if (decl.getCanonicalName().endsWith(".Param")) {
                Field[] fields = decl.getFields();
                for (Field f : fields) {
                    if (ConfigurationParameter.class.isAssignableFrom(f.getType())) {
                        parameterList.add((ConfigurationParameterImpl) f.get(null));
                    }
                }
            }
        }

        return parameterList.toArray(new ConfigurationParameterImpl[parameterList.size()]);
    }

    public static <T> void initParameterValues(T configObj, UimaContext context) throws ResourceInitializationException {
        try {
            ConfigurationParameterImpl[] parameters = getParams(configObj.getClass());
            if (parameters == null) return;
            for (ConfigurationParameterImpl parameter : parameters) {
                /** Make sure mandatory values have been set **/
                if (parameter.isMandatory()) {
                    if (getParameterValue(configObj, parameter.getName(), context) == null ||
                            (ConfigurationParameter.TYPE_STRING.equals(parameter.getType())) &&
                                    !parameter.isMultiValued() &&
                                    GenericValidator.isBlankOrNull((String) getParameterValue(configObj, parameter.getName(), context))) {
                        throw new ResourceInitializationException(
                                new IllegalArgumentException("Required Parameter: " + parameter.getName() + " is not set.")
                        );
                    }
                }
                /** Set the parameter value in the class field variable **/
                try {
                    Field field = FieldUtils.getField(configObj.getClass(), parameter.getName(), true);
                    if (field != null) {
                        FieldUtils.writeField(field, configObj, getParameterValue(configObj, parameter.getName(), context), true);
                    }
                } catch (IllegalAccessException e) {
                    throw new ResourceInitializationException(e);
                }
            }
        } catch (IllegalAccessException e) {
            throw new ResourceInitializationException(e);
        }
    }

    /**
     * Return the value set in the configuration for the parameter named.  Gets the value from the UimaContext if
     * provided.  If configObj is a reader then gets the UimaContext from the getUimaContext method.  If no context or
     * the parameter is not set then a null value is returned.
     *
     * @param configObj object whose parameter value will be discovered
     * @param paramName name of the parameter
     * @param context   UimaContext with the parameter values
     * @param <T>       Type of the configuration Object
     * @param <V>       Type of the value that is returned
     * @return parameter value or null
     */
    public static <T, V> V getParameterValue(T configObj, String paramName, UimaContext context) {
        if (context == null && CollectionReader_ImplBase.class.isAssignableFrom(configObj.getClass()))
            context = ((CollectionReader_ImplBase) configObj).getUimaContext();
        if (context != null)
            return (V) context.getConfigParameterValue(paramName);
        return null;
    }

    /**
     * Get the multivalued and type parameter information from the field object. Collections are supported types for fields
     * but only collections with a single generic type parameter that can return an array.  If no type is matched then
     * the type is set to {@code ConfigurationParamter.TYPE_STRING}.
     *
     * @param field field object from which class information can be retrieved.
     * @param param ConfigurationParameter to store type and multi-value information.
     */
    protected static void getParameterTypeFromField(Field field, ConfigurationParameter param) {
        Class<?> fieldClass = field.getType();
        if (fieldClass.isArray()) {  //Array Type
            param.setMultiValued(true);
            param.setType(javaTypeToUimaType.get(fieldClass.getComponentType().getName()));
        } else if (Collection.class.isAssignableFrom(fieldClass)) { // Collection
            param.setMultiValued(true);
            param.setType(javaTypeToUimaType.get(((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).getName()));
        } else {
            param.setType(javaTypeToUimaType.get(fieldClass.getName()));
        }
        if (StringUtils.isBlank(param.getType())) {
            logger.warn("An appropriate type mapping for " + field.getName() + " could not be found." +
                    "May be an invalid annotator param type, setting to ConfigurationParameter.TYPE_STRING");
            param.setType(ConfigurationParameter.TYPE_STRING);
        }
    }
}
