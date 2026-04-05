package com.jsebastian.eden.EdenSys.services.interfaces;

import com.jsebastian.eden.EdenSys.domain.Mensaje;

import java.util.List;

public interface MensajeService {

    public Mensaje save(Mensaje mensaje);
    public List<Mensaje> getChat(String user1, String user2);
}
