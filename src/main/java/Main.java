
import com.github.cliftonlabs.json_simple.JsonException;
import services.Service;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, JsonException {
        Service service = new Service();
        service.console();
    }
}
