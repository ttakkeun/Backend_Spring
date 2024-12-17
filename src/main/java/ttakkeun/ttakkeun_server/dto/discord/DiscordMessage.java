package ttakkeun.ttakkeun_server.dto.discord;

public record DiscordMessage(
        String content
) {
    public static DiscordMessage createDiscordMessage(String message) {
        return new DiscordMessage(message);
    }
}
