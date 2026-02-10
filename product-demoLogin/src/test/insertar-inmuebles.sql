-- Script para insertar 10 inmuebles distintos en PostgreSQL
-- Ajustado para ser compatible con la estructura de tablas existente

-- Usar la base de datos (en PostgreSQL se selecciona con \c inmobiliariaeden)
-- \c inmobiliariaeden;

-- Insertar 10 inmuebles con características distintas
INSERT INTO inmueble (codigoInmueble, longitud, latitud, precio, habitaciones, banos, descripcion, medidas, fecha_publicacion,
descripcionDireccion, departamento, ciudad, tipoInmueble, tipoNegocio, estadoTransa, agenteAsociado, asesorLegalAsociado, cantidadParqueaderos)
VALUES
-- Apartamento en Bogotá para venta
(103, -74.08300, 4.61010, 420000000, 3, 2, 'Moderno apartamento en Chapinero con vista panorámica', 92.5, '2025-01-15',
'Carrera 12 # 50-30', 'Cundinamarca', 'Bogotá', 'Apartamento', 'Venta', 'Disponible', 1, 1, 2),

-- Casa en Medellín para arriendo
(104, -75.56500, 6.25200, 2200000, 4, 3, 'Hermosa casa en Laureles con jardín', 180.0, '2025-01-20',
'Calle 40 Sur # 70-50', 'Antioquia', 'Medellín', 'Casa', 'Arriendo', 'Disponible', 2, 2, 3),

-- Finca en Cali para venta
(105, -76.52200, 3.43700, 1200000000, 5, 4, 'Finca productiva en el valle del Cauca', 5000.0, '2025-02-01',
'Vereda El Llanito', 'Valle del Cauca', 'Cali', 'Finca', 'Venta', 'Disponible', 1, 1, 5),

-- Apartaestudio en Cartagena para arriendo
(106, -75.88500, 10.39100, 850000, 1, 1, 'Moderno apartaestudio en el centro histórico', 45.0, '2025-02-05',
'Calle del Colegio # 12-25', 'Bolívar', 'Cartagena', 'Apartaestudio', 'Arriendo', 'Disponible', 2, 2, 1),

-- Lote en Barranquilla para venta
(107, -74.80000, 10.98000, 250000000, 0, 0, 'Lote comercial en zona exclusiva del norte', 320.0, '2025-02-10',
'Avenida Circunvalar # 80-40', 'Atlántico', 'Barranquilla', 'Lote', 'Venta', 'Disponible', 1, 1, 0),

-- Casa en Bucaramanga para venta
(108, -73.11900, 7.11900, 650000000, 4, 3, 'Casa campestre en el sector norte de Bucaramanga', 220.0, '2025-02-12',
'Calle 55 # 30-20', 'Santander', 'Bucaramanga', 'Casa', 'Venta', 'Disponible', 2, 2, 4),

-- Oficina en Pereira para arriendo
(109, -75.67000, 4.81000, 1200000, 2, 2, 'Oficina en centro empresarial de Pereira', 85.0, '2025-02-15',
'Avenida 30 de Agosto # 15-35', 'Risaralda', 'Pereira', 'Oficina', 'Arriendo', 'Disponible', 1, 1, 2),

-- Apartamento en Manizales para venta
(110, -75.51700, 5.07000, 280000000, 3, 2, 'Apartamento con acabados de alta calidad', 78.0, '2025-02-18',
'Diagonal 50 # 25-40', 'Caldas', 'Manizales', 'Apartamento', 'Venta', 'Disponible', 2, 2, 1),

-- Bodega en Cúcuta para arriendo
(111, -72.50500, 7.89300, 1500000, 1, 1, 'Bodega industrial en zona franca', 250.0, '2025-02-20',
'Autopista Oriental Km 2', 'Norte de Santander', 'Cúcuta', 'Bodega', 'Arriendo', 'Disponible', 1, 1, 3),

-- Casa en Santa Marta para venta
(112, -74.19000, 11.24000, 950000000, 5, 4, 'Casa en primera línea de playa en El Rodadero', 320.0, '2025-02-22',
'Calle 5 # 20-30', 'Magdalena', 'Santa Marta', 'Casa', 'Venta', 'Disponible', 2, 2, 4);

-- Insertar imágenes para los inmuebles
INSERT INTO imagen (codigoImagen, nombre, url, descripcion, inmueble_codigoInmueble) VALUES
-- Imágenes para el apartamento en Bogotá (código 103)
('IMG103-1', 'Fachada del apartamento', 'https://images.unsplash.com/photo-1568605114967-8130f3a36994?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Vista frontal del apartamento en Chapinero', 103),
('IMG103-2', 'Sala principal', 'https://images.unsplash.com/photo-1600566753177-026c07b071f5?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Amplia sala con iluminación natural', 103),
('IMG103-3', 'Cocina moderna', 'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Cocina integral con acabados modernos', 103),

-- Imágenes para la casa en Medellín (código 104)
('IMG104-1', 'Fachada principal', 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Hermosa casa en Laureles con jardín', 104),
('IMG104-2', 'Jardín trasero', 'https://images.unsplash.com/photo-1591747204386-7c1a51203e6b?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Amplio jardín con área de BBQ', 104),
('IMG104-3', 'Habitación principal', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Habitación con vestier y baño privado', 104),

-- Imágenes para la finca en Cali (código 105)
('IMG105-1', 'Vista general', 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Finca productiva en el valle del Cauca', 105),
('IMG105-2', 'Área de cultivo', 'https://images.unsplash.com/photo-1597842190408-35bd6fb2564d?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Zona de cultivos de la finca', 105),
('IMG105-3', 'Casa principal', 'https://images.unsplash.com/photo-1597848758211-60d8ecc9b75d?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Casa principal con vista panorámica', 105),

-- Imágenes para el apartaestudio en Cartagena (código 106)
('IMG106-1', 'Vista exterior', 'https://images.unsplash.com/photo-1582531543631-1a1bb0a1df9d?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Moderno apartaestudio en el centro histórico', 106),
('IMG106-2', 'Área de cocina', 'https://images.unsplash.com/photo-1583849528985-9709949a0354?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Mini cocina integral moderna', 106),

-- Imágenes para el lote en Barranquilla (código 107)
('IMG107-1', 'Vista del lote', 'https://images.unsplash.com/photo-1582407947304-fd86f028f716?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Lote comercial en zona exclusiva del norte', 107),
('IMG107-2', 'Ubicación del lote', 'https://images.unsplash.com/photo-1582407947304-fd86f028f716?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Mapa de ubicación del lote', 107),

-- Imágenes para la casa en Bucaramanga (código 108)
('IMG108-1', 'Fachada de la casa', 'https://images.unsplash.com/photo-1591747204386-7c1a51203e6b?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Casa campestre en el sector norte de Bucaramanga', 108),
('IMG108-2', 'Sala comedor', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Amplia sala comedor con iluminación natural', 108),

-- Imágenes para la oficina en Pereira (código 109)
('IMG109-1', 'Fachada del edificio', 'https://images.unsplash.com/photo-1497366754035-f200968a6e72?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Oficina en centro empresarial de Pereira', 109),
('IMG109-2', 'Área de trabajo', 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Espacio de trabajo colaborativo', 109),

-- Imágenes para el apartamento en Manizales (código 110)
('IMG110-1', 'Vista del apartamento', 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Apartamento con acabados de alta calidad', 110),
('IMG110-2', 'Balcón con vista', 'https://images.unsplash.com/photo-1600585152220-90363fe7e115?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Balcón con hermosa vista panorámica', 110),

-- Imágenes para la bodega en Cúcuta (código 111)
('IMG111-1', 'Fachada de la bodega', 'https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Bodega industrial en zona franca', 111),
('IMG111-2', 'Interior de la bodega', 'https://images.unsplash.com/photo-1586528116494-821cb72e0eff?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Amplio espacio interior de la bodega', 111),

-- Imágenes para la casa en Santa Marta (código 112)
('IMG112-1', 'Vista de la playa', 'https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Casa en primera línea de playa en El Rodadero', 112),
('IMG112-2', 'Piscina de la casa', 'https://images.unsplash.com/photo-1560448205-258fee27de8f?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&h=400&q=80', 'Piscina con vista al mar', 112);

-- Verificar que los inmuebles se hayan insertado correctamente
SELECT * FROM inmueble WHERE codigoInmueble BETWEEN 103 AND 112;

-- Verificar que las imágenes se hayan insertado correctamente
SELECT * FROM imagen WHERE inmueble_codigoInmueble BETWEEN 103 AND 112;