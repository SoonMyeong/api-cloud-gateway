package com.example.springcloudgateway.filter;

import com.example.springcloudgateway.JwtBuildUtil;
import java.nio.charset.StandardCharsets;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


//GlobalFilter 도 있는데, 이건 전역으로 필터 걸 때 impl 하여 사용
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config>
{

    private static final String AUTHORIZATION = "Authorization";
    private final JwtBuildUtil jwtBuildUtil;

    public JwtAuthenticationFilter(JwtBuildUtil jwtBuildUtil)
    {
        super(Config.class);
        this.jwtBuildUtil = jwtBuildUtil;
    }

    @Override
    public GatewayFilter apply(JwtAuthenticationFilter.Config config)
    {

        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if(!request.getHeaders().containsKey(AUTHORIZATION))
            {
                return onError(exchange, "header 에 Authorization 없음");
            }

            String token = String.valueOf(request.getHeaders().get(AUTHORIZATION)).substring(7);

            if(!jwtBuildUtil.validateToken(token))
            {
                return onError(exchange, "JWT 유효성 에러");
            }

            return chain.filter(exchange);
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String msg)
    {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = response.bufferFactory().wrap(msg.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config
    {
    }
}
