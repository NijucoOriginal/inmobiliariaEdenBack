package com.jsebastian.eden.EdenSys.repository;


import com.jsebastian.eden.EdenSys.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriaInmuebleRepository extends JpaRepository<HistorialInmueble, Long> {
}
