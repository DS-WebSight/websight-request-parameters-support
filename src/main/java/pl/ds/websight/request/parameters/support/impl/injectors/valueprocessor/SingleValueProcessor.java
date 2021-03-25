package pl.ds.websight.request.parameters.support.impl.injectors.valueprocessor;

import java.lang.reflect.Type;

public class SingleValueProcessor extends RequestParamValueProcessor {

    private final String value;

    protected SingleValueProcessor(Type type, String value) {
        super(type);
        this.value = value;
    }

    @Override
    public Object process() {
        Class<?> targetClass = (Class<?>) type;
        if (targetClass.isEnum()) {
            return getMatchingEnums(value, targetClass)
                    .findFirst()
                    .orElse(null);
        }
        return mapSimpleValue(targetClass, value);
    }
}
