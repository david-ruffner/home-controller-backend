package com.davidruffner.homecontrollerbackend.config;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class RestClientConfig {

    private static final String ICAL_URL = "https://caldav.icloud.com";

    @Autowired
    TodoistConfig todoistConfig;

    @Bean("GeoapifyRestClient")
    RestClient geoapifyRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
            .secure(ssl -> {
                try {
                    ssl.sslContext(
                        io.netty.handler.ssl.SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build()
                    );
                } catch (SSLException e) {
                    throw new RuntimeException(e);
                }
            });

        ReactorClientHttpRequestFactory requestFactory = new ReactorClientHttpRequestFactory(httpClient);

        return builder
            .baseUrl("https://api.geoapify.com") // TODO: Configify this value
            .defaultHeader("Accept", "application/json")
            .requestFactory(requestFactory)
            .build();
    }

    @Bean("HueRestClient")
    RestClient hueRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
            .secure(ssl -> {
                try {
                    ssl.sslContext(
                        io.netty.handler.ssl.SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build()
                    );
                } catch (SSLException e) {
                    throw new RuntimeException(e);
                }
            });

        ReactorClientHttpRequestFactory requestFactory = new ReactorClientHttpRequestFactory(httpClient);

        return builder
            .baseUrl("https://192.168.50.231") // TODO: Configify this value
            .defaultHeader("Accept", "application/json")
            .requestFactory(requestFactory)
            .build();
    }

    @Bean("NWSRestClient")
    RestClient nwsRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
            .secure(ssl -> {
                try {
                    ssl.sslContext(
                        io.netty.handler.ssl.SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build()
                    );
                } catch (SSLException e) {
                    throw new RuntimeException(e);
                }
            });

        ReactorClientHttpRequestFactory requestFactory = new ReactorClientHttpRequestFactory(httpClient);

        return builder
            .baseUrl("https://api.weather.gov") // TODO: Configify this value
            .defaultHeader("Accept", "application/json")
            .requestFactory(requestFactory)
            .build();
    }

    public RestClient calendarRestClientCustom(RestClient.Builder builder, String host) {
        HttpClient httpClient = HttpClient.create()
            .secure(ssl -> {
                try {
                    ssl.sslContext(
                        io.netty.handler.ssl.SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build()
                    );
                } catch (SSLException e) {
                    throw new RuntimeException(e);
                }
            });

        ReactorClientHttpRequestFactory requestFactory = new ReactorClientHttpRequestFactory(httpClient);

        return builder
            .baseUrl(host)
            .defaultHeader("Accept", "application/json")
            .requestFactory(requestFactory)
            .build();
    }

    @Bean("CalendarRestClient")
    RestClient calendarRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
            .secure(ssl -> {
                try {
                    ssl.sslContext(
                        io.netty.handler.ssl.SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build()
                    );
                } catch (SSLException e) {
                    throw new RuntimeException(e);
                }
            });

        ReactorClientHttpRequestFactory requestFactory = new ReactorClientHttpRequestFactory(httpClient);

        return builder
            .baseUrl(ICAL_URL)
            .defaultHeader("Accept", "application/json")
            .requestFactory(requestFactory)
            .build();
    }

    @Bean("TodoistRestClient")
    RestClient todoistRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
            .secure(ssl -> {
                try {
                    ssl.sslContext(
                        io.netty.handler.ssl.SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build()
                    );
                } catch (SSLException e) {
                    throw new RuntimeException(e);
                }
            });

        ReactorClientHttpRequestFactory requestFactory = new ReactorClientHttpRequestFactory(httpClient);

        return builder
            .baseUrl(this.todoistConfig.getApiUrl())
            .defaultHeader("Accept", "application/json")
            .requestFactory(requestFactory)
            .build();
    }
}
