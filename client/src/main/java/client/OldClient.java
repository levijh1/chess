//package client;
//
//import java.io.PrintStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Objects;
//import java.util.Scanner;
//
//import static ui.EscapeSequences.*;
//
//public class OldClient {
//    private Client serverFacade;
//
//    public OldClient(int port) {
//        serverFacade = new Client(port);
//    }
//
//    public void run() {
//        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//        out.print(SET_BG_COLOR_BLACK);
//        out.println("Welcome to 240 chess!");
//        out.print(SET_TEXT_COLOR_BLUE);
//        serverFacade.help();
//
//        Scanner scanner = new Scanner(System.in);
//        var result = "";
//        while (!result.equals("quit")) {
//            printPrompt(out);
//            String line = scanner.nextLine();
//
//            try {
//                out.print(SET_TEXT_COLOR_BLUE);
//                result = serverFacade.eval(line);
//                if (result == null) {
//                    result = "";
//                }
//                if (!Objects.equals(result, "quit")) {
//                    if (!line.equals("help")) {
//                        out.print("\n");
//                        serverFacade.help();
//                    }
//                }
//            } catch (Throwable e) {
//                var msg = e.toString();
//                out.print(msg);
//            }
//        }
//        out.println();
//    }
//
//    private void printPrompt(PrintStream out) {
//        out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
//    }
//}
