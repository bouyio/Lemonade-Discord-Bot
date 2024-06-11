package bouyio.bot.log;

import java.util.HashMap;

public class LoggingServer {
    public boolean logsToChannel = false;
    public boolean logsToFile = false;

    public static HashMap<String, LoggingServer> loggingServerHashMap = new HashMap<>();
}
