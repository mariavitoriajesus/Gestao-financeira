# Projeto Final – API de Gestão Financeira 

Este projeto implementa uma **API de gestão financeira** baseada em **microserviços**, utilizando **Java 17**, **Spring Boot**, **Kafka**, **PostgreSQL**, **Flyway**, **JWT** e integração com **API pública de câmbio (BrasilAPI)**. O objetivo é simular um **cenário bancário real**, com foco em arquitetura, segurança, mensageria e relatórios.

---

## 🧱 Arquitetura Geral

O sistema é composto por **3 microserviços**, cada um com responsabilidade bem definida:

### 🔹 MS1 – User Service (porta 8081)

Responsável por **usuários, autenticação e dados auxiliares do cliente**.

Principais responsabilidades:

* CRUD de usuários
* Autenticação via **JWT**
* Controle de **roles (USER / ADMIN)**
* Importação de usuários via **Excel (Apache POI)**
* Download de template Excel
* **Mock API de saldo bancário** (para simulação)
* Versionamento de banco com **Flyway**

### 🔹 MS2 – Transaction API (porta 8082)

Responsável por **transações financeiras e relatórios**.

Principais responsabilidades:

* Criação de transações financeiras
* Status inicial `PENDING`
* Integração com **Kafka (Producer)**
* Implementação de **transações de CÂMBIO**
* Consumo de **BrasilAPI** para cotação de moedas
* Persistência de dados de câmbio (`exchange_transactions`)
* Relatórios financeiros:

    * Resumo por período
    * Resumo por tipo
    * Exportação em **Excel**
    * Exportação em **PDF**

### 🔹 MS3 – Transaction Processor (porta 8083)

Responsável pelo **processamento assíncrono das transações**.

Principais responsabilidades:

* Consumo de eventos Kafka
* Processamento e decisão da transação
* Atualização de status (`APPROVED` / `FAILED`)
* Implementação de **DLQ (Dead Letter Queue)**

---

## 🔐 Segurança

* Autenticação via **JWT**
* Filtro customizado (`JwtAuthFilter`)
* Controle de acesso por **roles**:

    * `USER`: operações comuns
    * `ADMIN`: importação Excel
* Endpoints públicos:

    * Swagger
    * Login
    * Mock de saldo

---

## 🔁 Mensageria (Kafka)

Fluxo de eventos:

1. MS2 cria transação e publica evento `transaction.requested`
2. MS3 consome o evento
3. MS3 processa e chama MS2 para atualizar status
4. Em caso de falha:

    * Retry automático
    * Envio para **DLQ** (`transaction.requested.DLT`)

---

## 💱 Transações de Câmbio

* Tipo de transação: `CAMBIO`
* Integração com **BrasilAPI**
* Uso de **RestClient**
* Persistência separada dos dados de câmbio
* Tratamento de limitações da API (ex: cotação do dia atual)

---

## 📊 Relatórios

### Endpoints disponíveis:

#### Resumo por tipo

```
GET /reports/by-type?from=YYYY-MM-DD&to=YYYY-MM-DD
```

#### Resumo geral

```
GET /reports/summary?from=YYYY-MM-DD&to=YYYY-MM-DD
```

#### Exportação Excel

```
GET /reports/by-type/excel?from=YYYY-MM-DD&to=YYYY-MM-DD
```

#### Exportação PDF (Summary + By Type)

```
GET /reports/summary/pdf?from=YYYY-MM-DD&to=YYYY-MM-DD
```

---

## 💳 Mock API de Saldo Bancário

Endpoint para simulação de saldo do cliente:

```
GET /mock/balance/{userId}
```

* Saldo determinístico (baseado no UUID)
* Retorna saldo atual, disponível, limite e status
* Endpoint liberado para facilitar testes

---

## 🗄️ Banco de Dados e Migrations

* Banco: **PostgreSQL**
* Versionamento com **Flyway**

### Principais tabelas:

* `users`
* `transactions`
* `exchange_transactions`

---

## 🐳 Docker

O projeto pode ser executado via **Docker Compose**.

### Subir o ambiente:

```bash
docker compose up --build
```

Serviços expostos:

* User Service: `http://localhost:8081`
* Transaction API: `http://localhost:8082`
* Transaction Processor: `http://localhost:8083`

---

## 🧪 Testes

* Testes manuais via Swagger
* Fluxo completo demonstrável:

    1. Criar usuário
    2. Login
    3. Mock de saldo
    4. Criar transação
    5. Processar via Kafka
    6. Consultar relatório


---

## 📘 Swagger

Cada microserviço expõe documentação via Swagger:

* MS1: `http://localhost:8081/swagger-ui`
* MS2: `http://localhost:8082/swagger-ui`

---

## ✅ Conclusão

Este projeto demonstra:

* Arquitetura de microserviços
* Segurança com JWT e roles
* Processamento assíncrono com Kafka
* Integração com API externa
* Relatórios financeiros
* Boas práticas de organização e separação de responsabilidades

O sistema foi desenvolvido com foco em **clareza, extensibilidade e realismo bancário**.
