package org.tindalos.guardrails.internal.domain.constraints;

public record Grouping(String name) {

     public static Grouping of() {
         return new Grouping("");
     }
}
