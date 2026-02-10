USE inmobiliariaeden;

CREATE TABLE Cliente (
    id_cliente INT PRIMARY KEY ,
    nombre VARCHAR(200) NOT NULL,
    apellido VARCHAR(200) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20) UNIQUE NOT NULL,
    contrasenia VARCHAR(255) NOT NULL
);

CREATE TABLE AgenteInmobiliario (
    codigoAgente INT PRIMARY KEY ,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20) UNIQUE NOT NULL,
    contrasenia VARCHAR(255) NOT NULL,
    precioNomina FLOAT NOT NULL
);

CREATE TABLE AsesorLegal (
    codigoAsesorLegal INT PRIMARY KEY ,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    contrasenia VARCHAR(255) NOT NULL,
    precioNomina FLOAT
);

CREATE TABLE Inmueble (
    codigoInmueble INT PRIMARY KEY,
    longitud INT NOT NULL,
    latitud INT NOT NULL,
    precio FLOAT NOT NULL,
    habitaciones INT NOT NULL,
    banos INT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    medidas FLOAT NOT NULL,
    fecha_publicacion DATE NOT NULL,
    descripcionDireccion VARCHAR(255) NOT NULL,
    departamento VARCHAR(100) NOT NULL,
    tipoInmueble VARCHAR(100) NOT NULL,
    tipoNegocio VARCHAR(50) NOT NULL,
    estadoTransa VARCHAR(50) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    agenteAsociado INT NOT NULL,
    asesorLegalAsociado INT NOT NULL,
    cantidadParqueaderos INT NOT NULL,
    FOREIGN KEY (agenteAsociado) REFERENCES AgenteInmobiliario(codigoAgente),
    FOREIGN KEY (asesorLegalAsociado) REFERENCES AsesorLegal(codigoAsesorLegal)
);

CREATE TABLE DocumentoImportante (
    idDocumentoImportante INT PRIMARY KEY,
    fechaExpedicion DATE NOT NULL,
    descripcionDocumento VARCHAR(255) NOT NULL,
    clienteAsociado INT NOT NULL,
    inmuebleAsociado INT NOT NULL,
    nombreDocumento VARCHAR(200) NOT NULL,
    FOREIGN KEY (clienteAsociado) REFERENCES Cliente(id_cliente),
    FOREIGN KEY (inmuebleAsociado) REFERENCES Inmueble(codigoInmueble)
);

CREATE TABLE HistorialInmueble (
    idHistorial INT PRIMARY KEY,
    fechaInicio DATE NOT NULL,
    fechaFin DATE NOT NULL,
    tipoNegocio VARCHAR(50) NOT NULL,
    precioVenta FLOAT NOT NULL,
    descripcion VARCHAR(255),
    codigoInmueble INT NOT NULL,
    propietario INT NOT NULL,
    FOREIGN KEY (codigoInmueble) REFERENCES Inmueble(codigoInmueble)
);

CREATE TABLE Imagenes (
    codigoImagen VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    descripcion VARCHAR(500),
    inmuebleAsociado int,
    CONSTRAINT fk_inmueble
        FOREIGN KEY (inmuebleAsociado)
        REFERENCES Inmueble(codigoInmueble)
);

ALTER TABLE HistorialInmueble 
MODIFY descripcion VARCHAR(255);

ALTER TABLE HistorialInmueble
CHANGE descripcionPermutacion descripcion VARCHAR(255) ;

SELECT * FROM HistorialInmueble;

ALTER TABLE HistorialInmueble
ADD COLUMN propietario INT,
ADD CONSTRAINT fk_historial_propietario FOREIGN KEY (propietario) REFERENCES Cliente(id_cliente);

ALTER TABLE DocumentoImportante
ADD COLUMN nombreDocumento VARCHAR(200) NOT NULL;
