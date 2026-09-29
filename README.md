# Space Mining — Java Advanced (Exercício 06)

Frota de robôs mineradores em um asteroide, controlada por microsserviços resilientes.

```
POST /command                RestTemplate            RabbitMQ              banco
 cliente ───► command-service ───────────► validator-service ───► fila ───► mining-service ───► {LEFT:10, ...}
              (8080)   ▲                      (8081)  50% falha    mining.commands   (8082)
                       └──────── Eureka Server (8761) ◄─ todos se registram ─┘
```

| Serviço | Porta | O que faz |
|---|---|---|
| eureka-server | 8761 | Registro/descoberta dos serviços |
| command-service | 8080 | `POST /command` recebe `{"command": "LEFT"}` e chama o validator com `RestTemplate` (via Eureka, `@LoadBalanced`) |
| validator-service | 8081 | Valida `RIGHT, LEFT, FRONT, BACK, OPEN, CLOSE`; simula **50% de falha**; em falha faz **retry com exponential backoff** (500ms, 1s, 2s, 4s; 5 tentativas); em sucesso publica no RabbitMQ |
| mining-service | 8082 | Consome a fila, executa o comando no robô (log) e grava a contagem por comando no banco (H2). `GET /commands` mostra `{"LEFT": 10, "FRONT": 40, ...}` |

## Requisitos do PDF x onde está implementado

| Requisito | Implementação |
|---|---|
| Eureka Server + registro dos serviços | `eureka-server`, `eureka.client` nos outros 3 |
| CommandService recebe `POST /command` e envia por RestTemplate | `CommandController`, `CommandService`, `RestTemplateConfig` |
| Validação dos 6 comandos | `CommandType`, `ValidationService` (inválido = HTTP 400, sem retry) |
| Falha simulada de 50% | `RandomFailureSimulator` (`validator.failure-rate`) |
| Retry com exponential backoff | `RetryConfig` (`RetryTemplate` com `exponentialBackoff`) |
| Sucesso publica no RabbitMQ | `CommandPublisher`, fila `mining.commands` |
| MiningService consome a fila, loga o comando | `CommandConsumer`, `MiningService` |
| Contagem gravada no banco | `CommandCount` + `CommandCountRepository` (JPA/H2) |
| Robôs não sobrecarregados (backpressure) | `prefetch: 1` + `concurrency: 1` no consumer; limite de comandos simultâneos no `command-service` (HTTP 429) |
| Sem conflito entre ordens | um único consumidor processa 1 ordem por vez, contagem atualizada em transação |
| Falhas de comunicação tratadas automaticamente | retry/backoff no validator; erros 503 tratados nos handlers |

## Como rodar

Pré-requisitos: JDK 17+, Maven 3.9+ e Docker.

```bash
# 1) RabbitMQ
docker compose up -d

# 2) Compilar tudo (roda os testes unitários)
mvn clean package

# 3) Subir na ordem, cada um em um terminal
java -jar eureka-server/target/eureka-server-1.0.0.jar
java -jar validator-service/target/validator-service-1.0.0.jar
java -jar mining-service/target/mining-service-1.0.0.jar
java -jar command-service/target/command-service-1.0.0.jar
```

Aguarde ~30s após subir tudo: é o tempo do Eureka propagar o registro. Confira em http://localhost:8761 os 3 serviços (COMMAND-SERVICE, VALIDATOR-SERVICE, MINING-SERVICE).

## Testando

```bash
./test-commands.sh
```

ou manualmente:

```bash
curl -X POST http://localhost:8080/command -H "Content-Type: application/json" -d '{"command": "LEFT"}'
curl http://localhost:8082/commands
```

Respostas: `202` aceito, `400` comando inválido, `429` excesso de comandos em voo, `503` validação falhou após todas as tentativas ou serviço indisponível.

## O que mostrar na demonstração

1. Eureka com os 3 serviços registrados.
2. Logs do validator: `Falha simulada ...` e novas tentativas em intervalos crescentes (0,5s → 1s → 2s → 4s).
3. Log do mining-service: `Robô executando comando: LEFT`.
4. `GET http://localhost:8082/commands` com a contagem; console H2 em http://localhost:8082/h2-console (JDBC URL `jdbc:h2:mem:mining`, user `sa`, sem senha).
5. Painel do RabbitMQ (http://localhost:15672, guest/guest) com a fila `mining.commands`.
6. A rajada do `test-commands.sh`: o robô processa uma ordem por vez.
