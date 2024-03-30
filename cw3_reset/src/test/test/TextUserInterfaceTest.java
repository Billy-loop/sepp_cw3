
import org.junit.jupiter.api.*;
import view.TextUserInterface;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class TextUserInterfaceTest {
    private static final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private static TextUserInterface textUserInterface;


    @BeforeEach
    public void setUp(){
        System.setOut(new PrintStream(content)); // Redirect System.out to outContent
        textUserInterface = new TextUserInterface(System.in);
    }

    @Test
    public void displayInfoTest(String info){
        textUserInterface.displayInfo(info);

    }



}
