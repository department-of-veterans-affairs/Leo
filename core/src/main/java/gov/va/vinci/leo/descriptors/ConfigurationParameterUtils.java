package gov.va.vinci.leo.descriptors;

import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.log4j.Logger;
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

    public static <T> Map<ConfigurationParameterImpl, Object> getParamsToValuesMap(T paramObject) {
        Map<ConfigurationParameterImpl, Object> parameterObjectMap = new HashMap<>();

        try {
            ConfigurationParameterImpl[] parameters = getParams(paramObject.getClass());
            for(ConfigurationParameterImpl parameter : parameters) {
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

        for(Class<T> cls = c; cls != null; cls = (Class<T>) cls.getSuperclass()) {
            //Get the list of declared fields in this class and check for our Annotation
            for(Field field : cls.getDeclaredFields()) {
                if(!field.isAnnotationPresent(LeoConfigurationParameter.class))
                    continue;
                ConfigurationParameterImpl param = new ConfigurationParameterImpl();
                for(java.lang.annotation.Annotation a : field.getAnnotations()) {
                    if(a instanceof LeoConfigurationParameter) {
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
        for(Class decl : c.getClass().getDeclaredClasses()) {
            if(decl.isEnum() && Arrays.asList(decl.getInterfaces()).contains(ConfigurationParameter.class)) {
                parameterList.addAll(EnumSet.allOf(decl));
                break;
            } else if(decl.getCanonicalName().endsWith(".Param")) {
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
        if(fieldClass.isArray()) {  //Array Type
            param.setMultiValued(true);
            param.setType(javaTypeToUimaType.get(fieldClass.getComponentType().getName()));
        } else if(Collection.class.isAssignableFrom(fieldClass)) { // Collection
            param.setMultiValued(true);
            param.setType(javaTypeToUimaType.get(((Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]).getName()));
        } else {
            param.setType(javaTypeToUimaType.get(fieldClass.getName()));
        }
        if(StringUtils.isBlank(param.getType())) {
            logger.warn("An appropriate type mapping for " + field.getName() + " could not be found." +
                    "May be an invalid annotator param type, setting to ConfigurationParameter.TYPE_STRING");
            param.setType(ConfigurationParameter.TYPE_STRING);
        }
    }
}
