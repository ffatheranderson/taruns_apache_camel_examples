package com.camel.interceptor.bean;

import org.apache.camel.Consume;
import org.springframework.stereotype.Component;

import static com.camel.interceptor.restdsl.RestDslRouteBuilder.*;

@Component
public class TestConsumer {

	public static final String RETURN_STRING_ENDPOINT = "direct:return-string";


	@Consume(ENDPOINT_1)
	public void endpoint1() {
		System.out.println("inside: com.camel.interceptor.bean.TestConsumer.endpoint1");
	}

	@Consume(ENDPOINT_2)
	public void endpoint2() {
		System.out.println("inside: com.camel.interceptor.bean.TestConsumer.endpoint2");
	}

	@Consume(MY_AFTER_URI)
	public void afterUri() {
		System.out.println("inside: com.camel.interceptor.bean.TestConsumer.afterUri");
	}

	@Consume(RETURN_STRING_ENDPOINT)
	public String returnString() {
		System.out.println("inside : com.camel.interceptor.bean.TestConsumer.returnString");
		return "Success.";
	}
}
