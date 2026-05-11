package com.universidad.productos_service.service;

import com.universidad.productos_service.domain.Producto;
import com.universidad.productos_service.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;



@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void crear_datosValidos_retornaProductoGuardado() {

        Producto guardado = new Producto(
                1L,
                "Laptop",
                1500.0,
                10
        );

        when(productoRepository.save(any(Producto.class)))
                .thenReturn(guardado);

        Producto resultado = productoService.crear(
                "Laptop",
                1500.0,
                10
        );

        assertNotNull(resultado.getId());

        assertEquals("Laptop", resultado.getNombre());

        verify(productoRepository, times(1))
                .save(any(Producto.class));
    }

    @Test
    void buscarPorId_existente_retornaProducto() {

        Producto producto = new Producto(
                1L,
                "Mouse",
                50.0,
                100
        );

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        Producto resultado = productoService.buscarPorId(1L);

        assertEquals("Mouse", resultado.getNombre());

        assertEquals(50.0, resultado.getPrecio());
    }

@Test
void buscarPorId_noExistente_lanzaRuntimeException() {

    when(productoRepository.findById(99L))
            .thenReturn(Optional.empty());

    assertThrows(
            RuntimeException.class,
            () -> productoService.buscarPorId(99L)
    );
}

@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {" ", "\t", "\n"})
void crear_nombreInvalido_lanzaIllegalArgumentException(
        String nombre
) {

    assertThrows(
            IllegalArgumentException.class,
            () -> productoService.crear(nombre, 100.0, 5)
    );

    // El repositorio NO debe ser llamado
    verifyNoInteractions(productoRepository);
}

@ParameterizedTest
@ValueSource(doubles = {0.0, -1.0, -100.0, -0.01})
void crear_precioInvalido_lanzaIllegalArgumentException(
        double precio
) {

    assertThrows(
            IllegalArgumentException.class,
            () -> productoService.crear("Producto", precio, 5)
    );

    verifyNoInteractions(productoRepository);
}


}