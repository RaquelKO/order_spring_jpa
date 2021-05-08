package com.example.order.debug;

import java.time.Instant;

import com.example.order.entities.Address;
import com.example.order.entities.Customer;
import com.example.order.entities.Order;
import com.example.order.entities.Product;
import com.example.order.entities.ProductType;
import com.example.order.entities.Seller;
import com.example.order.entities.Supplier;
// import com.example.order.repositories.AddressRepository;
import com.example.order.repositories.CustomerRepository;
import com.example.order.repositories.OrderRepository;
import com.example.order.repositories.ProductRepository;
import com.example.order.repositories.SellerRepository;
import com.example.order.repositories.SupplierRepository;

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

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SupplierRepository supplierRepository;

	@Override
	@Transactional // Informa que tudo isso será feito em uma única transação no banco, mantendo a
					// conexão aberta, ideal para ser usado na camada de serviço
	public void run(String... args) throws Exception {

		System.out.println(" ***** Inicio do Runner! ***** ");
		System.out.println(" ***** Inicio do Transactional! ***** ");

		createCustomers();
		createSellers();
		createProducts();
		createSuppliers();
		associationProductsAndSuppliers();
		createOrders();

		System.out.println(" ***** Fim do Transactional! ***** ");
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

	private void createProducts() {

		Product p1 = new Product();
		p1.setName("Product 1");
		p1.setPrice(100.0);
		p1.setType(ProductType.IMPORTED);

		Product p2 = new Product();
		p2.setName("Product 2");
		p2.setPrice(200.0);
		p2.setType(ProductType.NATIONAL);

		Product p3 = new Product();
		p3.setName("Product 3");
		p3.setPrice(200.0);
		p3.setType(ProductType.NATIONAL);

		productRepository.save(p1);
		productRepository.save(p2);
		productRepository.save(p3);

	}

	private void createSuppliers() {

		Supplier s1 = new Supplier();
		s1.setName("Supplier 1");

		Supplier s2 = new Supplier();
		s2.setName("Supplier 2");

		supplierRepository.save(s1);
		supplierRepository.save(s2);

	}

	private void associationProductsAndSuppliers() {

		Product p1 = productRepository.findById(1L).get();

		Supplier s1 = supplierRepository.findById(1L).get();
		Supplier s2 = supplierRepository.findById(2L).get();

		// Quando usamos mappedBy, existe um lado mais forte da relação
		// Nesse caso Product é o mais forte
		// Então apenas a associação abaixo já é suficiente
		p1.addSupplier(s1);
		p1.addSupplier(s2);

		// Supplier é o mais fraco, então a associação abaixo não salvará no Banco
		// s1.addProduct(p1);
		// s2.addProduct(p1);

		// Se não usássemos o mappedBy, poderíamos usar o @JoinTable
		// em qualquer um dos lados (Product/Supplier)
		// Com o @JoinTable não tem mais forte/mais fraco
		// É necessário ter o @JoinTable em ambos os lados
		// apenas quando queremos renomear as tabelas/colunas

		// Não é necessário salvar pois as entidades já estão no estado gerenciavel
		// Dessa forma a associação será sincronizada com o Banco de Dados

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

		// Pesquisar produtos
		Product p1 = productRepository.findById(1L).get();
		Product p2 = productRepository.findById(2L).get();

		// Adicionar o produto no item
		o1.addItem(10, p1);
		o1.addItem(2, p2);

		// Salvar o pedido
		orderRepository.save(o1);
	}

}
