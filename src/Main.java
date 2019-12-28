import java.security.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    private static int NBR_THREAD_POOL = 10;
    private static BlockChain blockChain;
    private static List<Miner> miners;
    private static List<User> users;
    private static long idTransaction = 1;
    private static long idBlock = 1;

    private static BlockingQueue<DataResponse> q_main;
    private static BlockingQueue<DataResponse> q_blockchain;

    public static void main(String[] args) {
        // initialize
        initialize_blockchain();
        initialize_users();
        while (blockChain.getBlockchain().size() < 15) {
            q_main.clear();
            List<Transaction> transactions;
            if (blockChain.getBlockchain().size() > 0) {
                transactions = getTransactions();
            } else {
                transactions = Collections.emptyList();
            }
            Block block = new Block(
                    blockChain.getBlockchain().size() + 1,
                    blockChain.getPrevHash(),
                    transactions);
            executeMiners(block);
            try {
                int nbrTest = 0;
                while (nbrTest < NBR_THREAD_POOL) {
                    DataResponse dataResponse = q_main.take();
                    if (dataResponse.getKey().equals("success_block")) {
                        Block newBlock = (Block) dataResponse.getValue();
                        ArrayList<Block> newBlockChain = new ArrayList<>(blockChain.getBlockchain());
                        newBlockChain.add(newBlock);
                        blockChain.setBlockchain(newBlockChain);
                        System.out.println(newBlock);
                        break;
                    }
                    nbrTest++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // if finished
        q_blockchain.offer(new DataResponse("exit_blockchain", "", null));
    }

    private static void executeMiners(Block block) {
        for (int i=0; i<miners.size(); i++) {
            Miner miner = miners.get(i);
            miner.setVc(100);
            miner.setPattern(blockChain.getPattern());
            Block blck = new Block(block);
            blck.setIdMiner(miner.getIdMiner());
            blck.setVcMiner(miner.getVc());
            miner.setBlock(blck);
        }
        // start miners to work
        ExecutorService executorService = Executors.newFixedThreadPool(NBR_THREAD_POOL);
        for (int i=0; i<miners.size(); i++) {
            executorService.execute(miners.get(i));
        }
        awaitTerminationAfterShutdown(executorService);
    }

    private static void initialize_blockchain() {
        q_main = new LinkedBlockingQueue<>();
        q_blockchain = new LinkedBlockingQueue<>();
        blockChain = new BlockChain(q_blockchain, q_main);
        blockChain.start();
    }

    private static List<Transaction> getTransactions() {
        Random random = new Random();
        List<Transaction> transactions = new ArrayList<>();
        for (int iTransaction=0;iTransaction < 5;iTransaction++,idTransaction++) {
            try {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(1024);
                KeyPair keyPair = kpg.genKeyPair();

                int indexSender;
                do {
                    indexSender = random.ints(0, users.size()).findFirst().getAsInt();
                } while (getUserCoins(users.get(indexSender).getIdUser(), blockChain.getBlockchain()) <= 0);
                int indexReceiver;
                // choose receiver
                do {
                    indexReceiver = random.ints(0, users.size()).findFirst().getAsInt();
                } while (indexReceiver == indexSender);
                // generate transaction
                User userSender = users.get(indexSender);
                User userReceiver = users.get(indexReceiver);
                int vc = random.ints(1,
                        getUserCoins(userSender.getIdUser(), blockChain.getBlockchain()) + 1)
                        .findFirst().getAsInt();
                Transaction transaction = new Transaction(
                        idTransaction,
                        -1,
                        userSender.getIdUser(),
                        userSender.getUserName(),
                        userReceiver.getIdUser(),
                        userReceiver.getUserName(),
                        vc,
                        keyPair.getPublic()
                );
                Signature rsa = Signature.getInstance("SHA1withRSA");
                rsa.initSign(keyPair.getPrivate());
                rsa.update(transaction.getContent().getBytes());
                transaction.setSignature(rsa.sign());
                transactions.add(transaction);
            } catch (Exception e) {
//                e.printStackTrace();
//                System.err.println("Error Transaction");
                iTransaction--;
                idTransaction--;
            }
        }

        return transactions;
    }

    private static void initialize_users() {
        // create miner users
        miners = new ArrayList<>();
        users = new ArrayList<>();
        for (int idUser=1; idUser <= NBR_THREAD_POOL; idUser++) {
            try {
                Miner miner = new Miner(
                        idUser,
                        idUser,
                        0,
                        blockChain.getPattern(),
                        blockChain.getBlockchain(),
                        blockChain.getQueue());
                users.add(miner);
                miners.add(miner);
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Couldn't create miner");
                idUser--;
            }
        }
        // create normal users
        for (int idUser=1; idUser <= 10; idUser++) {
            try {
                users.add(new User(
                        idUser + NBR_THREAD_POOL,
                        "user" + idUser,
                        false,
                        0,
                        new LinkedBlockingQueue<>()));
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Couldn't create user");
                idUser--;
            }
        }
    }

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static boolean blockIsValid(Block block, List<Block> blocks) {
        List<Block> blocksTest = new ArrayList<>(blocks);
        blocksTest.add(block);
        boolean blockNotSecure = block
                .getTransactions()
                .stream()
                .anyMatch(transaction -> {
                    try {
                        Signature rsa = Signature.getInstance("SHA1withRSA");
                        rsa.initVerify(transaction.getPublicKey());
                        rsa.update(transaction.getContent().getBytes());

                        return !rsa.verify(transaction.getSignature());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return true;
                    }
                });

        return !blockNotSecure && users
                .stream()
                .allMatch(user -> getUserCoins(user.getIdUser(), blocksTest) > 0);
    }

    public static int getUserCoins(long idUser, List<Block> blocks) {
        return blocks
                .stream()
                .mapToInt(block -> block
                        .getTransactions()
                        .stream()
                        .mapToInt(tr -> {
                            if (tr.getIdSender() == idUser) {
                                return -tr.getVc();
                            } else if (tr.getIdReceiver() == idUser) {
                                return tr.getVc();
                            } else {
                                return 0;
                            }
                        }).sum()).sum() + 100;
    }
}
