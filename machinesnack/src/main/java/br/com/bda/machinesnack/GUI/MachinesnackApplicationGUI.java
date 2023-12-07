package br.com.bda.machinesnack.GUI;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import br.com.bda.machinesnack.itens.SnackItem;
import br.com.bda.machinesnack.money.ValidadorNotas;

public class MachinesnackApplicationGUI {

    private static int notaInserida;
    private static Connection connection;

    public static void main(String[] args) {
    	try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost/machine_snack", "postgres", "postgres");

            initializeMachinesnack(connection);

            SwingUtilities.invokeLater(() -> createAndShowGUI());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Máquina de Lanches");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(panel);
        placeComponents(panel);
        frame.setSize(800, 600);
        

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
    	GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); 

        Font labelFont = new Font("Arial", Font.BOLD, 24);

        JLabel label = new JLabel("Bem-vindo à Máquina de Lanches!");
        label.setFont(labelFont);
        panel.add(label, gbc);

        Font buttonFont = new Font("Arial", Font.PLAIN, 18);

        JButton comprarButton = new JButton("Comprar Lanche");
        comprarButton.setFont(buttonFont);
        panel.add(comprarButton, gbc);

        JButton removerButton = new JButton("Remover Produto");
        removerButton.setFont(buttonFont);
        panel.add(removerButton, gbc);

        JButton criarButton = new JButton("Criar Produto");
        criarButton.setFont(buttonFont);
        panel.add(criarButton, gbc);

        JButton atualizarButton = new JButton("Atualizar Produto");
        atualizarButton.setFont(buttonFont);
        panel.add(atualizarButton, gbc);

        JButton sairButton = new JButton("Sair");
        sairButton.setFont(buttonFont);
        panel.add(sairButton, gbc);

        comprarButton.addActionListener(e -> processarCompra());
        removerButton.addActionListener(e -> removerProduto());
        criarButton.addActionListener(e -> criarProduto());
        atualizarButton.addActionListener(e -> atualizarProduto());
        sairButton.addActionListener(e -> System.exit(0));
    

        comprarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processarCompra();
            }
        });

        removerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removerProduto();
            }
        });

        criarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                criarProduto();
            }
        });

        atualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarProduto();
            }
        });

        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
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
    private static void processarCompra() {
        try {
            notaInserida = Integer.parseInt(JOptionPane.showInputDialog("Insira a nota:"));

            if (ValidadorNotas.isValidNote(notaInserida)) {
                double noteValue = ValidadorNotas.getNoteValue(notaInserida);
                JOptionPane.showMessageDialog(null, "Nota válida! Valor: " + noteValue);

                escolherProdutos();

            } else {
                JOptionPane.showMessageDialog(null, "Nota inválida. Tente novamente.");
            }
        } catch (InputMismatchException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida. Certifique-se de digitar valores numéricos corretos.");
        }
    }

    private static void escolherProdutos() {
        try {
            int productId = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do produto que deseja comprar:"));

            if (verificarProdutoExistente(productId)) {
                JOptionPane.showMessageDialog(null, "Produto escolhido: " + productId);

                if (verificarEstoqueSuficiente(productId)) {
                    realizarCompra(productId);
                } else {
                    JOptionPane.showMessageDialog(null, "Produto fora de estoque. Por favor, escolha outro produto.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Produto não encontrado. Verifique o ID do produto e tente novamente.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void realizarCompra(int productId) {
        try {
            SnackItem produto = obterInformacoesProduto(productId);

            double troco = (double) notaInserida - produto.getPreco();

            atualizarEstoque(productId);

            JOptionPane.showMessageDialog(null, "Compra realizada com sucesso!\n" +
                    "Produto: " + produto.getProduto() + "\n" +
                    "Preço: " + produto.getPreco() + "\n" +
                    "Troco: " + troco);

        } catch (InputMismatchException ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida. Certifique-se de digitar valores numéricos corretos.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private static void criarProduto() {
    	try {
            String marca = JOptionPane.showInputDialog("Digite a marca do produto:");
            String produto = JOptionPane.showInputDialog("Digite o nome do produto:");
            int estoque = Integer.parseInt(JOptionPane.showInputDialog("Digite a quantidade em estoque:"));
            double preco = Double.parseDouble(JOptionPane.showInputDialog("Digite o preço do produto:"));

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
                            JOptionPane.showMessageDialog(null, "Produto criado com sucesso! Novo ID: " + novoId);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Falha ao criar o produto.");
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                JOptionPane.showMessageDialog(null, "Erro: já existe um produto com este ID. Tente novamente.");
            } else {
                e.printStackTrace();
            }
        } catch (InputMismatchException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada inválida. Certifique-se de digitar valores numéricos corretos.");
        }
    }
    private static void atualizarProduto() {
    	try {
            int productId = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do produto que deseja atualizar:"));

            if (verificarProdutoExistente(productId)) {
                double novoPreco = Double.parseDouble(JOptionPane.showInputDialog("Digite o novo preço do produto:"));

                String sql = "UPDATE produtos SET preco = ? WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setDouble(1, novoPreco);
                    preparedStatement.setInt(2, productId);

                    int affectedRows = preparedStatement.executeUpdate();

                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(null, "Preço do produto atualizado com sucesso!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Falha ao atualizar o preço do produto.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Produto não encontrado. Verifique o ID do produto e tente novamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InputMismatchException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada inválida. Certifique-se de digitar valores numéricos corretos.");
        }
    }

    private static void removerProduto() {
    	try {
            int productId = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do produto que deseja remover:"));

            if (verificarProdutoExistente(productId)) {
                String sql = "DELETE FROM produtos WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, productId);

                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Produto removido com sucesso!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Produto não encontrado. Verifique o ID do produto e tente novamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InputMismatchException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada inválida. Certifique-se de digitar valores numéricos corretos.");
        }
    }

    private static boolean verificarEstoqueSuficiente(int productId) throws SQLException {
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

    private static SnackItem obterInformacoesProduto(int productId) throws SQLException {
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

    private static boolean verificarProdutoExistente(int productId) throws SQLException {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, productId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static void atualizarEstoque(int productId) throws SQLException {
        String sql = "UPDATE produtos SET estoque = estoque - 1 WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, productId);
            preparedStatement.executeUpdate();
        }
    }
}
