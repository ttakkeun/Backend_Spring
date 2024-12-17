package ttakkeun.ttakkeun_server.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.DiscordHandler;
import ttakkeun.ttakkeun_server.dto.discord.DiscordMessage;
import ttakkeun.ttakkeun_server.entity.enums.EventMessage;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.INVALID_DISCORD_MESSAGE;
import static ttakkeun.ttakkeun_server.dto.discord.DiscordMessage.createDiscordMessage;

@RequiredArgsConstructor
@Component
public class DiscordMessageProvider {
    private final DiscordFeignClient discordFeignClient;

    public void sendMessage(String message) {
        DiscordMessage discordMessage = createDiscordMessage(message);
        sendMessageToDiscord(discordMessage);
    }

    private void sendMessageToDiscord(DiscordMessage discordMessage) {
        try {
            discordFeignClient.sendMessage(discordMessage);
        } catch (FeignException e) {
            throw new DiscordHandler(INVALID_DISCORD_MESSAGE);
        }
    }
}
