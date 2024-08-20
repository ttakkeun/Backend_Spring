package ttakkeun.ttakkeun_server.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @GetMapping(value = "/test/cicd")
    @ResponseBody
    public String helloRuckus(Model model) {
        return "테스트";
    }

}