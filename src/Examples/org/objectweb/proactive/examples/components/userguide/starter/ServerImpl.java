package org.objectweb.proactive.examples.components.userguide.starter;

public class ServerImpl implements Service {
    @Override
    public void print(String msg) {
        System.err.println("=> Server: " + msg);
    }
}