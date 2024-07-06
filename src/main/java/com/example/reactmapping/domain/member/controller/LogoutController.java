package com.example.reactmapping.domain.member.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {
    @ResponseBody
    @PostMapping("/logout")
    public void logout() {
    }
}
