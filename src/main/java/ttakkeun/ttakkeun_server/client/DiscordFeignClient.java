package ttakkeun.ttakkeun_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ttakkeun.ttakkeun_server.dto.discord.DiscordMessage;

@FeignClient(name = "discord-client", url = "${discord.webhook.url}")
public interface DiscordFeignClient {
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    void sendMessage(@RequestBody DiscordMessage discordMessage);
}
