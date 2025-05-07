package fizy.web.app.service;

import fizy.web.app.dto.AccountDto;
import fizy.web.app.dto.ConvertDto;
import fizy.web.app.dto.TransferDto;
import fizy.web.app.entity.Account;
import fizy.web.app.entity.Transaction;
import fizy.web.app.entity.User;
import fizy.web.app.repository.AccountRepository;
import fizy.web.app.service.factory.AccountOperationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountOperationFactory operationFactory;
    private final ExchangeRateService exchangeRateService;

    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        return operationFactory.getCreateAccountOperation().execute(accountDto, user);
    }

    public List<Account> getUserAccounts(String uid) {
        return accountRepository.findAllByOwnerUid(uid);
    }

    public Transaction transferFunds(TransferDto transferDto, User user) throws Exception {
        return operationFactory.getTransferFundsOperation().execute(transferDto, user);
    }

    public Map<String, Double> getExchangeRates() {
        return exchangeRateService.getRates();
    }

    public Transaction convertCurrency(ConvertDto convertDto, User user) throws Exception {
        return operationFactory.getConvertCurrencyOperation().execute(convertDto, user);
    }
}