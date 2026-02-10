package com.jsebastian.eden.EdenSys.repository;

import com.jsebastian.eden.EdenSys.domain.Imagen;
import com.jsebastian.eden.EdenSys.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentoImportanteRepository extends JpaRepository<DocumentoImportante, Long> {



}
