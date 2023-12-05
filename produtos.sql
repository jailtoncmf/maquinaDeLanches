CREATE TABLE produtos (
    id INT PRIMARY KEY,
    marca VARCHAR(255),
    produto VARCHAR(255),
    estoque INT,
    preco DECIMAL(10, 2)
);

INSERT INTO produtos (id, marca, produto, estoque, preco) 
VALUES 
(90, 'Ruffles', 'Batata Chips', 9, 2.5),
(91, 'Doritos', 'Salgadinho Nacho', 7, 5),
(92, 'Dori', 'Amendoim', 10, 5);
(93, 'Fanta', 'Refrigerante de Laranja', 11, 5);

SELECT * FROM produtos;

UPDATE produtos SET preco = x WHERE id = x;

DELETE FROM produtos WHERE id = x;
