package com.jsebastian.eden.EdenSys.services.interfaces;

import com.jsebastian.eden.EdenSys.Dtos.ContactoChatDTO;

import java.util.List;

public interface ContactoService {
    List<ContactoChatDTO> getContactos(String email);
    ContactoChatDTO agregarContacto(String userEmail, String contactoEmail);
    void eliminarContacto(String userEmail, String contactoEmail);
}
