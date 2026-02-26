package com.wooya.alert.sse.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/nonShared")
    public String nonSharedPage() {
        return "forward:/index-non-shared.html";
    }
}
