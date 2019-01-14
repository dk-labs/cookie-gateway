package org.example.dk.cookiegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    private GatewayFilter useCookieFilter =
            (exchange, chain) -> {
                var cookies = exchange.getRequest().getCookies();
                System.out.println("CAS SPY cookie value: " + cookies.getFirst("CAS_SPY_COOKIE"));
                return chain.filter(exchange);
            };

    @Bean
    RouteLocator routes(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("examine_and_use_cookie_route", r -> r.host("dk.example.org:9090")
                        .and()
                        .path("/")
                        .filters(f -> f.filter(useCookieFilter))
                        .uri("http://dk.example.org:8080"))
                .build();
    }
}
