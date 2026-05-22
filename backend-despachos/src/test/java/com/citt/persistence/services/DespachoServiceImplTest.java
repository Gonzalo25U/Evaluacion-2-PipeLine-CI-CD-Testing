package com.citt.persistence.services;

import com.citt.exceptions.DespachoNotFoundException;
import com.citt.persistence.entity.Despacho;
import com.citt.persistence.repository.DespachoRepository;
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
class DespachoServiceImplTest {

    @Mock
    private DespachoRepository despachoRepository;

    @InjectMocks
    private DespachoServiceImpl despachoService;

    private Despacho despacho;

    @BeforeEach
    void setUp() {
        despacho = new Despacho();
        despacho.setIdDespacho(1L);
        despacho.setFechaDespacho(LocalDate.of(2026, 5, 10));
        despacho.setPatenteCamion("ABCD12");
        despacho.setIntento(0);
        despacho.setIdCompra(100L);
        despacho.setDireccionCompra("Av. Providencia 1234, Santiago");
        despacho.setValorCompra(150000L);
        despacho.setDespachado(false);
    }

    @Test
    @DisplayName("findAllDespachos retorna lista con todos los despachos")
    void findAllDespachos_retornaLista() {
        when(despachoRepository.findAll()).thenReturn(List.of(despacho));
        List<Despacho> resultado = despachoService.findAllDespachos();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPatenteCamion()).isEqualTo("ABCD12");
        verify(despachoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllDespachos retorna lista vacía cuando no hay despachos")
    void findAllDespachos_retornaListaVacia() {
        when(despachoRepository.findAll()).thenReturn(List.of());
        List<Despacho> resultado = despachoService.findAllDespachos();
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("saveDespacho guarda y retorna el despacho correctamente")
    void saveDespacho_guardaYRetornaDespacho() {
        when(despachoRepository.save(any(Despacho.class))).thenReturn(despacho);
        Despacho resultado = despachoService.saveDespacho(despacho);
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdDespacho()).isEqualTo(1L);
        assertThat(resultado.getPatenteCamion()).isEqualTo("ABCD12");
        verify(despachoRepository, times(1)).save(despacho);
    }

    @Test
    @DisplayName("findById retorna el despacho cuando existe")
    void findById_retornaDespacho_cuandoExiste() throws DespachoNotFoundException {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
        Despacho resultado = despachoService.findById(1L);
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdDespacho()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById lanza DespachoNotFoundException cuando no existe")
    void findById_lanzaExcepcion_cuandoNoExiste() {
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> despachoService.findById(99L))
                .isInstanceOf(DespachoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("updateDespacho actualiza los campos correctamente")
    void updateDespacho_actualizaCampos() throws DespachoNotFoundException {
        Despacho despachoActualizado = new Despacho();
        despachoActualizado.setFechaDespacho(LocalDate.of(2026, 6, 1));
        despachoActualizado.setPatenteCamion("XYZ99");
        despachoActualizado.setIntento(2);
        despachoActualizado.setIdCompra(200L);
        despachoActualizado.setDireccionCompra("Calle Nueva 567");
        despachoActualizado.setValorCompra(200000L);
        despachoActualizado.setDespachado(true);

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
        when(despachoRepository.save(any(Despacho.class))).thenAnswer(inv -> inv.getArgument(0));

        Despacho resultado = despachoService.updateDespacho(1L, despachoActualizado);

        assertThat(resultado.getPatenteCamion()).isEqualTo("XYZ99");
        assertThat(resultado.getIntento()).isEqualTo(2);
        assertThat(resultado.isDespachado()).isTrue();
        assertThat(resultado.getDireccionCompra()).isEqualTo("Calle Nueva 567");
    }

    @Test
    @DisplayName("updateDespacho lanza DespachoNotFoundException cuando no existe")
    void updateDespacho_lanzaExcepcion_cuandoNoExiste() {
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> despachoService.updateDespacho(99L, despacho))
                .isInstanceOf(DespachoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("deleteDespacho elimina correctamente cuando existe")
    void deleteDespacho_eliminaCorrectamente() throws DespachoNotFoundException {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
        doNothing().when(despachoRepository).deleteById(1L);
        despachoService.deleteDespacho(1L);
        verify(despachoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteDespacho lanza DespachoNotFoundException cuando no existe")
    void deleteDespacho_lanzaExcepcion_cuandoNoExiste() {
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> despachoService.deleteDespacho(99L))
                .isInstanceOf(DespachoNotFoundException.class)
                .hasMessageContaining("99");
        verify(despachoRepository, never()).deleteById(any());
    }
}