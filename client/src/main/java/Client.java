import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    private String authToken = null;

    public void runClient() {
        ClientCommunicator clientCommunicator = new ClientCommunicator();
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.println("Welcome to 240 chess!");
        out.print(clientCommunicator.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt(out);
            String line = scanner.nextLine();

            try {
                result = clientCommunicator.eval(line);
                out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                out.print(msg);
            }
        }
        out.println();
    }

    private void printPrompt(PrintStream out) {
        out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
