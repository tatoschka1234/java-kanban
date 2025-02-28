import tasks.Epic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicsShouldBeEqualIfTheirIDsEqual() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic1", "Epic descr");
        epic2.setId(1);
        assertEquals(epic1, epic2, "Эпики не равны");
    }

}