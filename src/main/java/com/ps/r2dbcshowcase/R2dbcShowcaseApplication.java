package com.ps.r2dbcshowcase;

import com.ps.r2dbcshowcase.entities.Hobby;
import com.ps.r2dbcshowcase.entities.Person;
import com.ps.r2dbcshowcase.repositories.HobbyRepository;
import com.ps.r2dbcshowcase.repositories.PersonRepository;
import com.ps.r2dbcshowcase.utils.DbUtilImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class R2dbcShowcaseApplication {

  public static void main(String[] args) {
    SpringApplication.run(R2dbcShowcaseApplication.class, args);
  }

  @Bean
  CommandLineRunner runner(DbUtilImpl dbUtilImpl, PersonRepository personRepository, HobbyRepository hobbyRepository) {
    return args -> {
      Mono<Person> personMono = personRepository.save(Person.builder().name("Priagung").build());
      personMono
              .flatMap(person -> {
                Hobby hobby = Hobby.builder().name("mancing").personId(person.getId()).build();
                return hobbyRepository.save(hobby);
              })
              .subscribe(System.out::println, System.out::println);
//              .flatMap(hobby ->  dbUtil.fetchOneForeign(personMono, hobby.getClass()))

      dbUtilImpl.fetchParent(hobbyRepository.findById(1), Person.class)
              .subscribe(System.out::println, System.out::println);

      dbUtilImpl.fetchChilds(personRepository.findById(1), Hobby.class)
              .subscribe(System.out::println, System.out::println);

    };
  }

}
