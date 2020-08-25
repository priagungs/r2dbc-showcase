package com.ps.r2dbcshowcase.utils;

import com.ps.r2dbcshowcase.annotations.Foreign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.relational.core.query.Criteria.where;

@Service
public class DbUtilImpl implements DbUtil {

  @Autowired
  private DatabaseClient databaseClient;

  @Override
  public <T1, T2> Mono<T2> fetchParent(Mono<T1> child, Class<T2> parentClass) {
    return child
            .map(this::validateTableAnnotation)
            .flatMap(t1 -> {
              Field field = this.getForeignKey(t1.getClass(), parentClass);
              field.setAccessible(true);
              Object foreignKeyId = null;
              try {
                foreignKeyId = field.get(t1);
              } catch (IllegalAccessException e) {
                e.printStackTrace();
                return Mono.error(new RuntimeException(e));
              }
              String primaryKeyField = this.getIdField(parentClass).getName();
              return databaseClient.select()
                      .from(parentClass)
                      .matching(where(primaryKeyField).is(Optional.ofNullable(foreignKeyId).orElse("")))
                      .fetch().one();
            });
  }

  @Override
  public <T1, T2> Flux<T2> fetchChilds(Mono<T1> parent, Class<T2> childClass) {
    Mono<String> foreignKeyFieldName = parent
            .map(Object::getClass)
            .map(parentClass -> this.getForeignKey(childClass, parentClass))
            .map(Field::getName);

    return parent
            .map(this::validateTableAnnotation)
            .map(p -> this.getIdField(p.getClass()))
            .zipWith(parent, this::getIdFromField)
            .zipWith(foreignKeyFieldName)
            .flatMapMany(tuple -> {
              Object parentId = tuple.getT1();
              String fkFieldName = tuple.getT2();
              return databaseClient.select()
                      .from(childClass)
                      .matching(where(fkFieldName).is(parentId))
                      .fetch().all();
            });

  }

  private <T> Object getIdFromField(Field field, T parent) {
    field.setAccessible(true);
    try {
      return field.get(parent);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private <T> T validateTableAnnotation(T t) {
    if (Objects.isNull(t.getClass().getAnnotation(Table.class))) {
      throw new RuntimeException("Invalid entity");
    }
    return t;
  }

  private Field getForeignKey(Class<?> childClass, Class<?> parentClass) {
    return Arrays.stream(childClass.getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(Foreign.class))
            .filter(fi -> fi.getAnnotation(Foreign.class).entity().equalsIgnoreCase(parentClass.getSimpleName()))
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
