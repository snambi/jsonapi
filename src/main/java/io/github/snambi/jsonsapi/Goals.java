package io.github.snambi.jsonsapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Goals {
    private String team;
    private int year;
    private int goals;
}