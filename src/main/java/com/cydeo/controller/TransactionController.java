package com.cydeo.controller;

import com.cydeo.model.Account;
import com.cydeo.model.Transaction;
import com.cydeo.service.AccountService;
import com.cydeo.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Date;
import java.util.UUID;

@Controller
public class TransactionController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public TransactionController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }


    @GetMapping("/make-transfer")
    public String getMakeTransfer(Model model){

        //what we need to provide to make transfer happen
            //we need to provide empty transaction object
                model.addAttribute("transaction", Transaction.builder().build());
            //we need to provide list of all accounts
                model.addAttribute("accounts",accountService.listAllAccount());
            //we need list of last 10 transactions to fill the table(business logic is missing)
                model.addAttribute("lastTransactions",transactionService.last10Transactions());
        return "transaction/make-transfer";
    }

    //write a post method that takes transaction object from the UI
    //complete the transfer and return the same page

    @PostMapping("/transfer")
    public String makeTransfer(@ModelAttribute("transaction") @Valid Transaction transaction, BindingResult bindingResult,Model model){

        if(bindingResult.hasErrors()){
            model.addAttribute("accounts",accountService.listAllAccount());
            model.addAttribute("lastTransactions",transactionService.last10Transactions());
            return "transaction/make-transfer";
        }

        //I have UUID of  accounts but I need to provide Account object.
        //I need to find the Accounts based on the ID that I have and use as a parameter to complete makeTransfer method.
        Account sender = accountService.retrieveById(transaction.getSender());
        Account receiver = accountService.retrieveById(transaction.getReceiver());
        transactionService.makeTransfer(sender,receiver,transaction.getAmount(),new Date(),transaction.getMessage());

        return "redirect:/make-transfer";
    }

    //TASK 5 MIN
    //write a method, that gets the account id from index.html and print on the console.
    //(work on index.html and here)
    //transaction/{id}
    //return transaction/transactions page
    @GetMapping("/transaction/{id}")
    public String getTransactionList(@PathVariable("id")UUID id,Model model){
        //print the id
        System.out.println(id);

        //get the list of transactions based on id and return as a model attribute
        //TASK- complete the method(service and repository)
        //findTransactionListById
        model.addAttribute("transactions",transactionService.findTransactionListById(id));

        return "transaction/transactions";
    }

    //go to transactions.html
    //based on size of the transactions either show "No transactions yet" or transactions table

}
