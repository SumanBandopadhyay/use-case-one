package com.dbs.usecaseone.controller;

import com.dbs.usecaseone.service.AccountService;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/convert")
    public ResponseEntity<Resource> convertAccountJson(@RequestBody String account) throws ProcessingException, IOException {
        Resource resource = accountService.convertStringToDatFile(account);

        // Set the content type and attachment header
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=accountAttributes.dat");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Return the response entity with the file resource
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }


}
