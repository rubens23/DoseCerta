package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.ViewModel
import com.rubens.applembretemedicamento.framework.data.entities.ConfiguracoesEntity
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentConfiguracoes @Inject constructor(
    private val roomAccess: RoomAccess
): ViewModel() {
    fun mudarConfiguracoes(configuracoesEntity: ConfiguracoesEntity){
        roomAccess.colocarConfiguracoesAtualizadas(configuracoesEntity)
    }

    fun getSwitchersState(): ConfiguracoesEntity? {
        return roomAccess.pegarConfiguracoes()

    }
}