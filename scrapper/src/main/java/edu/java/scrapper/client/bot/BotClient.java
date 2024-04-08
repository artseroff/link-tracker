package edu.java.scrapper.client.bot;

import edu.java.client.ClientConfigRecord;
import edu.java.client.ServiceClient;
import edu.java.general.ApiException;
import edu.java.request.LinkUpdateRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class BotClient extends ServiceClient {

    public BotClient(ClientConfigRecord client) {
        super(client);
    }

    public void updates(LinkUpdateRequest request) {
        this.webClient
            .post()
            .uri("/update")
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), LinkUpdateRequest.class)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(ApiException.class)
            )
            .bodyToMono(Void.class)
            .retryWhen(retry)
            .block();
    }
}
