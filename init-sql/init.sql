-- Exemplo de script SQL
CREATE DATABASE cadastroclientess;
GO
USE cadastroclientess;
GO
CREATE TABLE Clientes (
    Id INT PRIMARY KEY IDENTITY,
    Nome NVARCHAR(100),
    Email NVARCHAR(100)
);
GO
INSERT INTO Clientes (Nome, Email) VALUES ('João', 'joao@email.com');