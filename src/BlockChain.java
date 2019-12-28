import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class BlockChain extends Thread {
    private List<Block> blockchain;
    private Block block;
    private String prevHash;
    private String pattern;
    private int N;
    private List<String> messages;
    private final BlockingQueue<DataResponse> queue;
    private final BlockingQueue<DataResponse> q_main;

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(List<Block> blockchain) {
        this.blockchain = new ArrayList<>(blockchain);
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public List<String> getMessages() {
        return messages;
    }

    public BlockingQueue<DataResponse> getQueue() {
        return queue;
    }

    public BlockingQueue<DataResponse> getQ_main() {
        return q_main;
    }

    public BlockChain(BlockingQueue<DataResponse> queue, BlockingQueue<DataResponse> q_main) {
        this.queue = queue;
        this.q_main = q_main;
        this.blockchain = Collections.emptyList();
        N = 0;
        prevHash = "0";
        pattern = "^[^0].+$";
        messages = new ArrayList<>(Collections.singletonList("no transactions"));
    }

    public boolean addBlock(Block block) {
        if (prevHash.equals(block.getPrevHash()) && block.getHash().matches(pattern) &&
                Main.blockIsValid(block, blockchain))
        {
            if (block.getTimeGen() < 5) {
                N++;
                block.setMsgN("N was increased to " + N);
            } else if (block.getTimeGen() < 60) {
                block.setMsgN("N stays the same");
            } else {
                N--;
                block.setMsgN("N was decreased by 1");
            }

            // decrease time execution
            if (N > 4)
                N = 1;

            //blockchain.add(new Block(block));
            if (N > 0) {
                pattern = "^0{" + N + "}[^0].+$";
            } else {
                N = 0;
                pattern = "^[^0].+$";
            }

            prevHash = block.getHash();
            this.block = new Block(block);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {
        boolean exit = false;
        while (!exit) {
            try {
                DataResponse data = queue.take();
                switch (data.getKey()) {
                    case "new_block":
                        Block block = (Block) data.getValue();
                        if(addBlock(block)) {
                            q_main.offer(new DataResponse("success_block", block));
                        } else {
                            q_main.offer(new DataResponse("error_block", null));
                        }
                        break;
                    case "error_block":
                        q_main.offer(new DataResponse("error_block", null));
                        break;
                    case "exit_blockchain":
                        exit = true;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
