package bouyio.bot.alphabet;

import java.util.HashMap;

public class GameServer {
    public String lastPlayerName = "";
    public int currentLetter = 0;

    public static HashMap<String, GameServer> gameServerHashMap = new HashMap<>();
}
