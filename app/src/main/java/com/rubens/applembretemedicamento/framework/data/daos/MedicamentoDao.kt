package com.rubens.applembretemedicamento.framework.data.daos

import androidx.room.*
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento

@Dao
interface MedicamentoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMedicamento(medicamentoTratamento: MedicamentoTratamento): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDose(dose: Doses): Long




    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNaTabelaHistoricoMedicamentos(novoMedicamentoFinalizado: HistoricoMedicamentos): Long

    @Query("UPDATE MedicamentoTratamento SET alarmeTocando =:tocando WHERE idMedicamento =:id")
    fun alarmeMedicamentoTocando(id: Int, tocando: Boolean)








    @Transaction
    @Query("SELECT * FROM MedicamentoTratamento WHERE nomeMedicamento = :nomeMedicamento")
    suspend fun getMedicamentoWithDoses(nomeMedicamento: String): List<MedicamentoComDoses>

    @Transaction
    @Query("SELECT * FROM MedicamentoTratamento WHERE nomeMedicamento = :nomeMedicamento")
    suspend fun getMedicamentoDosesByName(nomeMedicamento: String): MedicamentoComDoses

    @Transaction
    @Query("SELECT * FROM MedicamentoTratamento")
    fun getAllMedicamentoWithDoses(): List<MedicamentoComDoses>

    @Query("SELECT * FROM HistoricoMedicamentos")
    fun getTodosMedicamentosFinalizados(): List<HistoricoMedicamentos>

    @Query("SELECT * FROM MedicamentoTratamento WHERE tratamentoFinalizado = 0")
    fun getAllMedicamentosInUse(): List<MedicamentoTratamento>

    @Query("SELECT * FROM Doses WHERE nomeMedicamento = :nomeMedicamento")
    suspend fun getAllDoses(nomeMedicamento: String): List<Doses>



    @Query("UPDATE doses SET jaTomouDose=:tomou WHERE idDose=:id")
    suspend fun tomarDoseMedicamento(tomou: Boolean, id: Int)

    @Query("UPDATE MedicamentoTratamento SET tratamentoFinalizado=:finalizado WHERE nomeMedicamento=:nomeRemedio")
    suspend fun finalizarMedicamento(finalizado: Boolean, nomeRemedio: String)

    @Query("UPDATE MedicamentoTratamento SET diasRestantesDeTratamento=:diasRestantes WHERE nomeMedicamento=:nomeRemedio")
    suspend fun diaConcluido(diasRestantes: Int, nomeRemedio: String)

    @Query("UPDATE doses SET jaTomouDose=:naoTomou WHERE nomeMedicamento=:nomeRemedio")
    suspend fun resetarDosesTomadasParaDiaNovoDeTratamento(naoTomou: Boolean, nomeRemedio: String)

    @Query("UPDATE MedicamentoTratamento SET horaPrimeiraDose=:msmFinalizado WHERE nomeMedicamento=:nomeRemedio")
    suspend fun atualizarPrimeiraDoseQuandoMedicamentoEstaFinalizado(msmFinalizado: String, nomeRemedio: String)

    @Query("DELETE FROM MedicamentoTratamento WHERE nomeMedicamento =:nomeRemedio")
    fun deleteMedicamentoFromMedicamentoTratamento(nomeRemedio: String)

    @Query("DELETE FROM Doses WHERE nomeMedicamento = :nomeRemedio")
    fun deleteDosesDoMedicamentoFinalizado(nomeRemedio: String)

    @Query("UPDATE medicamentotratamento SET alarmeAtivado=:ativado WHERE nomeMedicamento=:nomeMedicamento")
    fun ligarAlarmeDoMedicamento(nomeMedicamento: String, ativado: Boolean)

    @Query("UPDATE medicamentotratamento SET alarmeAtivado = 0")
    fun desativarTodosOsAlarmes()
}