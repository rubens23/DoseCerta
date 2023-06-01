package com.rubens.applembretemedicamento.framework.data

import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento

object FakeMedicamentoData {
    val medicamentosComDoses = listOf(
        MedicamentoComDoses(
            MedicamentoTratamento(
                "Paracetamol",
                "01/06/2023 08:00",
                4,
                1,
                false,
                1,
                14,
                14,
                false,
                "01/06/2023",
                "14/06/2023",
                "01/01/2022",
                false
            ),
            listOf(
                Doses(
                    1,
                    "Paracetamol",
                    "01/06/2023 08:00",
                    6.0,
                    "01/06/2023  08:00",
                    1,
                    false
                ),
                Doses(
                    2,
                    "Paracetamol",
                    "01/06/2023 14:00",
                    6.0,
                    null,
                    1,
                    false
                )
            )
        ),
        MedicamentoComDoses(
            MedicamentoTratamento(
                "Dipirona",
                "01/06/2023 10:00",
                3,
                2,
                false,
                1,
                7,
                7,
                false,
                "01/06/2023",
                "07/06/2023",
                "01/01/2022",
                false
            ),
            listOf(
                Doses(
                    3,
                    "Dipirona",
                    "01/06/2023 10:00",
                    8.0,
                    "01/06/2023 10:00",
                    1,
                    false
                ),
                Doses(
                    4,
                    "Dipirona",
                    "01/06/2023 18:00",
                    8.0,
                    null,
                    1,
                    false
                ),
                Doses(
                    5,
                    "Dipirona",
                    "02/06/2023 02:00",
                    8.0,
                    null,
                    1,
                    false
                )
            )
        ),
        MedicamentoComDoses(
            MedicamentoTratamento(
                "Ibuprofeno",
                "01/06/2023 08:00",
                2,
                3,
                false,
                1,
                3,
                3,
                false,
                "01/06/2023",
                "03/06/2023",
                "01/01/2022",
                false
            ),
            listOf(
                Doses(
                    6,
                    "Ibuprofeno",
                    "01/06/2023 08:00",
                    12.0,
                    "01/06/2023  08:00",
                    2,
                    false
                ),
                Doses(
                    7,
                    "Ibuprofeno",
                    "01/06/2023 20:00",
                    12.0,
                    null,
                    2,
                    false
                )
            )
        )
    )
}