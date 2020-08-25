package com.ps.r2dbcshowcase.utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DbUtil {
  <T1, T2> Mono<T2> fetchParent(Mono<T1> child, Class<T2> parentClass);
  <T1, T2> Flux<T2> fetchChilds(Mono<T1> parent, Class<T2> childClass);
}
