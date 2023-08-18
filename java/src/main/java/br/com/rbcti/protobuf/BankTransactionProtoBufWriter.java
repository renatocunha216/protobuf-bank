package br.com.rbcti.protobuf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.protobuf.ByteString;

import br.com.rbcti.protobuf.artifacts.Bank;
import br.com.rbcti.protobuf.artifacts.BankAccount;
import br.com.rbcti.protobuf.artifacts.BankTransaction;
import br.com.rbcti.protobuf.artifacts.DateTime;
import br.com.rbcti.protobuf.artifacts.FundTransfer;
import br.com.rbcti.protobuf.artifacts.Status;


/**
 * Example of using Protocol Buffers creating bank transactions.<br>
 *
 * Create value transfer transactions with random values and store
 * the data in a binary file.
 *
 * @author Renato Cunha
 * @version 1.0 28/07/2023
 *
 */
public class BankTransactionProtoBufWriter {

    public static boolean DEBUG = false;
    public static boolean CHECK = true;

    // Control fields for transaction generation
    private int TOTAL_TRANSACTION;
    private LocalDateTime baseLocalDateTime = LocalDateTime.of(2023, 1, 1, 0, 0, 0);

    private float[] randomFeeAmount;
    private double[] randomAmountTransfert;
    private byte[][] randomAuthenticationCode;

    private int bankIndex = 0;
    private int checkDigit = 0;

    private List<br.com.rbcti.model.Bank> banks;
    private Bank[] banksBuffer;

    private BankTransaction.Builder bankTransactionBuilder;
    private BankTransaction bankTransaction;

    public BankTransactionProtoBufWriter() {
        this(5);
    }

    public BankTransactionProtoBufWriter(int totalTransaction) {

        TOTAL_TRANSACTION = totalTransaction;

        this.banks = new ArrayList<>();
        banks.add(new br.com.rbcti.model.Bank(9313, "ABN AMRO S.A."));
        banks.add(new br.com.rbcti.model.Bank(1, "Banco do Brasil"));
        banks.add(new br.com.rbcti.model.Bank(4041, "Banrisul"));
        banks.add(new br.com.rbcti.model.Bank(5237, "Bradesco"));
        banks.add(new br.com.rbcti.model.Bank(745, "Citibank"));
        banks.add(new br.com.rbcti.model.Bank(4048, "Itaú"));
        banks.add(new br.com.rbcti.model.Bank(7376, "J.P. Morgan S.A."));
        banks.add(new br.com.rbcti.model.Bank(260, "Nubank"));
        banks.add(new br.com.rbcti.model.Bank(3008, "Santander"));

        banksBuffer = new Bank[banks.size()];

        randomFeeAmount = new float[TOTAL_TRANSACTION];
        randomAmountTransfert = new double[TOTAL_TRANSACTION];
        randomAuthenticationCode = new byte[TOTAL_TRANSACTION][16];

        fillRandomFeeAmount();
        fillRandomAmountTransfer();
        fillRandomAuthenticationCode();

        bankTransactionBuilder = BankTransaction.newBuilder();

        bankIndex = 0;
    }

    public void buildBankBuffer() {
        int i = 0;
        for (br.com.rbcti.model.Bank bank : banks) {
        	banksBuffer[i++] = Bank.newBuilder().setBankCode(bank.getCode().intValue()).setName(bank.getName()).build();
        }
    }

    public void createBankTransaction() {

        for (int c = 0; c < TOTAL_TRANSACTION; c++) {

            LocalDateTime dateTime = getNextLocalDateTime(c);
            int day = dateTime.getDayOfMonth();
            int month = dateTime.getMonthValue();
            int year = dateTime.getYear();
            int hour = dateTime.getHour();
            int minute = dateTime.getMinute();
            int seconds = dateTime.getSecond();

			DateTime dateTimeBuffer = DateTime.newBuilder().setDay(day).setMonth(month).setYear(year).setHour(hour)
					.setMinute(minute).setSecond(seconds).build();

        	FundTransfer.Builder fundTransferBuilder = FundTransfer.newBuilder();
        	fundTransferBuilder.setTransferDate(dateTimeBuffer);
        	fundTransferBuilder.setSourceBank(banksBuffer[getNextBankIndex()]);
        	fundTransferBuilder.setSourceBankAccount(BankAccount.newBuilder().setAccountNumber(c).setCheckDigit(getNextCheckDigit()).build());
        	fundTransferBuilder.setDestinationBank(banksBuffer[getNextBankIndex()]);
        	fundTransferBuilder.setDestinationBankAccount(BankAccount.newBuilder().setAccountNumber(c + 1).setCheckDigit(getNextCheckDigit()).build());
        	fundTransferBuilder.setFeeAmount(randomFeeAmount[c]);
        	fundTransferBuilder.setAmountTransfer(randomAmountTransfert[c]);
        	fundTransferBuilder.setStatus(br.com.rbcti.protobuf.artifacts.Status.CONFIRMED);
        	fundTransferBuilder.setAuthenticationCode(ByteString.copyFrom(randomAuthenticationCode[c]));

        	FundTransfer fundTransfer = fundTransferBuilder.build();

        	this.bankTransactionBuilder.addTransactions(fundTransfer);
        }
    }

    private BankTransaction buildBankTransaction() {
    	this.bankTransaction = this.bankTransactionBuilder.build();
    	return this.bankTransaction;
    }

    private void fillRandomFeeAmount() {
        for (int c = 0; c < TOTAL_TRANSACTION; c++) {
            randomFeeAmount[c] = (float) randomDouble(100);
        }
    }

    private void fillRandomAmountTransfer() {
        for (int c = 0; c < TOTAL_TRANSACTION; c++) {
            randomAmountTransfert[c] = randomDouble(1000000);
        }
    }

    private void fillRandomAuthenticationCode() {
        SecureRandom random = new SecureRandom();
        for (int c = 0; c < TOTAL_TRANSACTION; c++) {
            random.nextBytes(randomAuthenticationCode[c]);
        }
    }

    private static double round(double value, int places) {
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    private static double randomDouble(int limit) {
        Random rand = new Random();
        double value = rand.nextDouble() * limit;
        return round(value, 2);
    }

    private LocalDateTime getNextLocalDateTime(int sum) {
        return baseLocalDateTime.plusDays(sum).plusMinutes(sum).plusSeconds(sum);
    }

    private void resetControlIndex() {
        bankIndex = 0;
        checkDigit = 0;
    }

    private int getNextBankIndex() {
        if (bankIndex >= banks.size()) {
            bankIndex = 0;
        }
        return bankIndex++;
    }

    private int getNextCheckDigit() {
        if (checkDigit > 9) {
            checkDigit = 0;
        }
        return checkDigit++;
    }

    public void saveToFile() {

    	byte[] transactionsData = this.bankTransaction.toByteArray();

        System.out.println("Total bytes: " + transactionsData.length);

        Path path = Path.of(System.getProperty("user.home"), "bankTransactionProtoBuf.bin");
        //Path path = Path.of("F:\\", "bankTransaction.bin");

        try {
            Files.write(path, transactionsData);

            System.out.println(path.toString() + " file was successfully created.");
            System.out.println("File lenght             : " +  String.format("%,d", path.toFile().length()) + " bytes");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readAndCheck() {

        resetControlIndex();

        Path path = Path.of(System.getProperty("user.home"), "bankTransactionProtoBuf.bin");
        //Path path = Path.of("F:\\", "bankTransaction.bin");

        File transactionsFile = path.toFile();

        if (!transactionsFile.exists()) {
            System.err.println("File not found.");
            return;
        }

        try {
            byte[] transactionsData = Files.readAllBytes(path);

            BankTransaction bankTransactions = BankTransaction.parseFrom(transactionsData);

			if (bankTransactions.getTransactionsCount() < 1) {
				System.err.println("No data found.");
				return;
			}

			int c = 0;

            for (FundTransfer fundTransfer : bankTransactions.getTransactionsList()) {

                DateTime transferDate = fundTransfer.getTransferDate();
                Bank sourceBank = fundTransfer.getSourceBank();
                BankAccount sourceBankAccount = fundTransfer.getSourceBankAccount();
                Bank destinationBank = fundTransfer.getDestinationBank();
                BankAccount destinationBankAccount = fundTransfer.getDestinationBankAccount();
                float feeAmount = fundTransfer.getFeeAmount();
                double amountTransfer = fundTransfer.getAmountTransfer();
                Status transferStatus = fundTransfer.getStatus();
                byte[] authenticationCode = fundTransfer.getAuthenticationCode().toByteArray();

                if (!CHECK) {
                    continue;
                }

                LocalDateTime dateTimeRef = getNextLocalDateTime(c);

                if ((transferDate.getDay() != dateTimeRef.getDayOfMonth()) ||
                        (transferDate.getMonth() != dateTimeRef.getMonthValue()) ||
                        (transferDate.getYear() != dateTimeRef.getYear()) ||
                        (transferDate.getHour() != dateTimeRef.getHour()) ||
                        (transferDate.getMinute() != dateTimeRef.getMinute()) ||
                        (transferDate.getSecond() != dateTimeRef.getSecond())) {
                    throw new Exception("Transaction " + c + ". Transfer date validation error.");
                }

                br.com.rbcti.model.Bank sourceBankRef = this.banks.get(getNextBankIndex());
                br.com.rbcti.model.Bank destinationBankRef = this.banks.get(getNextBankIndex());

                if (!sourceBank.getName().equals(sourceBankRef.getName()) || sourceBank.getBankCode() != sourceBankRef.getCode().intValue()) {
                    throw new Exception("Transaction " + c + ". Source Bank validation error.");
                }

                if ((sourceBankAccount.getAccountNumber() != c) || (sourceBankAccount.getCheckDigit() != getNextCheckDigit())) {
                    throw new Exception("Transaction " + c + ". Source Bank Account validation error.");
                }

                if (!destinationBank.getName().equals(destinationBankRef.getName()) || destinationBank.getBankCode() != destinationBankRef.getCode().intValue()) {
                    throw new Exception("Transaction " + c + ". Destination Bank validation error.");
                }

                if ((destinationBankAccount.getAccountNumber() != (c + 1)) || (destinationBankAccount.getCheckDigit() != getNextCheckDigit())) {
                    throw new Exception("Transaction " + c + ". Source Bank Account validation error.");
                }

                if (feeAmount != this.randomFeeAmount[c]) {
                    throw new Exception("Transaction " + c + ". Fee amount validation error.");
                }

                if (amountTransfer != this.randomAmountTransfert[c]) {
                    throw new Exception("Transaction " + c + ". Amount transfer validation error.");
                }

                if (Status.CONFIRMED != transferStatus) {
                    throw new Exception("Transaction " + c + ". Transfer status validation error.");
                }

                if (!Arrays.equals(authenticationCode, randomAuthenticationCode[c])) {
                    throw new Exception("Transaction " + c + ". Authentication code validation error.");
                }

                if (DEBUG) {
                    System.out.println("Transaction " + c + ". Check OK!");
                }

                c++;
            }


        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        DEBUG = false;
        CHECK = false;

        final int TOTAL = 1_000_000;
        // final int TOTAL = 100;

        System.out.println("Start test.");
        System.out.println("Check data        : " + CHECK);
        System.out.println("Total transactions: " + String.format("%,d", TOTAL));

        BankTransactionProtoBufWriter writer = new BankTransactionProtoBufWriter(TOTAL);

        long startTime1 = System.currentTimeMillis();
        writer.buildBankBuffer();
        writer.createBankTransaction();
        writer.buildBankTransaction();
        writer.saveToFile();
        long endTime1 = System.currentTimeMillis();

        long startTime2 = System.currentTimeMillis();
        writer.readAndCheck();
        long endTime2 = System.currentTimeMillis();

        System.out.println("Buffer build time       : " + (endTime1 - startTime1) + " ms");

        if (CHECK) {
            System.out.println("Read time and check time: " + (endTime2 - startTime2) + " ms");
        } else {
            System.out.println("Reading time            : " + (endTime2 - startTime2) + " ms");
        }

        System.out.println("End test.");
    }

}
