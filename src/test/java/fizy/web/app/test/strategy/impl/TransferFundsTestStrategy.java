package fizy.web.app.test.strategy.impl;

import fizy.web.app.dto.TransferDto;
import fizy.web.app.entity.Account;
import fizy.web.app.entity.Transaction;
import fizy.web.app.entity.User;
import fizy.web.app.repository.AccountRepository;
import fizy.web.app.service.helper.AccountHelper;
import fizy.web.app.service.operation.impl.TransferFundsOperation;
import fizy.web.app.test.strategy.AccountOperationTestStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class TransferFundsTestStrategy implements AccountOperationTestStrategy {
    private static final Logger logger = LoggerFactory.getLogger(TransferFundsTestStrategy.class);
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountHelper accountHelper;

    private TransferFundsOperation operation;
    private User testUser;
    private Account senderAccount;
    private Account recipientAccount;
    private TransferDto transferDto;
    private Transaction expectedTransaction;

    @Override
    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        operation = new TransferFundsOperation(accountRepository, accountHelper);

        // Create test data
        testUser = new User();
        testUser.setUid("test-user-123");

        senderAccount = new Account();
        senderAccount.setAccountId("sender-acc-id");
        senderAccount.setCode("USD");
        senderAccount.setBalance(1000.0);

        recipientAccount = new Account();
        recipientAccount.setAccountId("recipient-acc-id");
        recipientAccount.setAccountNumber(12345678L);
        recipientAccount.setBalance(500.0);

        transferDto = new TransferDto();
        transferDto.setCode("USD");
        transferDto.setAmount(100.0);
        transferDto.setRecipientAccountNumber(12345678L);

        expectedTransaction = new Transaction();
        expectedTransaction.setAmount(100.0);
    }

    public void testSuccessfulExecution() {
        logger.info("Starting successful transfer test");
        logger.info("Test executing with account: {}", senderAccount.getAccountId());



        // Set up mocks
        when(accountRepository.findByCodeAndOwnerUid(eq("USD"), eq("test-user-123")))
                .thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber(eq(12345678L)))
                .thenReturn(Optional.of(recipientAccount));

        try {
            when(accountHelper.performTransfer(eq(senderAccount), eq(recipientAccount), eq(100.0), eq(testUser)))
                    .thenReturn(expectedTransaction);

            Transaction result = operation.execute(transferDto, testUser);

            assertNotNull(result);
            assertEquals(expectedTransaction, result);
        } catch (Exception e) {
            logger.error("Test failed with exception", e);
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Override
    public void testErrorHandling() {
        // Test sender account not found
        when(accountRepository.findByCodeAndOwnerUid(anyString(), anyString()))
                .thenReturn(Optional.empty());

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    try {
                        operation.execute(transferDto, testUser);
                    } catch (Exception e) {
                        if (e instanceof UnsupportedOperationException) {
                            throw (UnsupportedOperationException) e;
                        }
                        fail("Unexpected exception: " + e.getMessage());
                    }
                }
        );

        assertEquals("Account of type currency does not exist", exception.getMessage());
    }
}