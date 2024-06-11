package bouyio.bot;

import bouyio.bot.alphabet.AlphabetGame;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.HashMap;


public class MainBot {

    public static HashMap<String, Integer> letters = new HashMap<>();

    public static void main(String[] args){
        for (int i = 0; i < AlphabetGame.alphabet.length; i++){
            letters.put(AlphabetGame.alphabet[i], i);
        }

        JDA jda = JDABuilder.createDefault("Insert api key here")
                        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                        .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("Team scraps"))
                                .build();

        jda.addEventListener(new MessageListener());


        jda.updateCommands().addCommands(
                Commands.slash("getlogconfig", "Returns current logging configurations.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
                Commands.slash("log", "Turn on or off logging.")
                        .addOption(OptionType.BOOLEAN, "log_to_channel", "Whether messages should be logged to the log channel.")
                        .addOption(OptionType.BOOLEAN, "log_to_file", "Whether messages should be logged to a file.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS))


        ).queue();



    }
}
