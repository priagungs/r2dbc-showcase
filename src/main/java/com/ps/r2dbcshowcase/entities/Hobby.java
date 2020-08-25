package com.ps.r2dbcshowcase.entities;

import com.ps.r2dbcshowcase.annotations.Foreign;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table
public class Hobby {

  @Id
  private Integer id;

  private String name;

  @Foreign(entity="person")
  private Integer personId;

}
