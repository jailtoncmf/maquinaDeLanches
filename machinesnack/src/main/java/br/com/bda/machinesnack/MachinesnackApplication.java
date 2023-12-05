package br.com.bda.machinesnack;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

import br.com.bda.machinesnack.itens.SnackItem;
import br.com.bda.machinesnack.money.ValidadorNotas;


@SpringBootApplication
	public class MachinesnackApplication {

	private static int notaInserida; 
    private static Scanner scanner = new Scanner(System.in);	 
    
    public static void main(String[] args) {
	        try {
	            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/machine_snack", "postgres", "fifa16");

	            initializeMachinesnack(connection);

	            while (true) {
	                displayMenu();
	                int choice = scanner.nextInt();

	                switch (choice) {
	                case 1:
	                    processarCompra(connection);
	                    break;
	                case 2:
	                    removerProduto(connection);
	                    break;
	                case 3:
	                    criarProduto(connection);
	                    break;
	                case 4:
	                    atualizarProduto(connection);
	                    break;
	                case 5:
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
	    private static void criarProduto(Connection connection) {
	        try {

	            System.out.println("Digite a marca do produto:");
	            String marca = scanner.next();

	            scanner.nextLine();

	            System.out.println("Digite o nome do produto:");
	            String produto = scanner.nextLine();

	            System.out.println("Digite a quantidade em estoque:");
	            int estoque = scanner.nextInt();

	            System.out.println("Digite o preço do produto:");
	            double preco = scanner.nextDouble();

	            String sql = "INSERT INTO produtos (marca, produto, estoque, preco) VALUES (?, ?, ?, ?)";
	            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	                preparedStatement.setString(1, marca);
	                preparedStatement.setString(2, produto);
	                preparedStatement.setInt(3, estoque);
	                preparedStatement.setDouble(4, preco);

	                int affectedRows = preparedStatement.executeUpdate();

	                if (affectedRows > 0) {
	                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
	                        if (generatedKeys.next()) {
	                            int novoId = generatedKeys.getInt(1);
	                            System.out.println("Produto criado com sucesso! Novo ID: " + novoId);
	                        }
	                    }
	                } else {
	                    System.out.println("Falha ao criar o produto.");
	                }
	            }
	        } catch (SQLException e) {
	            if (e.getSQLState().equals("23505")) {
	                System.out.println("Erro: já existe um produto com este ID. Tente novamente.");
	            } else {
	                e.printStackTrace();
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Entrada inválida. Certifique-se de digitar valores numéricos corretos.");
	        }
	    }
	    private static void atualizarProduto(Connection connection) {
	        try {

	            System.out.println("Digite o ID do produto que deseja atualizar:");
	            int productId = scanner.nextInt();

	            if (verificarProdutoExistente(connection, productId)) {
	                System.out.println("Digite o novo preço do produto:");
	                double novoPreco = scanner.nextDouble();

	                String sql = "UPDATE produtos SET preco = ? WHERE id = ?";
	                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	                    preparedStatement.setDouble(1, novoPreco);
	                    preparedStatement.setInt(2, productId);

	                    int affectedRows = preparedStatement.executeUpdate();

	                    if (affectedRows > 0) {
	                        System.out.println("Preço do produto atualizado com sucesso!");
	                    } else {
	                        System.out.println("Falha ao atualizar o preço do produto.");
	                    }
	                }
	            } else {
	                System.out.println("Produto não encontrado. Verifique o ID do produto e tente novamente.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } catch (InputMismatchException e) {
	            System.out.println("Entrada inválida. Certifique-se de digitar valores numéricos corretos.");
	        }
	    }

	    private static void removerProduto(Connection connection) {
	        try {

	            System.out.println("Digite o ID do produto que deseja remover:");
	            int productId = scanner.nextInt();

	            if (verificarProdutoExistente(connection, productId)) {
	                String sql = "DELETE FROM produtos WHERE id = ?";
	                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	                    preparedStatement.setInt(1, productId);

	                    preparedStatement.executeUpdate();
	                    System.out.println("Produto removido com sucesso!");
	                }
	            } else {
	                System.out.println("Produto não encontrado. Verifique o ID do produto e tente novamente.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void displayMenu() {
	        System.out.println("Bem-vindo à Máquina de Lanches!");
	        System.out.println("Selecione uma opção:");
	        System.out.println("1. Comprar um lanche");
	        System.out.println("2. Remover produto");
	        System.out.println("3. Criar produto");
	        System.out.println("4. Atualizar produto");
	        System.out.println("5. Sair");
	    }

	    private static void processarCompra(Connection connection) throws SQLException {
	        try {
	            System.out.println("Insira a nota:");
	            notaInserida = scanner.nextInt();

	            if (ValidadorNotas.isValidNote(notaInserida)) {
	                double noteValue = ValidadorNotas.getNoteValue(notaInserida);
	                System.out.println("Nota válida! Valor: " + noteValue);

	                escolherProdutos(connection);

	            } else {
	                System.out.println("Nota inválida. Tente novamente.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Entrada inválida. Certifique-se de digitar valores numéricos corretos.");
	        }
	    }
	    private static void escolherProdutos(Connection connection) {
	        try {
	            System.out.println("Produtos disponíveis:");
	            exibirProdutos(connection);

	            System.out.println("Digite o ID do produto que deseja comprar:");
	            int productId = scanner.nextInt();

	            if (verificarProdutoExistente(connection, productId)) {
	                System.out.println("Produto escolhido: " + productId);

	                if (verificarEstoqueSuficiente(connection, productId)) {
	                    realizarCompra(connection, productId, notaInserida);
	                } else {
	                    System.out.println("Produto fora de estoque. Por favor, escolha outro produto.");
	                }
	            } else {
	                System.out.println("Produto não encontrado. Verifique o ID do produto e tente novamente.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static boolean verificarEstoqueSuficiente(Connection connection, int productId) throws SQLException {
	        String sql = "SELECT estoque FROM produtos WHERE id = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setInt(1, productId);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    int estoque = resultSet.getInt("estoque");
	                    return estoque > 0;
	                }
	            }
	        }
	        return false;
	    }

	    private static void realizarCompra(Connection connection, int productId, int nota ) {
	    	try {
	            SnackItem produto = obterInformacoesProduto(connection, productId);

	            double troco = nota - produto.getPreco();

	            atualizarEstoque(connection, productId);

	            System.out.println("Compra realizada com sucesso!");
	            System.out.println("Produto: " + produto.getProduto());
	            System.out.println("Preço: " + produto.getPreco());
	            System.out.println("Troco: " + troco);
	        } catch (InputMismatchException e) {
	            System.out.println("Entrada inválida. Certifique-se de digitar valores numéricos corretos.");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	

	    private static SnackItem obterInformacoesProduto(Connection connection, int productId) throws SQLException {
	        String sql = "SELECT produto, preco FROM produtos WHERE id = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setInt(1, productId);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    String produto = resultSet.getString("produto");
	                    double preco = resultSet.getDouble("preco");
	                    return new SnackItem(produto, preco);
	                }
	            }
	        }
	        throw new SQLException("Produto não encontrado.");
	    }

	 

	    private static void atualizarEstoque(Connection connection, int productId) throws SQLException {
	        String sql = "UPDATE produtos SET estoque = estoque - 1 WHERE id = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setInt(1, productId);
	            preparedStatement.executeUpdate();
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
