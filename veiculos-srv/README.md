# veiculos-srv

API REST (Spring Boot 3, Java 17) para gestão de **veículos**, **pneus** e **aplicação/remoção de pneus** (posição no veículo), com PostgreSQL e migrações **Flyway**. Estrutura em camadas alinhada ao projeto de referência **gohome_srv_cantina** (`controller`, `service`, `repository`, `mapper`, `dto`, `entity`, `config`, `exception`). **JWT não é usado**; o Spring Security está configurado para permitir todos os endpoints (adequado ao escopo do teste).

## Guia rápido — rodar em outra máquina

1. **Instalar** [Java 17](https://adoptium.net/) e [Docker Desktop](https://www.docker.com/products/docker-desktop/) (ou Docker Engine + Compose).
2. **Copiar o projeto** para a máquina (clone do Git ou pasta zipada) e abrir um terminal na pasta `veiculos-srv`.
3. **Subir o PostgreSQL:**
   ```bash
   docker compose up -d
   ```
   Aguarde o container ficar saudável (uns segundos). A API usa por padrão `localhost` na porta **5434** do host.
4. **Subir a API** (não precisa instalar Maven; o projeto traz o wrapper):
   - **Windows (PowerShell ou CMD):** `mvnw.cmd spring-boot:run`
   - **Linux / macOS / Git Bash:** `./mvnw spring-boot:run`  
   Na primeira vez o wrapper baixa o Maven (precisa de internet).
5. **Testar:** abra no navegador [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) e use os endpoints pela interface Swagger.

Se a porta **8080** estiver ocupada, antes do passo 4 defina outra porta, por exemplo no PowerShell: `$env:SERVER_PORT="8090"`. Para mudar a porta do banco, altere o mapeamento em `docker-compose.yml` e use a mesma porta em `DB_PORT` (o padrão da aplicação é **5434** no host).

---

