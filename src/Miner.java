import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Miner extends User implements Runnable {
    private int idMiner;
    private List<Block> blockchain;
    private Block block;
    private String pattern;

    public int getIdMiner() {
        return idMiner;
    }

    public void setIdMiner(int idMiner) {
        this.idMiner = idMiner;
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(List<Block> blockchain) {
        this.blockchain = blockchain;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Miner(int idUser, int idMiner, int vc, String pattern, List<Block> blockchain,
                 final BlockingQueue<DataResponse> q_blockchain) throws NoSuchAlgorithmException {
        super(idUser, "miner" + idMiner, true, vc, q_blockchain);
        this.idMiner = idMiner;
        this.pattern = pattern;
        this.blockchain = new ArrayList<>(blockchain);
    }

    @Override
    public void run() {
        // if block is valid
//        System.out.println(getUserName() + " => " + block.getData());
//        long d = System.currentTimeMillis();
        if (Main.blockIsValid(block, blockchain)) {
            String validHash = "";
            while (!validHash.matches(pattern)) {
                block.setMagicNumber((long) new Random().nextInt() + (1L << 31));
                validHash = StringUtil.applySha256(
                        block.getId() + block.getData() + block.getMagicNumber());
            }

            // successful validhash
            block.setHash(validHash);
            block.setIdMiner(idMiner);
            block.computeTimeGen();
            getQueue().offer(new DataResponse("new_block", block));
        } else {
            getQueue().offer(new DataResponse("error_block", block));
        }
//        System.err.println("miner" + idMiner + " => " + ((System.currentTimeMillis() - d) / 1000.));
//        return null;
    }
}
