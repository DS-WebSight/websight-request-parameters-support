package pl.ds.websight.request.parameters.support.impl.injectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ds.websight.request.parameters.support.annotations.RequestParameter;
import pl.ds.websight.request.parameters.support.impl.injectors.valueprocessor.RequestParamValueProcessor;
import pl.ds.websight.request.parameters.support.impl.injectors.valueprocessor.RequestParamValueProcessorProvider;

import javax.servlet.ServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

@Component(
        property = {
                Constants.SERVICE_RANKING + ":Integer=4200"
        }
)
public class RequestParameterInjector implements Injector, StaticInjectAnnotationProcessorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParameterInjector.class);

    @Override
    public @NotNull String getName() {
        return "request-parameters";
    }

    @Override
    public @Nullable Object getValue(@NotNull Object adaptable, String name, @NotNull Type type,
                                     @NotNull AnnotatedElement annotatedElement, @NotNull DisposalCallbackRegistry disposalCallbackRegistry) {
        return !(adaptable instanceof ServletRequest) ? null : getParameter((ServletRequest) adaptable, type, name);
    }

    private Object getParameter(@NotNull ServletRequest request, @NotNull Type type, String name) {
        String[] values = request.getParameterValues(name);
        if (ArrayUtils.isEmpty(values)) {
            return null;
        }
        try {
            RequestParamValueProcessor valueProcessor = RequestParamValueProcessorProvider.create(type, values);
            if (valueProcessor != null) {
                return valueProcessor.process();
            }
        } catch (NumberFormatException nfe) {
            LOG.debug("Cannot parse parameter: {} of a type: {} with value: {}", name, type, values, nfe);
        }

        return null;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement annotatedElement) {
        RequestParameter annotation = annotatedElement.getAnnotation(RequestParameter.class);
        return annotation != null ? new RequestParameterAnnotationProcessor(annotation) : null;
    }

    private static class RequestParameterAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final RequestParameter annotation;

        public RequestParameterAnnotationProcessor(RequestParameter annotation) {
            this.annotation = annotation;
        }

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }

        @Override
        @SuppressWarnings("deprecation")
        public Boolean isOptional() {
            return annotation.optional();
        }

        @Override
        public String getName() {
            // Since null is not allowed as default value in annotations,
            // the blank string means, the default should be used!
            return StringUtils.isNotBlank(annotation.name()) ? annotation.name() : null;
        }
    }
}
