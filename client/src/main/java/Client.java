import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    private ServerFacade serverFacade;

    public Client(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.println("Welcome to 240 chess!");
        out.print(SET_TEXT_COLOR_BLUE);
        out.print(serverFacade.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt(out);
            String line = scanner.nextLine();

            try {
                result = serverFacade.eval(line);
                if (!Objects.equals(result, "quit")) {
                    out.print(SET_TEXT_COLOR_BLUE + result);
                }
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
