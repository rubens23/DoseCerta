package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.ViewModel
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.entities.ConfiguracoesEntity
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val medicationRepository: MedicationRepositoryImpl,
    private val roomAccess: RoomAccess
): ViewModel() {

    fun desativarOAlarmeDeTodosMedicamentos() {
        medicationRepository.desativarTodosOsAlarmes()
    }

    fun getSwitchersState(): ConfiguracoesEntity? {
        return roomAccess.pegarConfiguracoes()

    }

    fun mudarConfiguracoes(configuracoesEntity: ConfiguracoesEntity){
        roomAccess.colocarConfiguracoesAtualizadas(configuracoesEntity)
    }


}