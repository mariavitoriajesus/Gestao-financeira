# 📊 API de Gestão Financeira

Projeto desenvolvido como parte do **Desafio Beca Java 2025/2026**, com o objetivo de criar uma **API de gestão financeira** baseada em **microserviços**, utilizando **Spring Boot**, **Docker**, **Kafka** e **PostgreSQL**.

Até o momento, o projeto contempla a **infraestrutura base** e o **esqueleto dos microserviços**, criados via **Spring Initializr**, prontos para evolução funcional.

---

## 🏗️ Arquitetura Geral

O sistema é composto por **microserviços independentes**, com comunicação assíncrona via **Kafka**, seguindo boas práticas de arquitetura orientada a eventos.

### Microserviços
- **user-service**  
  Responsável pelo gerenciamento de usuários.

- **transaction-api**  
  Responsável pela criação e consulta de transações financeiras (API REST + Producer Kafka).

- **transaction-processor**  
  Responsável pelo processamento assíncrono das transações, consumindo eventos do Kafka.

---

## 🧰 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security**
- **Spring for Apache Kafka**
- **PostgreSQL**
- **Docker / Docker Compose**
- **Apache Kafka**
- **Swagger / OpenAPI**
- **Lombok**

---

## 📁 Estrutura do Projeto
projeto-gestao-financeira/
│
├── docker/
│ └── docker-compose.yml
│
├── user-service/
│
├── transaction-api/
│
├── transaction-processor/
│
├── docs/
│
└── README.md

---

## 🐳 Infraestrutura (Docker)

A infraestrutura do projeto é executada via **Docker Compose**, incluindo:

- PostgreSQL
- pgAdmin
- Kafka
- Zookeeper
- Kafka UI
