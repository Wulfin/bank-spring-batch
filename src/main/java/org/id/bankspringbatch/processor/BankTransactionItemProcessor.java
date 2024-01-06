package org.id.bankspringbatch.processor;

import org.id.bankspringbatch.dao.BankTransaction;
import org.springframework.batch.item.ItemProcessor;

import java.text.SimpleDateFormat;

public class BankTransactionItemProcessor implements ItemProcessor<BankTransaction,BankTransaction> {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public BankTransaction process(BankTransaction item) throws Exception {
        String strTransactionDate = item.getStrTransactionDate();
        item.setTransactionDate(dateFormat.parse(strTransactionDate));

        return item;
    }
}
