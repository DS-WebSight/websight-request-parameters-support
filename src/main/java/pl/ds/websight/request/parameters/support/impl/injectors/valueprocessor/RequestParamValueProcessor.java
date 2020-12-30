package pl.ds.websight.request.parameters.support.impl.injectors.valueprocessor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class RequestParamValueProcessor {

    protected static final Map<Class<?>, Function<String, Object>> SIMPLE_TYPES_MAPPERS;

    static {
        SIMPLE_TYPES_MAPPERS = new HashMap<>();
        SIMPLE_TYPES_MAPPERS.put(String.class, value -> value);
        SIMPLE_TYPES_MAPPERS.put(Boolean.class, BooleanUtils::toBooleanObject);
        SIMPLE_TYPES_MAPPERS.put(Integer.class, Integer::valueOf);
        SIMPLE_TYPES_MAPPERS.put(Double.class, Double::valueOf);
        SIMPLE_TYPES_MAPPERS.put(Long.class, Long::valueOf);
        SIMPLE_TYPES_MAPPERS.put(Float.class, Float::valueOf);
        SIMPLE_TYPES_MAPPERS.put(Short.class, Short::valueOf);
        SIMPLE_TYPES_MAPPERS.put(Byte.class, Byte::valueOf);
    }

    protected final Type type;

    protected RequestParamValueProcessor(Type type) {
        this.type = type;
    }

    public abstract Object process();

    protected static Object mapSimpleValue(Class<?> targetClass, String value) {
        return SIMPLE_TYPES_MAPPERS.getOrDefault(targetClass, v -> null).apply(value);
    }

    protected static Stream<Enum> getMatchingEnums(String value, Class<?> targetClass) {
        return getEnums(targetClass)
                .filter(enumVal -> enumVal.toString().equals(value));
    }

    protected static Stream<Enum> getMatchingEnums(String[] values, Class<?> targetClass) {
        return getEnums(targetClass)
                .filter(enumVal -> ArrayUtils.contains(values, enumVal.toString()));
    }

    private static Stream<Enum> getEnums(Class<?> targetClass) {
        return Optional.of(targetClass)
                .map(Class::getEnumConstants)
                .map(enumConstantsObj -> (Enum[]) enumConstantsObj)
                .map(Arrays::stream)
                .orElseGet(Stream::empty);
    }
}
