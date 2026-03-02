package com.aleonisp.singlesave.controller;

import com.aleonisp.singlesave.dto.AccountResponse;
import com.aleonisp.singlesave.service.AccountService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public Mono<AccountResponse> account(OAuth2AuthenticationToken auth) {
        return accountService.getCurrentAccount(auth);
    }
}