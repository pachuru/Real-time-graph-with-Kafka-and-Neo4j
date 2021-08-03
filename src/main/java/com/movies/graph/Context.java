package com.movies.graph;

import io.github.cdimascio.dotenv.Dotenv;

public class Context {

    private Dotenv dotenv;

    public Context() {
        this.dotenv = Dotenv.load();
    }

    public String getEnvVar(String envVarName) {
        return dotenv.get(envVarName);
    }
}
