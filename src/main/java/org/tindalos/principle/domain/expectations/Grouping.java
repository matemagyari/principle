package org.tindalos.principle.domain.expectations;

public record Grouping(String name) {

     public static Grouping of() {
         return new Grouping("");
     }
}
