package fizy.web.app.service.operation.impl;

import fizy.web.app.dto.AccountDto;
import fizy.web.app.entity.Account;
import fizy.web.app.entity.User;
import fizy.web.app.service.helper.AccountHelper;
import fizy.web.app.service.operation.AccountOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAccountOperation implements AccountOperation<AccountDto, Account> {
    private final AccountHelper accountHelper;

    @Override
    public Account execute(AccountDto request, User user) throws Exception {
        return accountHelper.createAccount(request, user);
    }
}