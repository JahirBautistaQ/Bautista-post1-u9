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

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;



@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

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

@Test
void crear_nombreConEspacios_guardaNombreNormalizado() {

    when(productoRepository.save(any()))
            .thenAnswer(invocation -> {

                Producto p = invocation.getArgument(0);

                p.setId(1L);

                return p;
            });

    productoService.crear(
            " Laptop Pro ",
            1500.0,
            5
    );

    verify(productoRepository)
            .save(productoCaptor.capture());

    Producto capturado = productoCaptor.getValue();

    // nombre normalizado con strip()
    assertEquals("Laptop Pro", capturado.getNombre());

    assertEquals(1500.0, capturado.getPrecio());

    // id todavía null antes de persistirse
    assertEquals(1L, capturado.getId());
}

@Test
void eliminar_productoExistente_llamaDeleteById() {

    Producto producto = new Producto(
            1L,
            "Teclado",
            80.0,
            20
    );

    when(productoRepository.findById(1L))
            .thenReturn(Optional.of(producto));

    doNothing()
            .when(productoRepository)
            .deleteById(1L);

    productoService.eliminar(1L);

    verify(productoRepository, times(1))
            .deleteById(1L);

    verify(productoRepository, times(1))
            .findById(1L);
}

}