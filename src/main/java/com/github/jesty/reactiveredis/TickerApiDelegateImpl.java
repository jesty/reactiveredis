package com.github.jesty.reactiveredis;

import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import com.github.jesty.reactiveredis.web.api.TickerApiDelegate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TickerApiDelegateImpl implements TickerApiDelegate {

	@Override
	public Mono<ResponseEntity<Flux<BigDecimal>>> tick(ServerWebExchange exchange) {
		Flux<BigDecimal> myflux = exchange	.getRequest()
											.getHeaders()
											.getAccept()
											.stream()
											.filter(mediaType -> mediaType.equals(MediaType.TEXT_EVENT_STREAM))
											.findFirst()
											.map(mt -> buildFlux())
											.orElse(Flux.just(BigDecimal.ONE));

		return Mono.just(ResponseEntity	.ok()
										.body(myflux));

	}

	private Flux<BigDecimal> buildFlux() {
		return Flux	.range(1, 10)
					.map(n -> new BigDecimal(n))
					.doOnNext(System.out::println)
					.delayElements(Duration.ofSeconds(1));
	}

}
