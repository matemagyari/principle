module org.tindalos.guardrails {
    exports org.tindalos.guardrails.api;

    requires commons.io;
    requires commons.lang3;
    requires guava;
    requires jdepend;
    requires maven.plugin.api;
    requires snakeyaml;

    requires static maven.plugin.annotations;
}
