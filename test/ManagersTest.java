import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {

    @Test
    void managerNotNull() {
        Assertions.assertNotNull(Managers.getDefault(), "Manager is null");
    }

    @Test
    void historyManagerNotNull() {
        Assertions.assertNotNull(Managers.getDefaultHistory(), "History manager is null");
    }

}
