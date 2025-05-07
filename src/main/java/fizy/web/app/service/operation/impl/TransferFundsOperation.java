package fizy.web.app.service.operation.impl;

import fizy.web.app.dto.TransferDto;
import fizy.web.app.entity.Transaction;
import fizy.web.app.entity.User;
import fizy.web.app.repository.AccountRepository;
import fizy.web.app.service.helper.AccountHelper;
import fizy.web.app.service.operation.AccountOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferFundsOperation implements AccountOperation<TransferDto, Transaction> {
    private final AccountRepository accountRepository;
    private final AccountHelper accountHelper;

    @Override
    public Transaction execute(TransferDto request, User user) throws Exception {
        var senderAccount = accountRepository.findByCodeAndOwnerUid(request.getCode(), user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency does not exist"));
        var recipientAccount = accountRepository.findByAccountNumber(request.getRecipientAccountNumber())
                .orElseThrow(() -> new UnsupportedOperationException("Recipient account does not exist"));
        return accountHelper.performTransfer(senderAccount, recipientAccount, request.getAmount(), user);
    }
}