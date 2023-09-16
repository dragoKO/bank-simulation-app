package com.cydeo.service.impl;

import com.cydeo.enums.AccountType;
import com.cydeo.exeption.AccountOwnershipException;
import com.cydeo.exeption.BadRequestException;
import com.cydeo.exeption.BalanceNotSufficientException;
import com.cydeo.model.Account;
import com.cydeo.model.Transaction;
import com.cydeo.repository.AccountRepository;
import com.cydeo.repository.TransactionRepository;
import com.cydeo.service.TransactionService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TransactionServiceImpl implements TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction makeTransfer(Account sender, Account receiver, BigDecimal amount, Date creationDate, String message) {
        validateAccount(sender, receiver);
        checkAccountOwnership(sender, receiver);
        executeBalanceAndUpdateIfRequired(amount,sender,receiver);
        Transaction transaction=Transaction.builder().amount(amount).sender(sender.getId()).receiver(receiver.getId()).createDate(creationDate).message(message).build();
        transactionRepository.save(transaction);

        return null;
    }

    private void executeBalanceAndUpdateIfRequired(BigDecimal amount, Account sender, Account receiver) {
        if(checkSenderBalance(sender,amount)){
            //update sender and receiver balance
            //100 - 80
            sender.setBalance(sender.getBalance().subtract(amount));
            //50 + 80
            receiver.setBalance(receiver.getBalance().add(amount));
        }else{
            throw new BalanceNotSufficientException("Balance is not enough for this transfer");
        }

    }

    private boolean checkSenderBalance(Account sender, BigDecimal amount) {
        //verify sender has enough balance to send
        return sender.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) >=0;

    }

    private void checkAccountOwnership(Account sender, Account receiver) {
        if (
                (sender.getAccountType().equals(AccountType.SAVING) || receiver.getAccountType().equals(AccountType.SAVING))
                        && !sender.getUserId().equals(receiver.getUserId())
        ) {

            throw new AccountOwnershipException("If one of the account is saving, user must be the same for sender and receiver");
        }


    }

    @Override
    public List<Transaction> findAllTransaction() {
        return transactionRepository.findAll();
    }

    private void validateAccount(Account sender, Account receiver) {
        if (sender == null || receiver == null) throw new BadRequestException("User or sender can not be null");

        if (sender.getId().equals(receiver.getId()))
            throw new BadRequestException("Sender account needs to be different than receiver account");

        findAccountById(sender.getId());
        findAccountById(receiver.getId());


    }

    private void findAccountById(UUID id) {
        accountRepository.findById(id);
    }

}
