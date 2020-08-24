package com.zone24x7.faume.webapp;

import com.zone24x7.faume.webapp.util.AppConfigStringConstants;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import javax.net.ssl.SSLException;
import java.util.concurrent.TimeUnit;

/**
 * Class to represent the spring main configurations and bindings.
 */
@Configuration
public class SpringMainConfig {
    @Value(AppConfigStringConstants.CONFIG_REST_TEMPLATE_CONN_TIMEOUT_IN_MILLIS)
    private int restTemplateConnectionTimeoutInMillis;

    @Value(AppConfigStringConstants.CONFIG_REST_TEMPLATE_READ_TIMEOUT_IN_MILLIS)
    private long restTemplateReadTimeoutInMillis;

    /**
     * Method to generate a bean to autowire WebClient builder instance
     *
     * @return WebClient.Builder web client builder instance
     */
    @Bean("webClientBuilder")
    public WebClient.Builder getWebClientBuilder() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, restTemplateConnectionTimeoutInMillis)
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(restTemplateReadTimeoutInMillis, TimeUnit.MILLISECONDS)));

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient).secure(t -> t.sslContext(sslContext))));
    }
}
