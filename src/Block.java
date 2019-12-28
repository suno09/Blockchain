import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Block {
    private long id;
    private long idMiner;
    private int vcMiner;
    private long timestamp; // milliseconds
    private long timeGen; // seconds
    private long magicNumber;
    private String hash;
    private String prevHash;
    private String msgN;
    private String data;
    private List<Transaction> transactions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdMiner() {
        return idMiner;
    }

    public void setIdMiner(long idMiner) {
        this.idMiner = idMiner;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimeGen() {
        return timeGen;
    }

    public void setTimeGen(long timeGen) {
        this.timeGen = timeGen;
    }

    public long getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(long magicNumber) {
        this.magicNumber = magicNumber;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public String getMsgN() {
        return msgN;
    }

    public void setMsgN(String msgN) {
        this.msgN = msgN;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getVcMiner() {
        return vcMiner;
    }

    public void setVcMiner(int vcMiner) {
        this.vcMiner = vcMiner;
    }

    public Block(long id, String prevHash, List<Transaction> transactions, int vcMiner) {
        this.id = id;
        this.idMiner = 0;
        this.timestamp = new Date().getTime();
        this.timeGen = 0;
        this.vcMiner = vcMiner;
        this.magicNumber = 0;
        this.hash = "";
        this.prevHash = prevHash;
        this.msgN = "";
        if (!transactions.isEmpty()) {
            this.data = "\n" + transactions
                    .stream()
                    .map(Transaction::toString)
                    .collect(Collectors.joining("\n"));
        } else {
            this.data = "no transactions";
        }
    }

    public Block(long id, String prevHash, List<Transaction> transactions) {
        this.id = id;
        this.idMiner = 0;
        this.timestamp = new Date().getTime();
        this.timeGen = 0;
        this.vcMiner = 0;
        this.magicNumber = 0;
        this.hash = "";
        this.prevHash = prevHash;
        this.msgN = "";
        if (!transactions.isEmpty()) {
            this.data = "\n" + transactions
                    .stream()
                    .map(Transaction::toString)
                    .collect(Collectors.joining("\n"));
        } else {
            this.data = "no transactions";
        }
        this.transactions = new ArrayList<>(transactions);
    }

    public Block(Block block) {
        this.id = block.id;
        this.idMiner = block.idMiner;
        this.timestamp = block.timestamp;
        this.timeGen = block.timeGen;
        this.magicNumber = block.magicNumber;
        this.hash = block.hash;
        this.prevHash = block.prevHash;
        this.msgN = block.msgN;
        this.data = block.data;
        this.vcMiner = block.vcMiner;
        this.transactions = block.transactions;
    }

    public void computeTimeGen() {
        timeGen = (new Date().getTime() - timestamp) / 1000L;
    }

    @Override
    public String toString() {
        return String.format(
                "Block:\n" +
                        "Created by: miner%d\n" +
                        "miner%d gets %d VC\n" +
                        "Id: %d\n" +
                        "Timestamp: %d\n" +
                        "Magic number: %d\n" +
                        "Hash of the previous block: \n" +
                        "%s\n" +
                        "Hash of the block: \n" +
                        "%s\n" +
                        "Block data: %s\n" +
                        "Block was generating for %d seconds\n" +
                        "%s\n",
                idMiner, idMiner, vcMiner, id, timestamp, magicNumber, prevHash, hash, data, timeGen, msgN
        );
    }
}
