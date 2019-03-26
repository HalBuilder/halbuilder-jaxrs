module com.theoryinpractise.halbuilder.jaxrs {
    exports com.theoryinpractise.halbuilder.jaxrs;
    requires java.ws.rs;
    requires transitive com.theoryinpractise.halbuilder.api;
    requires transitive com.theoryinpractise.halbuilder.json;
    requires transitive com.theoryinpractise.halbuilder.standard;
}
