package com.example.qrcode.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by patterncat on 2017-10-30.
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping(value = "")
    public String demo(){
        return "demo";
    }
}
