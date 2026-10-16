--Cargar u insertar los datos obligatorios
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('P001', 'Cuaderno universitario','Papelería',65.00, 25);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('P002', 'Cuaderno cuadriculado','Papelería',75.00, 12);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('P003', 'Lapicero azul','Papelería',12.00, 80);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('P004', 'Resma de papel carta ','Papelería',185.00, 0);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('T001', 'Memoria USB 32 GB','Tecnología',210.00, 8);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('T002', 'Mouse inalámbrico','Tecnología',320.00, 6);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('T003', 'Teclado USB','Tecnología',450.00, 4);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('T004', 'Cable HDMI','Tecnología',180.00, 0);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('A001', 'Mochila escolar','Accesorios',850.00, 5);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('A002', 'Estuche de lápices','Accesorios',95.00, 18);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('A003', 'Botella reutilizable','Accesorios',240.00, 0);
INSERT INTO catalogos.producto(codigo,nombre,categoria,precio,stock) VALUES ('A004', 'Regla metálica ','Accesorios',45.00, 20);


--verificar los datos
SELECT * FROM catalogos.producto;