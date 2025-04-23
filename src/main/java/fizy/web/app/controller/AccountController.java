package fizy.web.app.controller;


import fizy.web.app.dto.AccountDto;
import fizy.web.app.dto.ConvertDto;
import fizy.web.app.dto.TransferDto;
import fizy.web.app.entity.Account;
import fizy.web.app.entity.Transaction;
import fizy.web.app.entity.User;
import fizy.web.app.service.AccountService;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto, Authentication authentication) throws Exception {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.createAccount(accountDto, user));
    }

    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.getUserAccounts(user.getUid()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferFunds(@RequestBody TransferDto transferDto, Authentication authentication) throws Exception {
        var user = (User) authentication.getPrincipal();
        // Implement transfer logic here
        return ResponseEntity.ok(accountService.transferFunds(transferDto, user));
    }

    @GetMapping("/rates")
    public ResponseEntity<Map<String, Double>> getExchangeRates() {
        return ResponseEntity.ok(accountService.getExchangeRates());
    }

    @PostMapping("/convert")
    public ResponseEntity<Transaction> convertCurrency(@RequestBody ConvertDto convertDto, Authentication authentication) throws Exception {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.convertCurrency(convertDto, user));
    }
}
