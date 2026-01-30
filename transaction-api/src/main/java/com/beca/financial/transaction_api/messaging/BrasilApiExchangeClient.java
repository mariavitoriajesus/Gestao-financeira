package com.beca.financial.transaction_api.messaging;

import com.beca.financial.transaction_api.dto.BrasilApiQuoteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
public class BrasilApiExchangeClient {

    private static final Logger log = LoggerFactory.getLogger(BrasilApiExchangeClient.class);

    private static final String BASE_URL = "https://brasilapi.com.br/api/cambio/v1";
    private static final int MAX_BACKOFF_DAYS = 7;

    private final RestClient restClient;

    public BrasilApiExchangeClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public BigDecimal getExchangeRate(String currency, LocalDate quoteDate) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency is required");
        }

        String cur = currency.trim().toUpperCase();
        LocalDate attemptDate = (quoteDate != null) ? quoteDate : LocalDate.now().minusDays(1);
        attemptDate = lastBusinessDay(attemptDate);

        for (int i = 0; i < MAX_BACKOFF_DAYS; i++) {
            try {
                log.info("Consultando BrasilAPI: moeda={} data={}", cur, attemptDate);

                BrasilApiQuoteResponse resp = restClient.get()
                        .uri(BASE_URL + "/cotacao/{currency}/{date}", cur, attemptDate.toString())
                        .retrieve()
                        .body(BrasilApiQuoteResponse.class);

                if (resp != null && resp.cotacoes() != null && !resp.cotacoes().isEmpty()) {
                    BigDecimal rate = resp.cotacoes().stream()
                            .filter(c -> c.tipoBoletim() != null && c.tipoBoletim().equalsIgnoreCase("FECHAMENTO PTAX"))
                            .findFirst()
                            .orElse(resp.cotacoes().get(resp.cotacoes().size() - 1))
                            .cotacaoVenda();

                    if (rate != null && rate.compareTo(BigDecimal.ZERO) > 0) {
                        return rate;
                    }
                }

                log.warn("Sem cotacaoVenda (moeda={} data={}). Tentando dia anterior.", cur, attemptDate);
                attemptDate = previousBusinessDay(attemptDate);

            } catch (HttpClientErrorException e) {
                HttpStatus status = (HttpStatus) e.getStatusCode();
                String body = e.getResponseBodyAsString();

                log.warn("Erro BrasilAPI (status={} moeda={} data={}) body={}", status, cur, attemptDate, body);

                if (status == HttpStatus.BAD_REQUEST && body != null && body.contains("NO_TODAY_DATE")) {
                    attemptDate = previousBusinessDay(attemptDate);
                    continue;
                }
                if (status.is4xxClientError()) {
                    attemptDate = previousBusinessDay(attemptDate);
                    continue;
                }
                throw new IllegalStateException("BrasilAPI error: " + status, e);

            } catch (ResourceAccessException e) {
                throw new IllegalStateException("Connection failed when querying BrasilAPI.", e);
            }
        }

        throw new IllegalArgumentException("Quote not found for " + cur);
    }

    private LocalDate previousBusinessDay(LocalDate d) {
        return lastBusinessDay(d.minusDays(1));
    }

    private LocalDate lastBusinessDay(LocalDate d) {
        if (d.getDayOfWeek() == DayOfWeek.SATURDAY) return d.minusDays(1);
        if (d.getDayOfWeek() == DayOfWeek.SUNDAY) return d.minusDays(2);
        return d;
    }
}
