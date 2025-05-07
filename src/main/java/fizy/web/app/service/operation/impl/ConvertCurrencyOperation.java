package fizy.web.app.service.operation.impl;

import fizy.web.app.dto.ConvertDto;
import fizy.web.app.entity.Transaction;
import fizy.web.app.entity.User;
import fizy.web.app.service.helper.AccountHelper;
import fizy.web.app.service.operation.AccountOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConvertCurrencyOperation implements AccountOperation<ConvertDto, Transaction> {
    private final AccountHelper accountHelper;

    @Override
    public Transaction execute(ConvertDto request, User user) throws Exception {
        return accountHelper.convertCurrency(request, user);
    }
}