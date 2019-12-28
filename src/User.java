import java.security.*;
import java.util.concurrent.BlockingQueue;

public class User extends Thread{
    private long idUser;
    private String userName;
    private boolean miner;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private int vc;
    private final BlockingQueue<DataResponse> queue;


    public User(int idUser, String userName, boolean miner, int vc,
                BlockingQueue<DataResponse> queue) throws NoSuchAlgorithmException {
        this.idUser = idUser;
        this.userName = userName;
        this.miner = miner;
        this.vc = vc;
        this.queue = queue;
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair keyPair = kpg.genKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isMiner() {
        return miner;
    }

    public void setMiner(boolean miner) {
        this.miner = miner;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public int getVc() {
        return vc;
    }

    public void setVc(int vc) {
        this.vc = vc;
    }

    public BlockingQueue<DataResponse> getQueue() {
        return queue;
    }

    @Override
    public void run() {

    }
}
