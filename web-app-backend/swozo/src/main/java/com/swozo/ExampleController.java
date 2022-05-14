package com.swozo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
public class ExampleController {

    private record JsonTest(String text, int number){
    }

    @GetMapping
    public String getExample() {
        return "Swozo";
    }

    @GetMapping("/json")
    public JsonTest getExampleJson() {
        return new JsonTest("test", 1);
    }

}
