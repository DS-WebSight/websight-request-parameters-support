package pl.ds.websight.request.parameters.support.impl.scripting;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.api.BindingsValuesProvider;
import org.osgi.service.component.annotations.Component;

import javax.script.Bindings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sightly HTTP parameters {@code BindingsValuesProvider}.
 */
@Component(
        service = BindingsValuesProvider.class,
        property = {
                "javax.script.name=sightly"
        }
)
public class HttpParametersBindingsValuesProvider implements BindingsValuesProvider {

    private static final String PARAMETERS = "parameters";

    @Override
    public void addBindings(Bindings bindings) {
        if (!bindings.containsKey(PARAMETERS)) {
            SlingHttpServletRequest request = (SlingHttpServletRequest) bindings.get(SlingBindings.REQUEST);
            if (request != null) {
                bindings.put(PARAMETERS, parametersAsMap(request.getRequestParameterMap()));
            }
        }
    }

    private Map<String, String> parametersAsMap(RequestParameterMap requestParameterMap) {
        Map<String, String> result = new HashMap<>();
        requestParameterMap.entrySet().stream().forEach(parameter
                -> result.put(parameter.getKey(), requestParameterToString(parameter.getValue())));
        return result;
    }

    private String requestParameterToString(RequestParameter[] value) {
        List<String> result = new ArrayList<>(value.length);
        for (RequestParameter parameter : value) {
            result.add(parameter.getString());
        }
        return result.stream().collect(Collectors.joining(","));
    }
}
