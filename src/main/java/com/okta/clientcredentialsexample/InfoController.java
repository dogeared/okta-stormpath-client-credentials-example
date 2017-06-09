package com.okta.clientcredentialsexample;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class InfoController {

    @RequestMapping("/info")
    public @ResponseBody String info(Principal principal) {
        return principal.getName();
    }
}
