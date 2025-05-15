package com.cadastroclientes.api.repository;

import com.cadastroclientes.api.model.Logradouro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogradouroRepository extends JpaRepository<Logradouro, Long> {
    List<Logradouro> findByClienteId(Long clienteId);
    
    @Query("SELECT l FROM Logradouro l WHERE l.cidade = :cidade")
    List<Logradouro> findByCidade(@Param("cidade") String cidade);
    
    @Query("SELECT l FROM Logradouro l WHERE l.estado = :estado")
    List<Logradouro> findByEstado(@Param("estado") String estado);
    
    @Query("SELECT l FROM Logradouro l WHERE l.cep = :cep")
    List<Logradouro> findByCep(@Param("cep") String cep);
}
