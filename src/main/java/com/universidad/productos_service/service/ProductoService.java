package com.universidad.productos_service.service;

import com.universidad.productos_service.domain.Producto;;

public interface ProductoService {

    Producto crear(String nombre, Double precio, Integer stock);

    Producto buscarPorId(Long id);

    Producto actualizarStock(Long id, Integer nuevoStock);

    void eliminar(Long id);
}