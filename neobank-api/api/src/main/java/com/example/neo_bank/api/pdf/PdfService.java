package com.example.neo_bank.api.pdf;

import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.Transaction;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.repository.TransactionRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public PdfService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public byte[] generateStatement(Long accountId) {

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Extracto Bancario - NeoBank", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Titular: " + account.getUser().getName()));
            document.add(new Paragraph("IBAN: " + account.getIban()));
            document.add(new Paragraph("Saldo Actual: " + account.getBalance() + " €"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            addTableHeader(table, "Fecha");
            addTableHeader(table, "Tipo");
            addTableHeader(table, "Cantidad");

            for (Transaction t : transactions) {
                table.addCell(t.getTimestamp().toString().substring(0, 10));
                table.addCell(t.getType().toString());
                table.addCell(t.getAmount() + " €");
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando el PDF", e);
        }
        return out.toByteArray();
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setPhrase(new Phrase(headerTitle));
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}
