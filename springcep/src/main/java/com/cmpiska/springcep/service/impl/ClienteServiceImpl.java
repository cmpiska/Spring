package com.cmpiska.springcep.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmpiska.springcep.model.Cliente;
import com.cmpiska.springcep.model.ClienteRepository;
import com.cmpiska.springcep.model.Endereco;
import com.cmpiska.springcep.model.EnderecoRepository;
import com.cmpiska.springcep.service.ClienteService;
import com.cmpiska.springcep.service.ViaCepService;


@Service
public class ClienteServiceImpl implements ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	

	@Override
	public Iterable<Cliente> buscarTodos() {
	//Busca por todos os clientes
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		//Busca cliente por id
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		//Busca Cliente por ID, caso exista
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if (clienteBd.isPresent()) {
			salvarClienteComCep(cliente);
		}
	}

	@Override
	public void deletar(Long id) {
		//Deleta Cliente por ID.
		clienteRepository.deleteById(id);
	}

	private void salvarClienteComCep(Cliente cliente) {
		//Verifica se o Endereco do Cliente já existe
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Insere Cliente, e atualiza endereço caso já exista
		clienteRepository.save(cliente);
	}

}
