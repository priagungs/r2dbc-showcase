package com.ps.r2dbcshowcase;

import com.ps.r2dbcshowcase.entities.Hobby;
import com.ps.r2dbcshowcase.entities.Person;
import com.ps.r2dbcshowcase.repositories.HobbyRepository;
import com.ps.r2dbcshowcase.repositories.PersonRepository;
import com.ps.r2dbcshowcase.utils.DbUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class R2dbcShowcaseApplication {

  public static void main(String[] args) {
    SpringApplication.run(R2dbcShowcaseApplication.class, args);
  }

  @Bean
  CommandLineRunner runner(DbUtil dbUtil, PersonRepository personRepository, HobbyRepository hobbyRepository) {
    return args -> {
      Mono<Person> personMono = personRepository.save(Person.builder().name("Priagung").build());
      personMono
              .flatMap(person -> {
                Hobby hobby = Hobby.builder().name("mancing").personId(person.getId()).build();
                return hobbyRepository.save(hobby);
              })
              .subscribe(System.out::println, System.out::println);
//              .flatMap(hobby ->  dbUtil.fetchOneForeign(personMono, hobby.getClass()))

      dbUtil.fetchOneForeign(hobbyRepository.findById(1), Person.class)
              .subscribe(System.out::println, System.out::println);

    };
  }

}
