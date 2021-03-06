import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ButtonFactoryTest {
  private Button[] buttonsList = new Button[36];

  @Test
  void createSingleButton() {
    Button blackButton = ButtonFactory.createButton("black", 10);
    assertNotNull(blackButton);
  }

  @Test
  void createMultipleButtons() {
    for(int i=0;i<10;i++) {
      buttonsList[i] = ButtonFactory.createButton("black", i);
      assertNotNull(buttonsList[i]);
    }
  }


}