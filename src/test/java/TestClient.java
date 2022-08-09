import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestClient {
    private Client client = new Client();
    @Test
    public void test_GetPort(){
        int result = 11111;
        Assertions.assertEquals(client.getPort(), result);
    }
}
