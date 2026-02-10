USE inmobiliariaeden;

INSERT INTO Cliente (id_cliente, nombre, apellido, email, telefono, contrasenia) VALUES
(1, 'Nicolas', 'Peña', 'diegon.penar@uqvirtual.edu.co', '3001234567', 'pass123'),
(2, 'Mariana', 'Torres', 'mariana.torres@inmo.com', '3109876543', 'pass456'),
(3, 'Sofia', 'Hernández', 'sofia.hernandez@legal.com', '3205554433', 'pass789');

INSERT INTO AgenteInmobiliario (codigoAgente, nombre, apellido, email, telefono, contrasenia, precioNomina) VALUES
(1, 'Marcela', 'Abonce', 'marcela.aboncec@uqvirtual.edu.co', '3151239876', 'agente123', 2500000),
(2, 'Felipe', 'Lopez', 'felipe.lopez@inmo.com', '3118765432', 'agente456', 3000000);

INSERT INTO AsesorLegal (codigoAsesorLegal, nombre, apellido, email, telefono, contrasenia, precioNomina) VALUES
(1, 'Sebastian', 'Alzate', 'jhons.alzatea@uqvirtual.edu.co', '3135551212', 'legal123', 3500000),
(2, 'Javier', 'Castro', 'javier.castro@legal.com', '3224448899', 'legal456', 4000000);

INSERT INTO Inmueble (codigoInmueble, longitud, latitud, precio, habitaciones, banos, descripcion, medidas, fecha_publicacion,
descripcionDireccion, departamento, ciudad, tipoInmueble, tipoNegocio, estadoTransa, agenteAsociado, asesorLegalAsociado, cantidadParqueaderos)
VALUES
(101, -74.08175, 4.60971, 350000000, 3, 2, 'Apartamento en el centro de Bogotá', 85.5, '2023-05-12',
'Calle 15 # 7-30', 'Cundinamarca', 'Bogotá', 'Apartamento', 'Venta', 'Disponible', 1, 1, 1),
(102, -75.56359, 6.25184, 750000000, 4, 3, 'Casa amplia en Medellín', 150.2, '2023-07-20',
'Cra 45 # 30-25', 'Antioquia', 'Medellín', 'Casa', 'Arriendo', 'En proceso', 2, 2, 2);


INSERT INTO HistorialInmueble (fechaInicio, fechaFin, tipoNegocio, precioVenta, descripcion, idHistorial, codigoInmueble, propietario)
VALUES
('2022-01-01', '2022-12-31', 'Arriendo', 12000000, 'se acabo', 1, 101, 1),
('2023-01-01', '2024-12-31', 'Venta', 350000000, 'Permuta por apartamento en Medellín', 2, 101, 1);


INSERT INTO DocumentoImportante (fechaExpedicion, descripcionDocumento, idDocumentoImportante, clienteAsociado, inmuebleAsociado, nombreDocumento)
VALUES
('2022-06-10', 'Escritura pública apartamento Bogotá', 1, 1, 101, 'Escritura_101.pdf'),
('2022-07-15', 'Contrato de arriendo apartamento Bogotá', 2, 2, 101, 'Contrato_Arriendo_101.pdf'),
('2023-08-01', 'Escritura pública casa Medellín', 3, 3, 102, 'Escritura_102.pdf');

SELECT * FROM cliente;
SELECT * FROM agenteinmobiliario;
SELECT * FROM asesorlegal;
SELECT * FROM inmueble;
SELECT * FROM historialinmueble;
SELECT * FROM documentoimportante;