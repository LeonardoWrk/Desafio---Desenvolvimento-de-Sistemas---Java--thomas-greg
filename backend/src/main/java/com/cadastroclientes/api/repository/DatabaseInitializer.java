package com.cadastroclientes.api.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        String createProcedureSql = 
            "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'sqsl.CriarUsuarioInicial') AND type IN (N'P', N'PC')) " +
            "BEGIN " +
            "EXEC('CREATE PROCEDURE sqsl.CriarUsuarioInicial AS BEGIN SET NOCOUNT ON; " +
            "IF NOT EXISTS (SELECT 1 FROM sqsl.usuarios WHERE email = ''usuario1@email.com'') " +
            "BEGIN " +
            "INSERT INTO sqsl.usuarios (id, email, password, username) " +
            "VALUES (1, ''usuario1@email.com'', ''$2a$10$OrUWQP1B2xp1kwxPPLEIS.uYfYjmokIj1CPKnl962DHFLSO2j8aHK'', ''usuario''); " +
            "END END') " +
            "END";

      
    }
}