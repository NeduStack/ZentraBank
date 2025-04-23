package fizy.web.app.service;


import fizy.web.app.dto.AccountDto;
import fizy.web.app.entity.Account;
import fizy.web.app.entity.User;
import fizy.web.app.repository.AccountRepository;
import fizy.web.app.service.helper.AccountHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountHelper accountHelper;

    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        return accountHelper.createAccount(accountDto, user);
    }

    public List<Account> getUserAccounts(String uid) {
        return accountRepository.findAllByOwnerUid(uid);
    }
}
