CREATE TABLE produtos (
    id INT PRIMARY KEY,
    marca VARCHAR(255),
    produto VARCHAR(255),
    estoque INT,
    preco DECIMAL(10, 2)
);

INSERT INTO produtos (id, marca, produto, estoque, preco) 
VALUES 
(x, 'x', 'x', y, x.y);

SELECT * FROM produtos;

UPDATE produtos SET preco = x WHERE id = x;

DELETE FROM produtos WHERE id = x;
