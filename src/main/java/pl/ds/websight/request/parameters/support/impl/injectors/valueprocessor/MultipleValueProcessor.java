package pl.ds.websight.request.parameters.support.impl.injectors.valueprocessor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MultipleValueProcessor extends RequestParamValueProcessor {

    private final String[] values;

    protected MultipleValueProcessor(Type type, String[] values) {
        super(type);
        this.values = values;
    }

    @Override
    public Object process() {
        return extractParameterClass((ParameterizedType) type)
                .map(targetClass -> targetClass.isEnum() ?
                        getMatchingEnums(values, targetClass).collect(toList()) :
                        convertSimpleValues(values, targetClass))
                .orElse(null);
    }

    private static Optional<? extends Class<?>> extractParameterClass(ParameterizedType type) {
        return Optional.of(type)
                .map(ParameterizedType::getActualTypeArguments)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(typeVal -> typeVal instanceof Class<?>)
                .map(typeVal -> (Class<?>) typeVal)
                .findFirst();
    }

    private static List<Object> convertSimpleValues(String[] values, Class<?> targetClass) {
        return Arrays.stream(values)
                .filter(value -> SIMPLE_TYPES_MAPPERS.containsKey(targetClass))
                .map(value -> mapSimpleValue(targetClass, value))
                .collect(toList());
    }
}
