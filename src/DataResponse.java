import java.util.concurrent.BlockingQueue;

public class DataResponse {
    private String key;
    private Object value;
    private final BlockingQueue<DataResponse> data;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public BlockingQueue<DataResponse> getDataResponses() {
        return data;
    }

    public DataResponse(String key, Object value, BlockingQueue<DataResponse> data) {
        this.key = key;
        this.value = value;
        this.data = data;
    }

    public DataResponse(String key, Object value) {
        this.key = key;
        this.value = value;
        this.data = null;
    }
}
