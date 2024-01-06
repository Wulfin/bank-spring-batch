package org.id.bankspringbatch;

import lombok.Getter;
import org.id.bankspringbatch.dao.BankTransaction;
import org.springframework.batch.item.ItemProcessor;

@Getter
public class BankTransactionItemAnalyticsProcessor implements ItemProcessor<BankTransaction,BankTransaction> {
    private double totalDebit;
    private double totalCredit;
    @Override
    public BankTransaction process(BankTransaction item) throws Exception {
        if (item.getTransactionType().equals("D")) totalDebit+= item.getAmount();
        else if (item.getTransactionType().equals("C")) totalCredit+= item.getAmount();

        return item;
    }
}
