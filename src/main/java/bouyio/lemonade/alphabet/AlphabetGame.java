package bouyio.bot.alphabet;

import bouyio.bot.MainBot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


import java.util.Objects;


public class AlphabetGame {

    public static String[] alphabet = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    public static void youRuinedIt(MessageReceivedEvent event, GameServer server){

        // These lines send the message to communicate that the players have lost
        TextChannel textChannel = event.getGuild().getTextChannelsByName("alphabet", true).get(0);
        event.getMessage().addReaction(Emoji.fromUnicode("U+274C")).queue();
        String message = Objects.requireNonNull(event.getMember()).getAsMention() + ". You ruined it! Start from the letter a again.";
        textChannel.sendMessage(message).queue();

        // These lines initialize the variables, so the game can begin again
        server.currentLetter = 0;
        server.lastPlayerName = "";
    }

    public static void mainAlphabetGame(MessageReceivedEvent event, GameServer server) {
        TextChannel textChannel = event.getGuild().getTextChannelsByName("alphabet", true).get(0);

        String currentPlayerName = Objects.requireNonNull(event.getMember()).getEffectiveName();

        if (!checkForFail(server, event, currentPlayerName)){
                event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
                server.currentLetter++;
                server.lastPlayerName = currentPlayerName;
        } else youRuinedIt(event, server);

        if (server.currentLetter >= 26){
            textChannel.sendMessage("Congratulation you have reached the end! \nYou may start from letter a again.").queue();
            server.currentLetter = 0;
            server.lastPlayerName = "";
        }




    }

    public static boolean checkForFail(GameServer server, MessageReceivedEvent event, String currentPlayerName){
        if (server.lastPlayerName.equals("")) return !event.getMessage().getContentDisplay().equalsIgnoreCase("a");

        return server.lastPlayerName.equals(currentPlayerName) && !(MainBot.letters.get(event.getMessage().getContentDisplay()) == server.currentLetter + 1);
    }
}
