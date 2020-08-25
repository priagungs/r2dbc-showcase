package com.ps.r2dbcshowcase.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table
public class Person {
  
  @Id
  private Integer id;

  private String name;

}
