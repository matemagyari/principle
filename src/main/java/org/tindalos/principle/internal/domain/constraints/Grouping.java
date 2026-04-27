package org.tindalos.principle.internal.domain.constraints;

public record Grouping(String name) {

     public static Grouping of() {
         return new Grouping("");
     }
}
