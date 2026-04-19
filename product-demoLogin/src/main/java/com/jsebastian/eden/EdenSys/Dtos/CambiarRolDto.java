// ─── 2. DTO para cambiar rol ───────────────────────────────────────────────
package com.jsebastian.eden.EdenSys.Dtos;

public record CambiarRolDto(
        Long usuarioId,
        String nuevoRol
) {}