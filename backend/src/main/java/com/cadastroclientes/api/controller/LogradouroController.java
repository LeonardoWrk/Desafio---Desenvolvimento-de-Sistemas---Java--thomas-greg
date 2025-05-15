package com.cadastroclientes.api.controller;

import com.cadastroclientes.api.model.Logradouro;
import com.cadastroclientes.api.service.LogradouroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// se questiona a necessidade


@RestController
@RequestMapping("/api/logradouros")
public class LogradouroController {

    @Autowired
    private LogradouroService logradouroService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Logradouro> getAllLogradouros() {
        return logradouroService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Logradouro> getLogradouroById(@PathVariable Long id) {
        return logradouroService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Logradouro> getLogradourosByClienteId(@PathVariable Long clienteId) {
        return logradouroService.findByClienteId(clienteId);
    }

    @GetMapping("/cidade/{cidade}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Logradouro> getLogradourosByCidade(@PathVariable String cidade) {
        return logradouroService.findByCidade(cidade);
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Logradouro> getLogradourosByEstado(@PathVariable String estado) {
        return logradouroService.findByEstado(estado);
    }

    @GetMapping("/cep/{cep}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Logradouro> getLogradourosByCep(@PathVariable String cep) {
        return logradouroService.findByCep(cep);
    }

    @PostMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Logradouro> createLogradouro(@PathVariable Long clienteId, @Valid @RequestBody Logradouro logradouro) {
        Logradouro novoLogradouro = logradouroService.save(clienteId, logradouro);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoLogradouro);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Logradouro> updateLogradouro(@PathVariable Long id, @Valid @RequestBody Logradouro logradouroDetails) {
        Logradouro updatedLogradouro = logradouroService.update(id, logradouroDetails);
        return ResponseEntity.ok(updatedLogradouro);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteLogradouro(@PathVariable Long id) {
        logradouroService.delete(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logradouro excluído com sucesso!");
        return ResponseEntity.ok(response);
    }
}
