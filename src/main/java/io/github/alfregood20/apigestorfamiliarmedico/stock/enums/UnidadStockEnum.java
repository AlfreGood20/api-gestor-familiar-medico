package io.github.alfregood20.apigestorfamiliarmedico.stock.enums;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.FormaFarmaceuticaEnum;

public enum UnidadStockEnum {
    TABLETAS, CAPSULAS, ML, GRAMOS, DOSIS, PIEZAS, OTRA;

    public static UnidadStockEnum de(FormaFarmaceuticaEnum forma) {
        return switch (forma) {
            case TABLETA -> TABLETAS;
            case CAPSULA -> CAPSULAS;
            case JARABE, SUSPENSION, GOTAS, INYECCION -> ML;
            case CREMA, POMADA -> GRAMOS;
            case INHALADOR -> DOSIS;
            case SUPOSITORIO, PARCHE -> PIEZAS;
            case OTRA -> OTRA;
        };
    }
}
