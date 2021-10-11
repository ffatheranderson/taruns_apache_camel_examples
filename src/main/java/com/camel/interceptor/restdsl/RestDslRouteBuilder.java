package com.camel.interceptor.restdsl;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import static com.camel.interceptor.bean.TestConsumer.RETURN_STRING_ENDPOINT;

@Component
public class RestDslRouteBuilder extends RouteBuilder {

    private static final String SAMPLE_ENDPOINT = "direct:sampleEndpoint";
    private static final String LOCAL_SERVICE = "direct:localService";
    private static final String AFTER_URI_PROCESSOR = "direct:afterURLProcessor";
    private static final String AFTER_URI = "afterURI";
    private static final String ROUTE_ID = "direct-route";

    public static final String MY_ROUTE_EXMPL = "direct:my-route";
    public static final String ENDPOINT_1 = "direct:endpoint-1";
    public static final String ENDPOINT_2 = "direct:endpoint-2";
    public static final String MY_AFTER_URI = "direct:my-after-uri";


    @Override
    public void configure() {

        interceptSendToEndpoint(SAMPLE_ENDPOINT)
                .log(LoggingLevel.INFO, "Interceptor triggerred for SAMPLE_ENDPOINT to check condition for AFTER_URI")
                .afterUrl(AFTER_URI_PROCESSOR);


        //This is the solution I was looking for.
        interceptSendToEndpoint(ENDPOINT_2)
                .when(header(AFTER_URI).isEqualTo("true"))
                .skipSendToOriginalEndpoint()
                .to(ENDPOINT_2)
                .afterUrl(MY_AFTER_URI);

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.auto);

        // Rest API configuration
        rest()
                .path("/api")
                .consumes("application/json")
                .produces("application/json")

            // HTTP: GET /api
            .get()
            .outType(String.class)
            .to(LOCAL_SERVICE);

        from(LOCAL_SERVICE).routeId(ROUTE_ID)
            .log(LoggingLevel.INFO, "Inside " + ROUTE_ID + ", now Calling SAMPLE_ENDPOINT")
            .to(SAMPLE_ENDPOINT)
            .log(LoggingLevel.INFO, "Returning final response")
            .to("bean:getBean"); // This will invoke the Spring bean 'getBean'*/

        from(SAMPLE_ENDPOINT)
                .log(LoggingLevel.INFO, "Request received at sampleEndpoint");

        from(AFTER_URI_PROCESSOR)
                .choice()
                .when(header(AFTER_URI).isEqualTo("true"))
                .log(LoggingLevel.INFO, "afterURI was true, now executing the afterURI logic")
                .otherwise()
                .log(LoggingLevel.INFO, "afterURI condition not met")
                .endChoice();



        //My example endpoint.
        rest()
                .path("/myApi")
                .consumes("application/json")
                .produces("application/json")

                // HTTP: GET /api
                .get()
                .outType(String.class)
                .to(MY_ROUTE_EXMPL);

        from(MY_ROUTE_EXMPL)
                .log("after MY_ROUTE_EXMPL log")
                .to(ENDPOINT_1)
                .log("after ENDPOINT_1")
                .to(ENDPOINT_2)
                .log("after ENDPOINT_2")
                .to(RETURN_STRING_ENDPOINT)
                .log("after RETURN_STRING_ENDPOINT");

    }
}
