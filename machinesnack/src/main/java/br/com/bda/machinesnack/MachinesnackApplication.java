package br.com.bda.machinesnack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.bda.machinesnack.itens.SnackItem;
import br.com.bda.machinesnack.money.ValidadorNotas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;



@SpringBootApplication
	public class MachinesnackApplication {

	    public static void main(String[] args) {
	        try {
	            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/machine_snack", "postgres", "fifa16");

	            initializeMachinesnack(connection);

	            while (true) {
	                displayMenu();
	                Scanner scanner = new Scanner(System.in);
	                int choice = scanner.nextInt();

	                switch (choice) {
	                    case 1:
	                    	processarCompra(connection);
	                        break;
	                    
	                    case 2:
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

	    private static void initializeMachinesnack(Connection connection) throws SQLException {
	    	 try (Statement statement = connection.createStatement()) {
	    	        String createTableSQL = "CREATE TABLE IF NOT EXISTS produtos ("
	    	                + "id SERIAL PRIMARY KEY,"
	    	                + "marca VARCHAR(255),"
	    	                + "produto VARCHAR(255),"
	    	                + "estoque INT,"
	    	                + "preco DOUBLE PRECISION"
	    	                + ")";
	    	        statement.executeUpdate(createTableSQL);

	    	        System.out.println("Tabela de produtos criada com sucesso.");
	    	    } catch (SQLException e) {
	    	        e.printStackTrace();
	    	        throw e; 
	    	    }
	    
	    	
	    }

	    private static void displayMenu() {
	        System.out.println("Bem-vindo à Máquina de Lanches!");
	        System.out.println("Selecione uma opção:");
	        System.out.println("1. Comprar um lanche");
	        System.out.println("3. Sair");
	    }

	    private static void processarCompra(Connection connection) {
	        Scanner scanner = new Scanner(System.in);

	    	System.out.println("Insira a nota:");
	        int note = scanner.nextInt();

	        if (ValidadorNotas.isValidNote(note)) {
	            double noteValue = ValidadorNotas.getNoteValue(note);
	            System.out.println("Nota válida! Valor: " + noteValue);

	            escolherProdutos(connection);

	        } else {
	            System.out.println("Nota inválida. Tente novamente.");
	        }
	    }
	    
	    private static void escolherProdutos(Connection connection) {
	        try {
	            System.out.println("Produtos disponíveis:");
	            exibirProdutos(connection);

	            Scanner scanner = new Scanner(System.in);
	            System.out.println("Digite o ID do produto que deseja comprar:");
	            int productId = scanner.nextInt();

	            if (verificarProdutoExistente(connection, productId)) {
	                System.out.println("Produto escolhido: " + productId);
	            } else {
	                System.out.println("Produto não encontrado. Verifique o ID do produto e tente novamente.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    
	}
	    
	    private static void exibirProdutos(Connection connection) throws SQLException {
	        String sql = "SELECT * FROM produtos";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                while (resultSet.next()) {
	                    int id = resultSet.getInt("id");
	                    String marca = resultSet.getString("marca");
	                    String produto = resultSet.getString("produto");
	                    int estoque = resultSet.getInt("estoque");
	                    double preco = resultSet.getDouble("preco");

	                    System.out.println(id + ". " + marca + " - " + produto + " - Estoque: " + estoque + " - Preço: " + preco);
	                }
	            }
	        }
	    }

	    private static boolean verificarProdutoExistente(Connection connection, int productId) throws SQLException {
	        String sql = "SELECT * FROM produtos WHERE id = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setInt(1, productId);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                return resultSet.next();
	            }
	        }}
	    }
