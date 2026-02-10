-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: inmobiliariaeden
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `agenteinmobiliario`
--

DROP TABLE IF EXISTS `agenteinmobiliario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agenteinmobiliario` (
  `codigoAgente` int NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `contrasenia` varchar(255) NOT NULL,
  `precioNomina` float NOT NULL,
  PRIMARY KEY (`codigoAgente`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `telefono` (`telefono`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agenteinmobiliario`
--

LOCK TABLES `agenteinmobiliario` WRITE;
/*!40000 ALTER TABLE `agenteinmobiliario` DISABLE KEYS */;
INSERT INTO `agenteinmobiliario` VALUES (1,'Marcela','Abonce','marcela.aboncec@uqvirtual.edu.co','3151239876','agente123',2500000),(2,'Felipe','Lopez','felipe.lopez@inmo.com','3118765432','agente456',3000000);
/*!40000 ALTER TABLE `agenteinmobiliario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asesorlegal`
--

DROP TABLE IF EXISTS `asesorlegal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asesorlegal` (
  `codigoAsesorLegal` int NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `contrasenia` varchar(255) NOT NULL,
  `precioNomina` float DEFAULT NULL,
  PRIMARY KEY (`codigoAsesorLegal`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asesorlegal`
--

LOCK TABLES `asesorlegal` WRITE;
/*!40000 ALTER TABLE `asesorlegal` DISABLE KEYS */;
INSERT INTO `asesorlegal` VALUES (1,'Sebastian','Alzate','jhons.alzatea@uqvirtual.edu.co','3135551212','legal123',3500000),(2,'Javier','Castro','javier.castro@legal.com','3224448899','legal456',4000000);
/*!40000 ALTER TABLE `asesorlegal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_cliente` int NOT NULL,
  `nombre` varchar(200) NOT NULL,
  `apellido` varchar(200) NOT NULL,
  `email` varchar(150) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `contrasenia` varchar(255) NOT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `telefono` (`telefono`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,'Nicolas','Peña','diegon.penar@uqvirtual.edu.co','3001234567','pass123'),(2,'Mariana','Torres','mariana.torres@inmo.com','3109876543','pass456'),(3,'Sofia','Hernández','sofia.hernandez@legal.com','3205554433','pass789');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `documentoimportante`
--

DROP TABLE IF EXISTS `documentoimportante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `documentoimportante` (
  `idDocumentoImportante` int NOT NULL,
  `fechaExpedicion` date NOT NULL,
  `descripcionDocumento` varchar(255) NOT NULL,
  `clienteAsociado` int NOT NULL,
  `inmuebleAsociado` int NOT NULL,
  `nombreDocumento` varchar(200) NOT NULL,
  PRIMARY KEY (`idDocumentoImportante`),
  KEY `clienteAsociado` (`clienteAsociado`),
  KEY `inmuebleAsociado` (`inmuebleAsociado`),
  CONSTRAINT `documentoimportante_ibfk_1` FOREIGN KEY (`clienteAsociado`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `documentoimportante_ibfk_2` FOREIGN KEY (`inmuebleAsociado`) REFERENCES `inmueble` (`codigoInmueble`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `documentoimportante`
--

LOCK TABLES `documentoimportante` WRITE;
/*!40000 ALTER TABLE `documentoimportante` DISABLE KEYS */;
INSERT INTO `documentoimportante` VALUES (1,'2022-06-10','Escritura pública apartamento Bogotá',1,101,'Escritura_101.pdf'),(2,'2022-07-15','Contrato de arriendo apartamento Bogotá',2,101,'Contrato_Arriendo_101.pdf'),(3,'2023-08-01','Escritura pública casa Medellín',3,102,'Escritura_102.pdf');
/*!40000 ALTER TABLE `documentoimportante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historialinmueble`
--

DROP TABLE IF EXISTS `historialinmueble`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historialinmueble` (
  `idHistorial` int NOT NULL,
  `fechaInicio` date NOT NULL,
  `fechaFin` date NOT NULL,
  `tipoNegocio` varchar(50) NOT NULL,
  `precioVenta` float NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `codigoInmueble` int NOT NULL,
  `propietario` int DEFAULT NULL,
  PRIMARY KEY (`idHistorial`),
  KEY `codigoInmueble` (`codigoInmueble`),
  KEY `fk_historial_propietario` (`propietario`),
  CONSTRAINT `fk_historial_propietario` FOREIGN KEY (`propietario`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `historialinmueble_ibfk_1` FOREIGN KEY (`codigoInmueble`) REFERENCES `inmueble` (`codigoInmueble`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historialinmueble`
--

LOCK TABLES `historialinmueble` WRITE;
/*!40000 ALTER TABLE `historialinmueble` DISABLE KEYS */;
INSERT INTO `historialinmueble` VALUES (1,'2022-01-01','2022-12-31','Arriendo',12000000,'se acabo',101,1),(2,'2023-01-01','2024-12-31','Venta',350000000,'Permuta por apartamento en Medellín',101,1);
/*!40000 ALTER TABLE `historialinmueble` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `imagenes`
--

DROP TABLE IF EXISTS `imagenes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `imagenes` (
  `codigoImagen` varchar(50) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `url` varchar(500) NOT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `inmuebleAsociado` int DEFAULT NULL,
  PRIMARY KEY (`codigoImagen`),
  KEY `fk_inmueble` (`inmuebleAsociado`),
  CONSTRAINT `fk_inmueble` FOREIGN KEY (`inmuebleAsociado`) REFERENCES `inmueble` (`codigoInmueble`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `imagenes`
--

LOCK TABLES `imagenes` WRITE;
/*!40000 ALTER TABLE `imagenes` DISABLE KEYS */;
/*!40000 ALTER TABLE `imagenes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inmueble`
--

DROP TABLE IF EXISTS `inmueble`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inmueble` (
  `codigoInmueble` int NOT NULL,
  `longitud` int NOT NULL,
  `latitud` int NOT NULL,
  `precio` float NOT NULL,
  `habitaciones` int NOT NULL,
  `banos` int NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `medidas` float NOT NULL,
  `fecha_publicacion` date NOT NULL,
  `descripcionDireccion` varchar(255) NOT NULL,
  `departamento` varchar(100) NOT NULL,
  `tipoInmueble` varchar(100) NOT NULL,
  `tipoNegocio` varchar(50) NOT NULL,
  `estadoTransa` varchar(50) NOT NULL,
  `ciudad` varchar(100) NOT NULL,
  `agenteAsociado` int NOT NULL,
  `asesorLegalAsociado` int NOT NULL,
  `cantidadParqueaderos` int NOT NULL,
  PRIMARY KEY (`codigoInmueble`),
  KEY `agenteAsociado` (`agenteAsociado`),
  KEY `asesorLegalAsociado` (`asesorLegalAsociado`),
  CONSTRAINT `inmueble_ibfk_1` FOREIGN KEY (`agenteAsociado`) REFERENCES `agenteinmobiliario` (`codigoAgente`),
  CONSTRAINT `inmueble_ibfk_2` FOREIGN KEY (`asesorLegalAsociado`) REFERENCES `asesorlegal` (`codigoAsesorLegal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inmueble`
--

LOCK TABLES `inmueble` WRITE;
/*!40000 ALTER TABLE `inmueble` DISABLE KEYS */;
INSERT INTO `inmueble` VALUES (101,-74,5,350000000,3,2,'Apartamento en el centro de Bogotá',85.5,'2023-05-12','Calle 15 # 7-30','Cundinamarca','Apartamento','Venta','Disponible','Bogotá',1,1,1),(102,-76,6,750000000,4,3,'Casa amplia en Medellín',150.2,'2023-07-20','Cra 45 # 30-25','Antioquia','Casa','Arriendo','En proceso','Medellín',2,2,2);
/*!40000 ALTER TABLE `inmueble` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `apellidos` varchar(100) NOT NULL,
  `cedula` varchar(20) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `email` varchar(150) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `rol` enum('ADMIN','AGENTE','CLIENTE','GERENTE') NOT NULL,
  `telefono` varchar(15) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK72nq30heq3jovypjxbe289jc` (`cedula`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-02 22:19:38
