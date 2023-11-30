CREATE TABLE produtos (
    id INT PRIMARY KEY,
    marca VARCHAR(255),
    produto VARCHAR(255),
    estoque INT,
    preco DECIMAL(10, 2)
);

INSERT INTO produtos (id, marca, produto, estoque, preco) 
VALUES 
(1, 'Ruffles', 'Batata Chips', 9, 2.5),
(2, 'Doritos', 'Salgadinho Nacho', 5, 5);
(3, 'Amendoim', 'Amendoim', 2, 5);


SELECT * FROM produtos;

UPDATE produtos SET estoque = 8 WHERE id = 1;

DELETE FROM produtos;
