package com.rubens.applembretemedicamento.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import javax.inject.Inject

class MainFragmentFactory
@Inject
constructor(
    private val alarmUtilsInterface: AlarmUtilsInterface,
    private val medicamentoManager: MedicamentoManager,
    private val funcoesDeTempo: FuncoesDeTempo,
    private val calendarHelper: CalendarHelper,
    private val context: Context

) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            FragmentListaMedicamentos::class.java.name->{
                FragmentListaMedicamentos(alarmUtilsInterface, medicamentoManager, funcoesDeTempo, calendarHelper, context)
            }
            FragmentDetalhesMedicamentos::class.java.name->{
                FragmentDetalhesMedicamentos(alarmUtilsInterface, funcoesDeTempo, calendarHelper)
            }
            FragmentHistoricoMedicamentos::class.java.name->{
                FragmentHistoricoMedicamentos()
            }
            FragmentConfiguracoes::class.java.name->{
                FragmentConfiguracoes()
            }
            FragmentCadastrarNovoMedicamento::class.java.name->{
                FragmentCadastrarNovoMedicamento(funcoesDeTempo, calendarHelper)
            }
            else->super.instantiate(classLoader, className)
        }

    }

}
