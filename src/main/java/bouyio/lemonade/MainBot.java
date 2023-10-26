package bouyio.lemonade;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.Random;


public class MainBot extends ListenerAdapter{

    // All of these variables are for the logging functionality
    public static boolean loggingToChannel = true;
    public static boolean loggingToFile = true;
    public static PrintStream outputToFile;

    // All if these variables are for the alphabet game channel
    public static String[] alphabet = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    public static int currentLetter = 0;
    public static String currentPlayer = "";
    public static String lastPlayer = "";



    public static void main(String[] args){

        // Initialization of the file that messages are going be logged
        try {
            Random random = new Random();
            outputToFile = new PrintStream("log" + random.nextInt(0, 1000) + ".txt");
            outputToFile.println("loaded");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // These lines are connecting and initialize the bot and its features (slash commands, listeners etc.)
        JDA jda = JDABuilder.createDefault("Insert Token")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT) // enables explicit access to message.getContentDisplay()
                .build();

        jda.addEventListener(new MainBot());

        jda.updateCommands().addCommands(
                Commands.slash("log", "Turn on or off logging.")
                        .addOption(OptionType.BOOLEAN, "log_to_channel", "Whether messages should be logged to the log channel.")
                        .addOption(OptionType.BOOLEAN, "log_to_file", "Whether messages should be logged to a file.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS))

        ).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event){


        if(!event.getAuthor().isBot()){

            // These lines handle the alphabet game
            if(event.getChannel().getName().equals("alphabet")){
                TextChannel textChannel = event.getGuild().getTextChannelsByName("alphabet", true).get(0);
                currentPlayer = Objects.requireNonNull(event.getMember()).getEffectiveName();

                // These lines handle what happens when the players reach the end
                if(currentLetter == 26){
                    textChannel.sendMessage("Congrats. You reached the end. Start again at a.").queue();
                    currentLetter = 0;
                }
                else {

                    /*
                       These lines handle what happens when a player gets a letter right
                       and the else case handles what happens when they don't lol
                     */
                    if (event.getMessage().getContentDisplay().toLowerCase().equals(alphabet[currentLetter]) && !currentPlayer.equals(lastPlayer)) {
                        textChannel.sendMessage("Nice").queue();
                        currentLetter++;
                        lastPlayer = currentPlayer;
                    } else {
                        youRuinedIt(event);
                    }
                }
            }
            else {

                /*
                   These lines begin the logging process.
                   If they throw an exception try disabling logging to a file
                   (if you don't want to deal with my code)
                 */
                try {
                    log(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    // This function handles what happens when the players loose
    public static void youRuinedIt(MessageReceivedEvent event){

        // These lines send the message to communicate that the players have lost
        TextChannel textChannel = event.getGuild().getTextChannelsByName("alphabet", true).get(0);
        String message = Objects.requireNonNull(event.getMember()).getAsMention() + ". You ruined it! Start from the letter a again.";
        textChannel.sendMessage(message).queue();

        // These lines initialize the variables, so the game can begin again
        currentLetter = 0;
        lastPlayer = "";
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        // These lines turn logging on or off base on user's request
        if(event.getName().equals("log")){
            loggingToChannel = Objects.requireNonNull(event.getOption("log_to_channel")).getAsBoolean();
            loggingToFile = Objects.requireNonNull(event.getOption("log_to_file")).getAsBoolean();
        }
    }

    /*
        This function handles the logging part of the bot.
        Note: messages printed to the standard output do not necessarily get stored
     */
    public static void log(MessageReceivedEvent event) throws IOException {

        // This line gets and formats message to be logged
        String currentMessage = "[" + java.time.ZonedDateTime.now() + "] " + "USER " + Objects.requireNonNull(event.getMember()).getEffectiveName() + " MESSAGED " + event.getMessage().getContentDisplay() + " AT THE CHANNEL " + event.getChannel().getName();

        if(loggingToChannel){

            // These lines handle what happens if a channel is private (those messages are not stored)
            if (event.isFromType(ChannelType.PRIVATE)) {
                System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                        event.getMessage().getContentDisplay());
            } else {

                System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                        event.getChannel().getName(), Objects.requireNonNull(event.getMember()).getEffectiveName(),
                        event.getMessage().getContentDisplay());

                // These lines send the logged messages to the log channel
                TextChannel textChannel = event.getGuild().getTextChannelsByName("log", true).get(0);
                textChannel.sendMessage(currentMessage).queue();
            }
        }else if(loggingToFile){

            // These lines handle what happens if a channel is private (those messages are not stored)
            if (event.isFromType(ChannelType.PRIVATE)) {
                System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                        event.getMessage().getContentDisplay());
            } else {

                // These lines store the messages to a file
                System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                        event.getChannel().getName(), Objects.requireNonNull(event.getMember()).getEffectiveName(),
                        event.getMessage().getContentDisplay());
                outputToFile.println(currentMessage);
            }
        }
    }
}