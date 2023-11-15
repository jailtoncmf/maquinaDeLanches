package br.com.bda.machinesnack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

@SpringBootApplication
	public class MachinesnackApplication {

	    public static void main(String[] args) {
	        try {
	            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/machine_snack", "postgres", "fifa16");

	            initializeVendingMachine(connection);

	            while (true) {
	                displayMenu();
	                Scanner scanner = new Scanner(System.in);
	                int choice = scanner.nextInt();

	                switch (choice) {
	                    case 1:
	                        processPurchase(connection);
	                        break;
	                    case 2:
	                        addProductsToStock(connection);
	                        break;
	                    case 3:
	                        System.out.println("Saindo da máquina de lanches. Obrigado!");
	                        System.exit(0);
	                        break;
	                    default:
	                        System.out.println("Opção inválida. Tente novamente.");
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void initializeVendingMachine(Connection connection) throws SQLException {
	      
	    }

	    private static void displayMenu() {
	        System.out.println("Bem-vindo à Máquina de Lanches!");
	        System.out.println("Selecione uma opção:");
	        System.out.println("1. Comprar um lanche");
	        System.out.println("2. Adicionar produtos ao estoque");
	        System.out.println("3. Sair");
	    }

	    private static void processPurchase(Connection connection) {
	    }

	    private static void addProductsToStock(Connection connection) {
	    }
	}
