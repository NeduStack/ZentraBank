package fizy.web.app.service.helper;


import fizy.web.app.dto.AccountDto;
import fizy.web.app.dto.ConvertDto;
import fizy.web.app.entity.*;
import fizy.web.app.repository.AccountRepository;
import fizy.web.app.repository.TransactionRepository;
import fizy.web.app.service.ExchangeRateService;
import fizy.web.app.util.RandomUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.naming.OperationNotSupportedException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Getter
public class AccountHelper {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final Logger logger = LoggerFactory.getLogger(AccountHelper.class);
    private final ExchangeRateService exchangeRateService;

    private final Map<String, String> CURRENCIES = Map.of(
            "USD", "United States Dollar",
            "EUR", "Euro",
            "GBP", "British Pound",
            "JPY", "Japanese yen",
            "NGN", "Nigerian Naira",
            "CAD", "Canadian dollar",
            "INR", "Indian Rupee"
    );

    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        long accountNumber;
        try {
            validateAccountNonExistsForUser(accountDto.getCode(), user.getUid());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        do{
            accountNumber = new RandomUtil().generateRandom(10);
        } while(accountRepository.existsByAccountNumber(accountNumber));

        var account = Account.builder()
                .accountNumber(accountNumber)
                .accountName(user.getFirstname() + " " + user.getLastname())
                .balance(1000)
                .owner(user)
                .code(accountDto.getCode())
                .symbol(accountDto.getSymbol())
                .label(CURRENCIES.get(accountDto.getCode()))
                .build();
        return accountRepository.save(account);
    }


    public Transaction performTransfer(Account senderAccount, Account recipientAccount, double amount, User user) throws Exception {
        validateSufficientFunds(senderAccount, (amount * 1.01));
        senderAccount.setBalance(senderAccount.getBalance() - amount * 1.01);
        recipientAccount.setBalance(recipientAccount.getBalance() + amount);
        accountRepository.saveAll(List.of(senderAccount, recipientAccount));
        var senderTransaction = Transaction.builder()
                .account(senderAccount)
                .status(Status.COMPLETED)
                .type(Type.WITHDRAWAL)
                .amount(amount)
                .txFee(amount * 0.01)
                .owner(senderAccount.getOwner())
                .build();

        var recipientTransaction = Transaction.builder()
                .account(recipientAccount)
                .status(Status.COMPLETED)
                .type(Type.DEPOSIT)
                .owner(recipientAccount.getOwner())
                .amount(amount)
                .build();
        return transactionRepository.saveAll(List.of(senderTransaction, recipientTransaction)).get(0);
    }

    public void validateAccountNonExistsForUser(String code, String uid) throws Exception {
        if (accountRepository.existsByCodeAndOwnerUid(code, uid)) {
            throw new Exception("Account of this type already exists for this user");
        }
    }

    public void validateAccountOwner(Account account, User user) throws OperationNotSupportedException {
        if (!account.getOwner().getUid().equals(user.getUid())) {
            throw new OperationNotSupportedException("Account does not belong to this user");
        }
    }

    public void validateSufficientFunds(Account account, double amount) throws Exception {
        if (account.getBalance() < amount) {
            throw new Exception("Insufficient funds");
        }
    }

    public void validateAmount(double amount) throws Exception {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    public void validateDifferentCurrencyType(ConvertDto convertDto) throws Exception {
        if (convertDto.getToCurrency().equals(convertDto.getFromCurrency())) {
            throw new IllegalArgumentException("Conversion between same currency is not allowed");
        }
    }

    public void validateAccountOwnership(ConvertDto convertDto, String uid) throws Exception {
        accountRepository.findByCodeAndOwnerUid(convertDto.getFromCurrency(), uid).orElseThrow();
        accountRepository.findByCodeAndOwnerUid(convertDto.getToCurrency(), uid).orElseThrow();

    }

    public void validateConversion(ConvertDto convertDto, String uid) throws Exception {
        validateAccountOwnership(convertDto, uid);
        validateDifferentCurrencyType(convertDto);
        validateAmount(convertDto.getAmount());
        validateSufficientFunds(accountRepository.findByCodeAndOwnerUid(convertDto.getFromCurrency(), uid).orElseThrow(), convertDto.getAmount());
    }

    public Transaction convertCurrency(ConvertDto convertDto, User user) throws Exception {
        validateConversion(convertDto, user.getUid());
        var rates = exchangeRateService.getRates();
        var sendingRates = rates.get(convertDto.getFromCurrency());
        var receivingRates = rates.get(convertDto.getToCurrency());
        var computedAmount = convertDto.getAmount() * (sendingRates / receivingRates);
        var senderAccount = accountRepository.
                findByCodeAndOwnerUid(convertDto.getFromCurrency(), user.getUid()).
                orElseThrow();
        var recipientAccount = accountRepository.
                findByCodeAndOwnerUid(convertDto.getToCurrency(), user.getUid()).
                orElseThrow();
        senderAccount.setBalance(senderAccount.getBalance() - convertDto.getAmount() * 1.01);
        recipientAccount.setBalance(recipientAccount.getBalance() + computedAmount);
        accountRepository.saveAll(List.of(senderAccount, recipientAccount));

        var transaction = Transaction.builder()
                .account(senderAccount)
                .status(Status.COMPLETED)
                .type(Type.CONVERSION)
                .amount(convertDto.getAmount())
                .txFee(convertDto.getAmount() * 0.01)
                .owner(senderAccount.getOwner())
                .build();

        return transactionRepository.save(transaction);
    }


}
