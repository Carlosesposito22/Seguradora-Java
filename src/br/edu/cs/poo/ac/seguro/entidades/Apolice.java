package br.edu.cs.poo.ac.seguro.entidades;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
public class Apolice implements Serializable {

    @NonNull private String numero;
    @NonNull private Veiculo veiculo;
    @NonNull private BigDecimal valorFranquia;
    @NonNull private BigDecimal valorPremio;
    @NonNull private BigDecimal valorMaximoSegurado;
    @NonNull private LocalDate dataInicioVigencia;
}
