package com.cadastroclientes.api.service;

import com.cadastroclientes.api.model.Cliente;
import com.cadastroclientes.api.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public List<Cliente> findByNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente update(Long id, Cliente clienteDetails) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o id: " + id));
    
        if (clienteDetails.getNome() != null && !clienteDetails.getNome().isEmpty()) {
            cliente.setNome(clienteDetails.getNome());
        } else {
            cliente.setNome(cliente.getNome()); 
        }
    
        if (clienteDetails.getEmail() != null && !clienteDetails.getEmail().isEmpty()) {
            cliente.setEmail(clienteDetails.getEmail());
        } else {
            cliente.setEmail(cliente.getEmail()); 
        }
    
        if (clienteDetails.getLogotipo() != null) {
            cliente.setLogotipo(clienteDetails.getLogotipo());
        }
    
        return clienteRepository.save(cliente);
    } 

    @Transactional
    public void delete(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o id: " + id));
        
        clienteRepository.delete(cliente);
    }

    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    public List<Cliente> buscarClientesPorCidade(String cidade) {
        return clienteRepository.buscarClientesPorCidade(cidade);
    }
}
