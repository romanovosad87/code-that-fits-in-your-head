package org.example;


import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.ApiGatewayRequestIdentity;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyRequestContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.crac.Core;
import org.crac.Resource;
import org.example.exception.CouldNotInitializeApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler, Resource {

    private static final Logger logger = LoggerFactory.getLogger(StreamLambdaHandler.class);

    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);
        } catch (ContainerInitializationException e) {
            throw new CouldNotInitializeApplicationException("Could not initialize Spring Boot application", e);
        }
    }

    public StreamLambdaHandler () {
        Core.getGlobalContext().register(this);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }

    @Override
    public void beforeCheckpoint(org.crac.Context<? extends Resource> context) throws Exception {
        logger.info("Preparing snapshot (before checkpoint)");
        handler.proxy(getAwsProxyRequest(), new MockLambdaContext());
    }

    @Override
    public void afterRestore(org.crac.Context<? extends Resource> context) throws Exception {
        logger.info("Snapshot restored (after restore)");
    }


    private static AwsProxyRequest getAwsProxyRequest () {
        final AwsProxyRequest awsProxyRequest = new AwsProxyRequest();
        awsProxyRequest.setHttpMethod(HttpMethod.GET.name());
        awsProxyRequest.setPath("/reservation/1");

        final AwsProxyRequestContext awsProxyRequestContext = new AwsProxyRequestContext();
        final ApiGatewayRequestIdentity apiGatewayRequestIdentity= new ApiGatewayRequestIdentity();
        apiGatewayRequestIdentity.setApiKey("key");
        awsProxyRequestContext.setIdentity(apiGatewayRequestIdentity);
        awsProxyRequest.setRequestContext(awsProxyRequestContext);

        return awsProxyRequest;
    }
}