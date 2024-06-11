package bouyio.bot.log;

import bouyio.bot.log.LoggingServer;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Objects;
import java.time.ZonedDateTime;

public class Log {

    public static PrintStream outputToFile;
    //public static boolean loggingToChannel = true;
    //public static boolean loggingToFile = true;


    static {

        Date time = new Date();
        try {
            outputToFile = new PrintStream("log" + time.getTime() + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("loaded");
        outputToFile.println("loaded");


    }


    public static void log(MessageReceivedEvent event, LoggingServer server) throws IOException {



        String formatedTime = String.format("%d/%d/%d %d:%d:%d", ZonedDateTime.now().getDayOfMonth(), ZonedDateTime.now().getMonth().getValue(), ZonedDateTime.now().getYear(), ZonedDateTime.now().getHour(), ZonedDateTime.now().getMinute(), ZonedDateTime.now().getSecond());
        String currentMessage = String.format("[%s][%s][%s] %s: %s", formatedTime, event.getGuild().getName(), event.getChannel().getName(), Objects.requireNonNull(event.getMember()).getEffectiveName(), event.getMessage().getContentDisplay());

        if(server.logsToChannel){

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
        } if(server.logsToFile){

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
                System.out.println("ok so it logged");
            }
        }else {
            outputToFile.close();
        }
    }
}
