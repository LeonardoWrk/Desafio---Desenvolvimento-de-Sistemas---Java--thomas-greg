package com.cadastroclientes.api.service;

import com.cadastroclientes.api.model.Cliente;
import com.cadastroclientes.api.model.Logradouro;
import com.cadastroclientes.api.repository.ClienteRepository;
import com.cadastroclientes.api.repository.LogradouroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LogradouroService {

    @Autowired
    private LogradouroRepository logradouroRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Logradouro> findAll() {
        return logradouroRepository.findAll();
    }

    public Optional<Logradouro> findById(Long id) {
        return logradouroRepository.findById(id);
    }

    public List<Logradouro> findByClienteId(Long clienteId) {
        return logradouroRepository.findByClienteId(clienteId);
    }

    public List<Logradouro> findByCidade(String cidade) {
        return logradouroRepository.findByCidade(cidade);
    }

    public List<Logradouro> findByEstado(String estado) {
        return logradouroRepository.findByEstado(estado);
    }

    public List<Logradouro> findByCep(String cep) {
        return logradouroRepository.findByCep(cep);
    }

    @Transactional
    public Logradouro save(Long clienteId, Logradouro logradouro) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o id: " + clienteId));
        
        logradouro.setCliente(cliente);
        return logradouroRepository.save(logradouro);
    }

    @Transactional
    public Logradouro update(Long id, Logradouro logradouroDetails) {
        Logradouro logradouro = logradouroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logradouro não encontrado com o id: " + id));
        
        logradouro.setRua(logradouroDetails.getRua());
        logradouro.setNumero(logradouroDetails.getNumero());
        logradouro.setComplemento(logradouroDetails.getComplemento());
        logradouro.setBairro(logradouroDetails.getBairro());
        logradouro.setCidade(logradouroDetails.getCidade());
        logradouro.setEstado(logradouroDetails.getEstado());
        logradouro.setCep(logradouroDetails.getCep());
        
        return logradouroRepository.save(logradouro);
    }

    @Transactional
    public void delete(Long id) {
        Logradouro logradouro = logradouroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logradouro não encontrado com o id: " + id));
        
        logradouroRepository.delete(logradouro);
    }
}
