package com.cadastroclientes.api.controller;

import com.cadastroclientes.api.model.Cliente;
import com.cadastroclientes.api.model.Logradouro;
import com.cadastroclientes.api.model.LogradouroDTO;
import com.cadastroclientes.api.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Cliente> getAllClientes() {
        return clienteService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        return clienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Cliente> getClienteByEmail(@PathVariable String email) {
        return clienteService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Cliente> searchClientes(@RequestParam String nome) {
        return clienteService.findByNome(nome);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCliente(@Valid @RequestBody Cliente cliente) {
        if (clienteService.existsByEmail(cliente.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email já está em uso!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<Logradouro> logradourosOriginais = new ArrayList<>(cliente.getLogradouros());

        for (Logradouro logradouro : logradourosOriginais) {
            cliente.addLogradouro(logradouro);
        }

        Cliente novoCliente = clienteService.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCliente(@PathVariable Long id, @RequestBody Cliente clienteDetails) {
        Optional<Cliente> clienteOpt = clienteService.findById(id);
        
        if (!clienteOpt.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cliente não encontrado com id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Cliente cliente = clienteOpt.get();
        
        // Validação manual de campos obrigatórios
        if (clienteDetails.getEmail() == null || clienteDetails.getEmail().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email é obrigatório");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (clienteService.existsByEmail(clienteDetails.getEmail()) &&
            !cliente.getEmail().equals(clienteDetails.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email já está em uso por outro cliente!");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Atualização de campos
        cliente.setNome(clienteDetails.getNome());
        cliente.setEmail(clienteDetails.getEmail());
        cliente.setLogotipo(clienteDetails.getLogotipo());
        
        // Lógica de Logradouros
        if (clienteDetails.getLogradouros() != null) {
            if (clienteDetails.getLogradouros().isEmpty()) {
                cliente.getLogradouros().clear();
            } else {
                for (Logradouro inputLogradouro : clienteDetails.getLogradouros()) {
                    if (inputLogradouro.getId() != null) {
                        Optional<Logradouro> existingLogradouroOpt = cliente.getLogradouros()
                            .stream()
                            .filter(l -> l.getId().equals(inputLogradouro.getId()))
                            .findFirst();
        
                        // Verifica se o logradouro com o ID pertence ao cliente
                        if (!existingLogradouroOpt.isPresent()) {
                            Map<String, String> response = new HashMap<>();
                            response.put("message", "Logradouro com id " + inputLogradouro.getId() + " não pertence ao cliente.");
                            return ResponseEntity.badRequest().body(response);
                        }
        
                        Logradouro existingLogradouro = existingLogradouroOpt.get();
                        if (inputLogradouro.getRua() != null) existingLogradouro.setRua(inputLogradouro.getRua());
                        if (inputLogradouro.getNumero() != null) existingLogradouro.setNumero(inputLogradouro.getNumero());
                        if (inputLogradouro.getComplemento() != null) existingLogradouro.setComplemento(inputLogradouro.getComplemento());
                        if (inputLogradouro.getBairro() != null) existingLogradouro.setBairro(inputLogradouro.getBairro());
                        if (inputLogradouro.getCidade() != null) existingLogradouro.setCidade(inputLogradouro.getCidade());
                        if (inputLogradouro.getEstado() != null) existingLogradouro.setEstado(inputLogradouro.getEstado());
                        if (inputLogradouro.getCep() != null) existingLogradouro.setCep(inputLogradouro.getCep());
                    } else {
                        // Novo logradouro (somente se vier campos preenchidos)
                        boolean hasData = inputLogradouro.getRua() != null ||
                                          inputLogradouro.getNumero() != null ||
                                          inputLogradouro.getBairro() != null ||
                                          inputLogradouro.getCidade() != null ||
                                          inputLogradouro.getEstado() != null ||
                                          inputLogradouro.getCep() != null ||
                                          inputLogradouro.getComplemento() != null;
        
                        if (hasData) {
                            inputLogradouro.setCliente(cliente);
                            cliente.addLogradouro(inputLogradouro);
                        }
                    }
                }
            }
        }
        
        Cliente updatedCliente = clienteService.save(cliente);
        return ResponseEntity.ok(updatedCliente);
    }
    
    @PutMapping("/{id}/logotipo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLogotipo(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Optional<Cliente> clienteOpt = clienteService.findById(id);
        if (!clienteOpt.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cliente não encontrado com id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    
        String base64Logotipo = payload.get("logotipo");
        if (base64Logotipo == null || base64Logotipo.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logotipo é obrigatório");
            return ResponseEntity.badRequest().body(response);
        }
    
        try {
            byte[] logotipoBytes = Base64.getDecoder().decode(base64Logotipo);
            Cliente cliente = clienteOpt.get();
            cliente.setLogotipo(logotipoBytes);
            clienteService.save(cliente); // ou update, conforme sua implementação
    
            Map<String, String> response = new HashMap<>();
            response.put("status", "ok");
            response.put("message", "Logotipo atualizado com sucesso");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logotipo inválido (base64 mal formatado)");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id) {
        clienteService.delete(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cliente excluído com sucesso!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/logotipo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadLogotipo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Cliente cliente = clienteService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o id: " + id));
            
            cliente.setLogotipo(file.getBytes());
            clienteService.save(cliente);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logotipo enviado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Erro ao enviar o logotipo: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/logotipo")
    public ResponseEntity<byte[]> getLogotipo(@PathVariable Long id) {
        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o id: " + id));
        
        if (cliente.getLogotipo() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(cliente.getLogotipo());
    }

    @GetMapping("/cidade/{cidade}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Cliente> getClientesByCidade(@PathVariable String cidade) {
        return clienteService.buscarClientesPorCidade(cidade);
    }
}
