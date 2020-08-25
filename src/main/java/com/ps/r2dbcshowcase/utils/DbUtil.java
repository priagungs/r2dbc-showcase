package com.ps.r2dbcshowcase.utils;

import com.ps.r2dbcshowcase.annotations.Foreign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.relational.core.query.Criteria.where;

@Service
public class DbUtil {

  @Autowired
  private DatabaseClient databaseClient;

  public <T1, T2> Mono<T2> fetchOneForeign(Mono<T1> record, Class<T2> classTable2) {
    return record
            .map(this::validateTableAnnotation)
            .flatMap(t1 -> {
              Field field = this.getForeignKey(t1.getClass(), classTable2);
              field.setAccessible(true);
              Object foreignKeyId = null;
              try {
                foreignKeyId = field.get(t1);
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
              String entityName = field.getAnnotation(Foreign.class).entity();
              String primaryKeyField = this.getIdField(classTable2).getName();

              return databaseClient.select()
                      .from(entityName)
                      .matching(where(primaryKeyField).is(Optional.ofNullable(foreignKeyId).orElse("")))
                      .as(classTable2)
                      .fetch().one();

            });
  }

  private <T1> T1 validateTableAnnotation(T1 t) {
    if (Objects.isNull(t.getClass().getAnnotation(Table.class))) {
      throw new RuntimeException("Invalid entity");
    }
    return t;
  }

  private Field getForeignKey(Class<?> clazz1, Class<?> clazz2) {
    return Arrays.stream(clazz1.getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(Foreign.class))
            .filter(fi -> fi.getAnnotation(Foreign.class).entity().equalsIgnoreCase(clazz2.getSimpleName()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No foreign key found"));
  }

  private Field getIdField(Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(Id.class))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No primary key found"));

  }

}
