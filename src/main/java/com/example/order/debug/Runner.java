package com.example.order.debug;

import java.time.Instant;

import com.example.order.entities.Address;
import com.example.order.entities.Customer;
import com.example.order.entities.Order;
import com.example.order.entities.Seller;
// import com.example.order.repositories.AddressRepository;
import com.example.order.repositories.CustomerRepository;
import com.example.order.repositories.OrderRepository;
import com.example.order.repositories.SellerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Runner implements CommandLineRunner {

	@Autowired
	private CustomerRepository customerRepository;

	// @Autowired
	// private AddressRepository addressRepository;

	@Autowired
	private SellerRepository sellerRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	@Transactional // Informa que tudo isso será feito em uma única transação no banco, mantendo a
					// conexão aberta
	public void run(String... args) throws Exception {

		System.out.println(" ***** Inicio do Runner! ***** ");

		createCustomers();
		createSellers();
		createOrders();

		System.out.println(" ***** Fim do Runner! ***** ");

	}

	private void createCustomers() {

		Customer c1 = new Customer();
		c1.setEmail("ana@gmail.com");
		c1.setName("Ana");

		Customer c2 = new Customer();
		c2.setEmail("pedro@gmail.com");
		c2.setName("Pedro");

		Address adr1 = new Address();
		adr1.setCity("Sorocaba");
		adr1.setStreet("Rua X, 99");

		Address adr2 = new Address();
		adr2.setCity("Sorocaba");
		adr2.setStreet("Rua Y, 123");

		Address adr3 = new Address();
		adr3.setCity("Votorantim");
		adr3.setStreet("Rua Z, 67");

		// Estabelece a relação entre customers e addresses
		c1.addAddress(adr1);
		c1.addAddress(adr2);

		c2.addAddress(adr3);

		// addressRepository.save(adr1);
		// Devido ao cascade persist não é mais necessário salvar o endereço a parte
		// Porém CUIDADO, se não usar o cascade de PERSIST a ordem para salvar um
		// customer e um address é IMPORTANTE!!!

		customerRepository.save(c1);

		customerRepository.save(c2);

	}

	private void createSellers() {

		Seller s1 = new Seller();
		s1.setDepartment("department 1");
		s1.setName("Marcos");

		Seller s2 = new Seller();
		s2.setDepartment("department 2");
		s2.setName("Fernanda");

		sellerRepository.save(s1);

		sellerRepository.save(s2);
	}

	private void createOrders() {

		// Criar um pedido para Ana sendo o Marcos o vendedor!

		Order o1 = new Order();
		o1.setDate(Instant.now());
		o1.setNumber(1L);

		// Isso recupera somente o customer, NÃO recupera as coleções,
		// ou seja, isso nao recupera os Address!
		// O carregamento dos Address é TARDIO (Lazy)
		Customer c1 = customerRepository.findById(1L).get();
		Seller s1 = sellerRepository.findById(3L).get();

		// Associações
		o1.setCustomer(c1);
		o1.setSeller(s1);

		// Não precisa pesquisar o endereço, ele já está associado ao customer
		// Atenção!!!
		// É necessário fazer o carregamento IMEDIATO do Address para funcionar
		// OU usar a anotação @Transactional
		// Nesse exemplo estamos usando o @Transactional no método run
		o1.setDeliveredAdress(c1.getAddresses().get(0));

		// Salvar o pedido
		orderRepository.save(o1);
	}

}
