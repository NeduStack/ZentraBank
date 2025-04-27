package fizy.web.app.service;

import fizy.web.app.entity.*;
import fizy.web.app.repository.AccountRepository;
import fizy.web.app.repository.CardRepository;
import fizy.web.app.repository.TransactionRepository;
import fizy.web.app.service.helper.AccountHelper;
import fizy.web.app.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final AccountHelper accountHelper;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Card getCard(User user) {
        return cardRepository.findByOwnerUid(user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Card does not exist"));
    }

   public Card createCard(double amount, User user) throws Exception {
        if (amount < 2) {
            throw new IllegalArgumentException("Minimum amount to create a card is 2");
        }
        if (!accountRepository.existsByCodeAndOwnerUid("USD", user.getUid())) {
            throw new UnsupportedOperationException("USD account not found for this user so card cannot be created");
        }
        var usdAccount = accountRepository.findByCodeAndOwnerUid("USD", user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency does not exist"));
        accountHelper.validateSufficientFunds(usdAccount, amount);
        usdAccount.setBalance(usdAccount.getBalance() - amount);
        // Generate a random 16-digit card number
        Long cardNumber = generateRandomCardNumber();

        // Check if card number already exists
        while (cardRepository.existsByCardNumber(cardNumber)) {
            cardNumber = generateRandomCardNumber();
        }

        // Generate CVV and PIN
        String cvv = String.format("%03d", new java.util.Random().nextInt(1000));
        String pin = String.format("%04d", new java.util.Random().nextInt(10000));

        // Set issue date and expiration date (3 years from now)
        LocalDateTime issueDate = LocalDateTime.now();
        LocalDateTime expirationDate = issueDate.plusYears(3);

        // Create card entity
        Card card = Card.builder()
            .cardNumber(cardNumber)
            .cardHolder(user.getFirstname() + " " + user.getLastname())
            .balance(amount - 1) // Deduct 1 for card creation
            .iss(issueDate)
            .exp(expirationDate)
            .cvv(cvv)
            //.pin(pin)
            //.billingAddress(user.getAddress())
            .owner(user)
            .build();

        //accountRepository.save(usdAccount);
        card = cardRepository.save(card);

        //Create a transaction for the card creation
       accountHelper.createAccountTransaction(1, Type.WITHDRAWAL, 0.00, user, usdAccount);
       accountHelper.createAccountTransaction(amount-1, Type.WITHDRAWAL, 0.00, user, usdAccount);
       createCardTransaction(amount, Type.WITHDRAWAL, 0.00, user, card);
       accountRepository.save(usdAccount);

        return card;
    }

    private Long generateRandomCardNumber() {
        // Generate a 16-digit random card number
        return new RandomUtil().generateRandom(16);
    }

    public Transaction creditCard(double amount, User user) throws Exception {
        var usdAccount = accountRepository.findByCodeAndOwnerUid("USD", user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency does not exist"));
        usdAccount.setBalance(usdAccount.getBalance() - amount);
        accountHelper.createAccountTransaction(amount, Type.WITHDRAWAL, 0.00, user, usdAccount);
        var card = user.getCard();
        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);
        return createCardTransaction(amount, Type.CREDIT, 0.00, user, card);

    }

    public Transaction debitCard(double amount, User user) throws Exception {
        var usdAccount = accountRepository.findByCodeAndOwnerUid("USD", user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency does not exist"));
        usdAccount.setBalance(usdAccount.getBalance() + amount);
        accountHelper.createAccountTransaction(amount, Type.DEPOSIT, 0.00, user, usdAccount);
        var card = user.getCard();
        card.setBalance(card.getBalance() - amount);
        cardRepository.save(card);
        return createCardTransaction(amount, Type.DEBIT, 0.00, user, card);
    }

    public Transaction createCardTransaction(double amount, Type type, double txFee, User user, Card card) throws Exception {
        var transaction = Transaction.builder()
                //.account(card)
                .card(card)
                .status(Status.COMPLETED)
                .type(type)
                .amount(amount)
                .txFee(txFee)
                .owner(user)
                .build();
        return transactionRepository.save(transaction);
    }

    //public Transaction creditCard(double amount, User user) {
    //}
}
