package com.example.controller;

import com.example.service.impl.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/websocket")
public class WebSocketController {

    @Autowired
    private WebSocketService webSocketService;


    @GetMapping("/pushone")
    public void pushone() {
        webSocketService.sendMessage("badao","公众号:霸道的程序猿");
    }
}