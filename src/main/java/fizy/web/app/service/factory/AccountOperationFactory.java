package fizy.web.app.service.factory;

import fizy.web.app.dto.AccountDto;
import fizy.web.app.dto.ConvertDto;
import fizy.web.app.dto.TransferDto;
import fizy.web.app.entity.Account;
import fizy.web.app.entity.Transaction;
import fizy.web.app.service.operation.AccountOperation;
import fizy.web.app.service.operation.impl.ConvertCurrencyOperation;
import fizy.web.app.service.operation.impl.CreateAccountOperation;
import fizy.web.app.service.operation.impl.TransferFundsOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountOperationFactory {
    private final CreateAccountOperation createAccountOperation;
    private final TransferFundsOperation transferFundsOperation;
    private final ConvertCurrencyOperation convertCurrencyOperation;

    public AccountOperation<AccountDto, Account> getCreateAccountOperation() {
        return createAccountOperation;
    }

    public AccountOperation<TransferDto, Transaction> getTransferFundsOperation() {
        return transferFundsOperation;
    }

    public AccountOperation<ConvertDto, Transaction> getConvertCurrencyOperation() {
        return convertCurrencyOperation;
    }
}