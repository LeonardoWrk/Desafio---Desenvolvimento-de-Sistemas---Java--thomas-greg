package com.cadastroclientes.api.repository;

import com.cadastroclientes.api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    Boolean existsByEmail(String email);
    
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Cliente> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    // Exemplo de consulta nativa para uso com Stored Procedure
    @Query(value = "EXEC sp_buscar_clientes_por_cidade :cidade", nativeQuery = true)
    List<Cliente> buscarClientesPorCidade(@Param("cidade") String cidade);
}
