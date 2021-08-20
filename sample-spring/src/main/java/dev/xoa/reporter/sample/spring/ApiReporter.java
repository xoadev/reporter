package dev.xoa.reporter.sample.spring;

import dev.xoa.reporter.Reporter;

@Reporter
public interface ApiReporter {

    void infoHello_Requested(String name);

    void incrementHelloRequestedWithName_(String name);
}
