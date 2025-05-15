package com.company.crs.bean;

import com.company.crs.model.Address;
import com.company.crs.model.Client;
import com.company.crs.util.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.file.UploadedFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

/**
 * Bean para gerenciar o cadastro de clientes usando a classe Client e Address completas
 */
@Named
@SessionScoped
public class ClientBean implements Serializable {

private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ClientBean.class.getName());

    @Inject
    private ApiClient apiClient;

    private Client client = new Client();
    private UploadedFile logotipo;
    private Address novoAddress = new Address();
    private Address selectedAddress;
    private List<Client> clientes = new ArrayList<>();
    private Client clienteSelecionado;
    private JsonNode resultado;

    public ClientBean() {
        // construtor vazio necessário para JSF
    }

    @PostConstruct
    public void init() {
        carregarClientes();
    }

    public void carregarClientes() {
        try {
            JsonNode response = apiClient.get("/clientes");
            clientes.clear();
            if (response != null && response.isArray()) {
                ObjectMapper mapper = new ObjectMapper();
                List<Client> lista = mapper.readValue(
                    mapper.treeAsTokens(response),
                    new TypeReference<List<Client>>() {}
                );
                clientes.addAll(lista);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar clientes", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao carregar clientes.");
        }
    }

    // Método para excluir cliente
    public void excluir(Client cliente) {
        try {
            boolean sucesso = apiClient.delete("/clientes/" + cliente.getId());
            if (sucesso) {
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cliente excluído com sucesso.");
                carregarClientes();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao excluir cliente.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao excluir cliente", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir cliente.");
        }
    }

    public void editar(Client cliente) {
        this.clienteSelecionado = cliente;
        this.client = copiarCliente(cliente);
        this.selectedAddress = null;
        this.novoAddress = new Address();
    }

    private Client copiarCliente(Client original) {
        Client copia = new Client();
        copia.setId(original.getId());
        copia.setNome(original.getNome());
        copia.setEmail(original.getEmail());
        copia.setLogoPath(original.getLogoPath());
        
        List<Address> enderecos = new ArrayList<>();
        for (Address addr : original.getAddresses()) {
            enderecos.add(copiarAddress(addr));
        }
        copia.setAddresses(enderecos);
        
        return copia;
    }

    private Address copiarAddress(Address original) {
        Address copia = new Address();
        copia.setId(original.getId());
        copia.setStreet(original.getStreet());
        copia.setNumber(original.getNumber());
        copia.setComplement(original.getComplement());
        copia.setNeighborhood(original.getNeighborhood());
        copia.setCity(original.getCity());
        copia.setState(original.getState());
        copia.setZipCode(original.getZipCode());
        return copia;
    }

    public void onRowSelect(SelectEvent<Address> event) {
        this.selectedAddress = event.getObject();
        this.novoAddress = copiarAddress(this.selectedAddress);
    }

    public void onRowUnselect(UnselectEvent<Address> event) {
        this.selectedAddress = null;
        this.novoAddress = new Address();
    }

    public String atualizar() {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("nome", client.getNome());
            payload.put("email", client.getEmail());

            List<Map<String, Object>> enderecos = new ArrayList<>();
            for (Address addr : client.getAddresses()) {
                Map<String, Object> endereco = new HashMap<>();
                endereco.put("id", addr.getId()); // Inclui ID existente
                endereco.put("rua", addr.getStreet());
                endereco.put("numero", addr.getNumber());
                endereco.put("complemento", addr.getComplement());
                endereco.put("bairro", addr.getNeighborhood());
                endereco.put("cidade", addr.getCity());
                endereco.put("estado", addr.getState());
                endereco.put("cep", addr.getZipCode());
                enderecos.add(endereco);
            }
            payload.put("logradouros", enderecos);

            JsonNode response = apiClient.put("/clientes/" + clienteSelecionado.getId(), payload);

            if (response != null) {
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cliente atualizado com sucesso!");
                carregarClientes();
                return "listaClientes?faces-redirect=true";
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao atualizar cliente");
                return null;
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao atualizar cliente: " + e.getMessage());
            return null;
        }
    }

    public String salvar() {
        try {
            Map<String, Object> clientMap = new HashMap<>();
            clientMap.put("nome", client.getNome());
            clientMap.put("email", client.getEmail());
            clientMap.put("logotipo", client.getLogoPath());
            
            // Converter lista de endereços para lista de mapas com todos os campos
            List<Map<String, Object>> listaEnderecos = new ArrayList<>();
            for (Address addr : client.getAddresses()) {
                Map<String, Object> enderecoMap = new HashMap<>();
                enderecoMap.put("id", addr.getId());
                enderecoMap.put("rua", addr.getStreet());
                enderecoMap.put("numero", addr.getNumber());
                enderecoMap.put("complemento", addr.getComplement());
                enderecoMap.put("bairro", addr.getNeighborhood());
                enderecoMap.put("cidade", addr.getCity());
                enderecoMap.put("estado", addr.getState());
                enderecoMap.put("cep", addr.getZipCode());
                listaEnderecos.add(enderecoMap);
            }
            clientMap.put("logradouros", listaEnderecos);
            
            // Enviar dados do cliente
            JsonNode response = apiClient.post("/clientes", clientMap);

            if (response != null && response.has("id")) {
                Long clientId = response.get("id").asLong();

                // Upload do logotipo se existir (implementar método)
                if (logotipo != null && logotipo.getContent() != null && logotipo.getContent().length > 0) {
                    uploadLogotipo(clientId);
                }

                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cliente cadastrado com sucesso!");
                limpar();
                return "listaClientes?faces-redirect=true";
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao cadastrar cliente");
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar cliente", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao cadastrar cliente: " + e.getMessage());
            return null;
        }
    }

    private void uploadLogotipo(Long clientId) {
        try {
            if (logotipo != null && logotipo.getContent() != null) {
                // Converte o conteúdo do arquivo para Base64
                String base64Logo = java.util.Base64.getEncoder().encodeToString(logotipo.getContent());
    
                // Cria o payload JSON com apenas o campo logotipo
                Map<String, Object> payload = new HashMap<>();
                payload.put("logotipo", base64Logo);
    
                // Envia via PUT para o endpoint específico de logotipo
                JsonNode response = apiClient.put("/clientes/" + clientId + "/logotipo", payload);
    
                if (response != null && response.has("status") && response.get("status").asText().equalsIgnoreCase("ok")) {
                    LOGGER.info("Logotipo enviado com sucesso para o cliente ID: " + clientId);
                } else {
                    addMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Falha ao salvar logotipo no servidor.");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao enviar logotipo em Base64", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao enviar logotipo.");
        }
    }

    public void addAddress() {
        if (novoAddress.getId() != null) {
            // Atualiza endereço existente
            client.getAddresses().removeIf(a -> a.getId().equals(novoAddress.getId()));
            client.getAddresses().add(novoAddress);
        } else {
            // Adiciona novo endereço
            if (client.getAddresses() == null) {
                client.setAddresses(new ArrayList<>());
            }
            client.getAddresses().add(novoAddress);
        }
        novoAddress = new Address();
        selectedAddress = null;
    }
    

    public void removeAddress(Address address) {
        if (client.getAddresses() != null) {
            client.getAddresses().removeIf(a -> a.getId().equals(address.getId()));
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }


    public String cancelar() {
        if (isFormEmpty()) {
            // Se o formulário está vazio, apenas redireciona
            return "index?faces-redirect=true";
        } else {
            // Se há dados preenchidos, limpa e redireciona
            limpar();
            return "index?faces-redirect=true";
        }
    }
    
    private boolean isFormEmpty() {
        // Verifica se os campos do cliente estão vazios
        boolean clientEmpty = (client == null) ||
                              (isNullOrEmpty(client.getNome()) &&
                               isNullOrEmpty(client.getEmail()) &&
                               (client.getAddresses() == null || client.getAddresses().isEmpty()));
    
        // Verifica se o logotipo está vazio
        boolean logotipoEmpty = (logotipo == null || logotipo.getSize() == 0);   
        return clientEmpty && logotipoEmpty;
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public void limpar() {
        client = new Client();
        logotipo = null;
        novoAddress = new Address();
    }
    // Getters e Setters
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public UploadedFile getLogotipo() {
        return logotipo;
    }

    public void setLogotipo(UploadedFile logotipo) {
        this.logotipo = logotipo;
    }

    public Address getNovoAddress() {
        return novoAddress;
    }

    public void setNovoAddress(Address newAddress) {
        this.novoAddress = newAddress;
    }

    public List<Client> getClientes() {
        return clientes;
    }

    public void setClientes(List<Client> clientes) {
        this.clientes = clientes;
    }

    public Client getClienteSelecionado() {
        return clienteSelecionado;
    }

    public void setClienteSelecionado(Client clienteSelecionado) {
        this.clienteSelecionado = clienteSelecionado;
    }

        // Faltando no seu código atual:
    public Address getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(Address selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

}