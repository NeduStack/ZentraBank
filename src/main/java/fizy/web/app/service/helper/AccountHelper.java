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
            "NGN", "Nigerian Naira",
            "CAD", "Canadian dollar"
    );


    public Account createAccount(AccountDto accountDto, User user) throws AccountAlreadyExistsForCurrencyException {
        // 1. Validate no duplicate account exists for this user
        if (accountRepository.existsByCodeAndOwnerUid(accountDto.getCode(), user.getUid())) {
            throw new AccountAlreadyExistsForCurrencyException(
                    "User " + user.getUid() + " already has a " + accountDto.getCode() + " account"
            );
        }

        // 2. Generate unique account number
        long accountNumber;
        do {
            accountNumber = new RandomUtil().generateRandom(10);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        // 3. Build and save new account
        return accountRepository.save(
                Account.builder()
                        .accountNumber(accountNumber)
                        .accountName(user.getFirstname() + " " + user.getLastname())
                        .balance(10000000) // Initial balance
                        .owner(user)
                        .code(accountDto.getCode())
                        .symbol(accountDto.getSymbol())
                        .label(CURRENCIES.get(accountDto.getCode()))
                        .build()
        );
    }


    public Transaction performTransfer(Account senderAccount, Account recipientAccount, double amount, User user) throws Exception {
        validateSufficientFunds(senderAccount, (amount * 1.01));
        senderAccount.setBalance(senderAccount.getBalance() - amount * 1.01);
        recipientAccount.setBalance(recipientAccount.getBalance() + amount);
        accountRepository.saveAll(List.of(senderAccount, recipientAccount));
        var senderTransaction = createAccountTransaction(amount, Type.WITHDRAWAL, amount * 0.01, user, senderAccount);
        var receiverTransaction = createAccountTransaction(amount, Type.DEPOSIT, amount * 0.00, recipientAccount.getOwner(), recipientAccount);

        return senderTransaction;
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



    // In an appropriate package, e.g., com.yourproject.exception
    public class AccountAlreadyExistsForCurrencyException extends RuntimeException { // Or extends Exception
        public AccountAlreadyExistsForCurrencyException(String message) {
            super(message);
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

        var fromAccountTransaction = createAccountTransaction(convertDto.getAmount(), Type.CONVERSION, convertDto.getAmount() * 0.01, user, senderAccount);
        var toAccounTransaction = createAccountTransaction(computedAmount, Type.DEPOSIT, convertDto.getAmount() * 0.00, user, recipientAccount);
        
        return fromAccountTransaction;
    }

    public Transaction createAccountTransaction(double amount, Type type, double txFee, User user, Account account) throws Exception {
        //validateSufficientFunds(account, amount);
        //account.setBalance(account.getBalance() - amount * 1.01);
        //accountRepository.save(account);
        var transaction = Transaction.builder()
                .account(account)
                .status(Status.COMPLETED)
                .type(type)
                .amount(amount)
                .txFee(txFee)
                .owner(user)
                .build();
        return transactionRepository.save(transaction);
    }
}
