package com.citt.persistence.services;

import com.citt.exceptions.VentaNotFoundException;
import com.citt.persistence.entity.Venta;
import com.citt.persistence.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Venta venta;

    @BeforeEach
    void setUp() {
        venta = Venta.builder()
                .idVenta(1L)
                .direccionCompra("Av. Providencia 1234, Santiago")
                .valorCompra(150000)
                .fechaCompra(LocalDate.of(2026, 5, 10))
                .despachoGenerado(false)
                .build();
    }

    @Test
    @DisplayName("findAllVentas retorna lista con todas las ventas")
    void findAllVentas_retornaLista() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta));
        List<Venta> resultado = ventaService.findAllVentas();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDireccionCompra()).isEqualTo("Av. Providencia 1234, Santiago");
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllVentas retorna lista vacía cuando no hay ventas")
    void findAllVentas_retornaListaVacia() {
        when(ventaRepository.findAll()).thenReturn(List.of());
        List<Venta> resultado = ventaService.findAllVentas();
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("saveVenta guarda y retorna la venta correctamente")
    void saveVenta_guardaYRetornaVenta() {
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        Venta resultado = ventaService.saveVenta(venta);
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdVenta()).isEqualTo(1L);
        assertThat(resultado.getValorCompra()).isEqualTo(150000);
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    @DisplayName("findById retorna la venta cuando existe")
    void findById_retornaVenta_cuandoExiste() throws VentaNotFoundException {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        Venta resultado = ventaService.findById(1L);
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdVenta()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById lanza VentaNotFoundException cuando no existe")
    void findById_lanzaExcepcion_cuandoNoExiste() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ventaService.findById(99L))
                .isInstanceOf(VentaNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("updateVenta actualiza los campos correctamente")
    void updateVenta_actualizaCampos() throws VentaNotFoundException {
        Venta ventaActualizada = Venta.builder()
                .direccionCompra("Calle Nueva 567")
                .valorCompra(200000)
                .fechaCompra(LocalDate.of(2026, 6, 1))
                .despachoGenerado(true)
                .build();
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));
        Venta resultado = ventaService.updateVenta(1L, ventaActualizada);
        assertThat(resultado.getDireccionCompra()).isEqualTo("Calle Nueva 567");
        assertThat(resultado.getValorCompra()).isEqualTo(200000);
        assertThat(resultado.getDespachoGenerado()).isTrue();
    }

    @Test
    @DisplayName("updateVenta lanza VentaNotFoundException cuando no existe")
    void updateVenta_lanzaExcepcion_cuandoNoExiste() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ventaService.updateVenta(99L, venta))
                .isInstanceOf(VentaNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("updateVenta no modifica dirección si viene vacía")
    void updateVenta_noModificaDireccion_siVieneBlancos() throws VentaNotFoundException {
        Venta ventaParcial = Venta.builder()
                .direccionCompra("   ")
                .valorCompra(200000)
                .build();
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));
        Venta resultado = ventaService.updateVenta(1L, ventaParcial);
        assertThat(resultado.getDireccionCompra()).isEqualTo("Av. Providencia 1234, Santiago");
    }

    @Test
    @DisplayName("deleteVenta elimina correctamente cuando existe")
    void deleteVenta_eliminaCorrectamente() throws VentaNotFoundException {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        doNothing().when(ventaRepository).deleteById(1L);
        ventaService.deleteVenta(1L);
        verify(ventaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteVenta lanza VentaNotFoundException cuando no existe")
    void deleteVenta_lanzaExcepcion_cuandoNoExiste() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ventaService.deleteVenta(99L))
                .isInstanceOf(VentaNotFoundException.class)
                .hasMessageContaining("99");
        verify(ventaRepository, never()).deleteById(any());
    }
}