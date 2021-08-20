package dev.xoa.reporter.sample.spring;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ApiController {

    private final ApiReporter reporter;

    @GetMapping(
            value = "/hello/{name}",
            produces = "text/plain"
    )
    public String helloWorld(@PathVariable("name") String name) {
        reporter.infoHello_Requested(name);
        reporter.incrementHelloRequestedWithName_(name);

        return "Hello " + name + "!!!";
    }
}
