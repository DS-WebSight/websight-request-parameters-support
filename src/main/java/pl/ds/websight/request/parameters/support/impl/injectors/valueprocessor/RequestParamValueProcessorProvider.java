package pl.ds.websight.request.parameters.support.impl.injectors.valueprocessor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RequestParamValueProcessorProvider {

    private static final Set<Class<?>> SUPPORTED_COLLECTIONS_TYPES;

    static {
        SUPPORTED_COLLECTIONS_TYPES = new HashSet<>();
        SUPPORTED_COLLECTIONS_TYPES.add(Collection.class);
        SUPPORTED_COLLECTIONS_TYPES.add(List.class);
    }

    private RequestParamValueProcessorProvider() {
        // no instance
    }

    public static RequestParamValueProcessor create(Type type, String[] values) {
        if (isSimpleType(type)) {
            return new SingleValueProcessor(type, values[0]);
        } else if (isSupportedCollectionType(type)) {
            return new MultipleValueProcessor(type, values);
        }
        return null;
    }

    private static boolean isSimpleType(Type declaredType) {
        return declaredType instanceof Class<?>;
    }

    private static boolean isSupportedCollectionType(Type declaredType) {
        if (declaredType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) declaredType;
            Class<?> collectionType = (Class<?>) type.getRawType();
            return SUPPORTED_COLLECTIONS_TYPES.contains(collectionType);
        }
        return false;
    }
}
