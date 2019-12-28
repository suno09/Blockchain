import java.security.PublicKey;

public class Transaction {
    private long idTransaction;
    private long idMiner;
    private long idSender;
    private String nameSender;
    private long idReceiver;
    private String nameReceiver;
    private int vc;
    private PublicKey publicKey;
    private String content;
    private byte[] signature;

    public long getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(long idTransaction) {
        this.idTransaction = idTransaction;
    }

    public long getIdMiner() {
        return idMiner;
    }

    public void setIdMiner(long idMiner) {
        this.idMiner = idMiner;
    }

    public long getIdSender() {
        return idSender;
    }

    public void setIdSender(long idSender) {
        this.idSender = idSender;
    }

    public String getNameSender() {
        return nameSender;
    }

    public void setNameSender(String nameSender) {
        this.nameSender = nameSender;
    }

    public long getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(long idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getNameReceiver() {
        return nameReceiver;
    }

    public void setNameReceiver(String nameReceiver) {
        this.nameReceiver = nameReceiver;
    }

    public int getVc() {
        return vc;
    }

    public void setVc(int vc) {
        this.vc = vc;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public Transaction(long idTransaction, long idMiner, long idSender, String nameSender,
                       long idReceiver, String nameReceiver, int vc,
                       PublicKey publicKey) {
        this.idTransaction = idTransaction;
        this.idMiner = idMiner;
        this.idSender = idSender;
        this.nameSender = nameSender;
        this.idReceiver = idReceiver;
        this.nameReceiver = nameReceiver;
        this.vc = vc;
        this.content = "Transaction{" +
                "idTransaction=" + idTransaction +
                ", idMiner=" + idMiner +
                ", idSender=" + idSender +
                ", nameSender='" + nameSender + '\'' +
                ", idReceiver=" + idReceiver +
                ", nameReceiver='" + nameReceiver + '\'' +
                ", vc=" + vc +
                '}';
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return String.format("%s sent %d VC to %s", nameSender, vc, nameReceiver);
    }
}
