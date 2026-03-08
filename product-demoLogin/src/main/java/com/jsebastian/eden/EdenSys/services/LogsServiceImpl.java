package com.jsebastian.eden.EdenSys.services;

import com.jsebastian.eden.EdenSys.domain.Logs;
import com.jsebastian.eden.EdenSys.repository.LogsRepository;
import com.jsebastian.eden.EdenSys.repository.UserRepository;
import com.jsebastian.eden.EdenSys.services.interfaces.LogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LogsServiceImpl implements LogsService {

    @Autowired
    LogsRepository logsRepository;

    @Autowired
    UserRepository userRepository;


    @Override
    public void registrarLog(String descripcion,Long idUsuario) {
        Logs log = new Logs();
        log.setFechaDeEmision(java.time.LocalDateTime.now());
        log.setUsuario(userRepository.findById(idUsuario).orElse(null));
        log.setDescripcion(descripcion);
        logsRepository.save(log);
    }
}
