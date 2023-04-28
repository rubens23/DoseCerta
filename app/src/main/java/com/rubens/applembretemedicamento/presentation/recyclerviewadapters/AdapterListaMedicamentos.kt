package com.rubens.applembretemedicamento.presentation.recyclerviewadapters

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnScrollChangeListener
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.MedicamentoBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.presentation.FragmentListaMedicamentosDirections
import com.rubens.applembretemedicamento.utils.comunicacaoFragmentAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterListaMedicamentos(private val list: ArrayList<MedicamentoComDoses>, val context: Context): RecyclerView.Adapter<AdapterListaMedicamentos.ViewHolder>() {
    var idNotificacao = 1
    var idMedicamentoTocandoAtualmente = -1
    companion object{

        var listaIdMedicamentos: ArrayList<Int> = ArrayList()
    }

    private var db: AppDatabase? = null
    private lateinit var medicamentoDoseDao: MedicamentoDao
    var listaComDosesToast: MutableLiveData<List<Doses>> = MutableLiveData()

    init{
        db = AppDatabase.getAppDatabase(context)
        //medicamentoDoseDao = db!!.medicamentoDoseDao()
        idMedicamentoTocandoObserver()
    }

    private fun idMedicamentoTocandoObserver() {
        AlarmReceiver.idMedicamentoTocandoAtualmente.observe(context as LifecycleOwner){
            //idMedicamentoTocandoAtualmente = it
            //listaIdMedicamentos.add(it.first())
        }
    }


    inner class ViewHolder(val binding: MedicamentoBinding): RecyclerView.ViewHolder(binding.root) {

        var medicamentoId = -1




        fun bind(medicamento: MedicamentoComDoses){

            medicamentoId = medicamentoId

            var proxDose: String? = null
            var intervaloEntreDoses = 0.0
            var definiuProxDose = false
            for (i in 0..medicamento.listaDoses.size -1){
                intervaloEntreDoses = medicamento.listaDoses.get(i).intervaloEntreDoses
                val horarioDose = medicamento.listaDoses.get(i)
                if(!horarioDose.jaTomouDose && !definiuProxDose){
                        proxDose = horarioDose.horarioDose
                        definiuProxDose = true
                    }
                if (medicamento.medicamentoTratamento.alarmeAtivado){
                    val shake = AnimationUtils.loadAnimation(binding.root.context, R.anim.shake)
                    //binding.alarmeAtivado.startAnimation(shake)
                    binding.alarmeAtivado.visibility = View.VISIBLE
                    if(AlarmReceiver.mp.isPlaying){
                        listaIdMedicamentos.forEach {
                            Log.d("testeids", "$it")
                            if (medicamento.medicamentoTratamento.idMedicamento == it && it > -1){
                                val shake = AnimationUtils.loadAnimation(binding.root.context, R.anim.shake)
                                binding.alarmeAtivado.startAnimation(shake)
                                Log.d("testelistaid", "o id $it esta na lista por isso eu estou balançando o reloginho")
                            }

                        }

                    }else{
                        //val shake = AnimationUtils.loadAnimation(binding.root.context, R.anim.shake)
                        //binding.alarmeAtivado.startAnimation(shake)
                    }
                }
                if(horarioDose.jaTomouDose && i != medicamento.listaDoses.size -1){
                    definiuProxDose = false
                }else if(i == medicamento.listaDoses.size -1 && horarioDose.jaTomouDose){
                    proxDose = medicamento.medicamentoTratamento.horaPrimeiraDose
                    //aqui ele tem que verificar se ainda tem dias restantes de tratamento e
                    //se sim, diminuir um na quantidade de dias restantes de tratamento
                    //todo alterar o valor do if para > 1 e testar
                    GlobalScope.launch {
                        val db = AppDatabase.getAppDatabase(binding.root.context)?.medicamentoDao
                        if(medicamento.medicamentoTratamento.diasRestantesDeTratamento > 1){
                            db?.diaConcluido(medicamento.medicamentoTratamento.diasRestantesDeTratamento - 1, medicamento.medicamentoTratamento.nomeMedicamento)
                            db?.resetarDosesTomadasParaDiaNovoDeTratamento(false, medicamento.medicamentoTratamento.nomeMedicamento)
                        }else{
                           val sdf = SimpleDateFormat("dd/MM/yyyy")
                            val c = Calendar.getInstance()
                            val date = sdf.format(c.time)

                            /*

                             */


                            db?.insertNaTabelaHistoricoMedicamentos(
                                HistoricoMedicamentos(
                                medicamento.medicamentoTratamento.nomeMedicamento,
                                medicamento.medicamentoTratamento.totalDiasTratamento,
                                date
                            )
                            )


                            db?.deleteMedicamentoFromMedicamentoTratamento(medicamento.medicamentoTratamento.nomeMedicamento)
                            db?.deleteDosesDoMedicamentoFinalizado(medicamento.medicamentoTratamento.nomeMedicamento)
                            Log.d("terminoumedicamento", "Voce terminou de tomar esse medicamento!")
                        }

                    }

                }

            }


            if (proxDose != null) {
                Log.d("testeformatadapter", "to no if ${proxDose.length}")
                if(proxDose.length == 15){
                    Log.d("testeformatadapter", "to no if length é 17")

                    binding.horaProximaDose.text = proxDose.subSequence(11,15)
                }
            }

            if (proxDose != null) {
                Log.d("testeformatadapter", "to no if ${proxDose.length}")

                if (proxDose.length == 16){
                    Log.d("testeformatadapter", "to no if length == 18")

                    binding.horaProximaDose.text = proxDose.subSequence(11, 16)
                }
            }


            db = AppDatabase.getAppDatabase(binding.root.context)
            medicamentoDoseDao = db!!.medicamentoDao



            //aqui começa o codigo de tentativa de criação das notificações
            //createNotificationChannel()
            //scheduleNotification(proxDose, medicamento.medicamentoTeste.nomeMedicamento)


            binding.medicamento.setOnClickListener {
                //se eu conseguir passar um objeto aqui na intent
//                val intent = Intent(binding.root.context, MedicineDetailActivity::class.java)
//                intent.putExtra("medicamento", medicamento)
//                intent.putExtra("horaproximadoses", proxDose)
//                intent.putExtra("intervaloentredoses", intervaloEntreDoses)
//                binding.root.context.startActivity(intent)

                val action = proxDose?.let { it1 ->
                    FragmentListaMedicamentosDirections.actionMedicamentosFragmentToFragmentDetalhesMedicamentos(medicamento,
                        it1, intervaloEntreDoses.toString()
                    )
                }

                if (action != null) {
                    binding.root.findNavController().navigate(action)
                }


            }
            binding.medName.text = medicamento.medicamentoTratamento.nomeMedicamento

        }

        fun getItemBinding(): MedicamentoBinding{
            return binding
        }



    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)

        var binding = holder.getItemBinding()

        if(AlarmReceiver.mp.isPlaying){
            //val shake = AnimationUtils.loadAnimation(binding.root.context, R.anim.shake)
            //binding.alarmeAtivado.startAnimation(shake)

        }else{
            //val shake = AnimationUtils.loadAnimation(binding.root.context, R.anim.shake)
            //binding.alarmeAtivado.startAnimation(shake)
        }

    }


    private fun getTime(proxDose: String): Long {
        var minute: Int = 0
        var hour: Int = 0
        Log.d("testehoranot", "proxDose length: ${proxDose.length}")
        Log.d("testehoranot", "proxDose: ${proxDose}")
        if(proxDose.length == 6){
            minute = (proxDose[3].toString()+proxDose[4].toString()).toInt()
            hour = (proxDose[0].toString()+proxDose[1].toString()).toInt()
            Log.d("testehoranot", "to no primeiro if")
        }
        if(proxDose.length == 5){
            minute = (proxDose[2].toString()+proxDose[3].toString()).toInt()
            hour = (proxDose[0].toString()).toInt()
            Log.d("testehoranot", "to no segundo if")
        }
        //por enquanto a data vai estar hardcoded aqui
        val day = 25
        val month = 9
        val year = 2022
        Log.d("testehoranot", "hora: ${hour}  minutos: ${minute}")



        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        Log.d("testehoranot", "hora: ${hour}  minutos: ${minute}")
        return calendar.timeInMillis

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MedicamentoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicamento = list.get(position)
        holder.bind(medicamento)

    }



    override fun getItemCount(): Int{
        Log.d("testehoranot", "list size: ${list.size}")
        return list.size
    }

}

/*
Tem um erro nesse adapter

Shutting down VM
2022-11-27 22:34:03.199 27039-27039 AndroidRuntime          com.example.appmedicamentos          E  FATAL EXCEPTION: main
                                                                                                    Process: com.example.appmedicamentos, PID: 27039
                                                                                                    java.lang.NumberFormatException: For input string: ":1"
                                                                                                    	at java.lang.Integer.parseInt(Integer.java:615)
                                                                                                    	at java.lang.Integer.parseInt(Integer.java:650)
                                                                                                    	at com.example.appmedicamentos.adapters.MedicamentosAdapter.getTime(MedicamentosAdapter.kt:176)
                                                                                                    	at com.example.appmedicamentos.adapters.MedicamentosAdapter.scheduleNotification(MedicamentosAdapter.kt:155)
                                                                                                    	at com.example.appmedicamentos.adapters.MedicamentosAdapter.access$scheduleNotification(MedicamentosAdapter.kt:30)
                                                                                                    	at com.example.appmedicamentos.adapters.MedicamentosAdapter$ViewHolder.bind(MedicamentosAdapter.kt:117)
                                                                                                    	at com.example.appmedicamentos.adapters.MedicamentosAdapter.onBindViewHolder(MedicamentosAdapter.kt:219)
                                                                                                    	at com.example.appmedicamentos.adapters.MedicamentosAdapter.onBindViewHolder(MedicamentosAdapter.kt:30)
                                                                                                    	at androidx.recyclerview.widget.RecyclerView$Adapter.onBindViewHolder(RecyclerView.java:7065)
                                                                                                    	at androidx.recyclerview.widget.RecyclerView$Adapter.bindViewHolder(RecyclerView.java:7107)
                                                                                                    	at androidx.recyclerview.widget.RecyclerView$Recycler.tryBindViewHolderByDeadline(RecyclerView.java:6012)
                                                                                                    	at androidx.recyclerview.widget.RecyclerView$Recycler.tryGetViewHolderForPositionByDeadline(RecyclerView.java:6279)
                                                                                                    	at androidx.recyclerview.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:6118)
                                                                                                    	at androidx.recyclerview.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:6114)
                                                                                                    	at androidx.recyclerview.widget.LinearLayoutManager$LayoutState.next(LinearLayoutManager.java:2303)
                                                                                                    	at androidx.recyclerview.widget.LinearLayoutManager.layoutChunk(LinearLayoutManager.java:1627)
                                                                                                    	at androidx.recyclerview.widget.LinearLayoutManager.fill(LinearLayoutManager.java:1587)
                                                                                                    	at androidx.recyclerview.widget.LinearLayoutManager.onLayoutChildren(LinearLayoutManager.java:665)
                                                                                                    	at androidx.recyclerview.widget.RecyclerView.dispatchLayoutStep2(RecyclerView.java:4134)
                                                                                                    	at androidx.recyclerview.widget.RecyclerView.dispatchLayout(RecyclerView.java:3851)
                                                                                                    	at androidx.recyclerview.widget.RecyclerView.onLayout(RecyclerView.java:4404)
                                                                                                    	at android.view.View.layout(View.java:24475)
                                                                                                    	at android.view.ViewGroup.layout(ViewGroup.java:7383)
                                                                                                    	at androidx.constraintlayout.widget.ConstraintLayout.onLayout(ConstraintLayout.java:1873)
                                                                                                    	at android.view.View.layout(View.java:24475)
                                                                                                    	at android.view.ViewGroup.layout(ViewGroup.java:7383)
                                                                                                    	at android.widget.FrameLayout.layoutChildren(FrameLayout.java:332)
                                                                                                    	at android.widget.FrameLayout.onLayout(FrameLayout.java:270)
                                                                                                    	at android.view.View.layout(View.java:24475)
                                                                                                    	at android.view.ViewGroup.layout(ViewGroup.java:7383)
                                                                                                    	at android.widget.LinearLayout.setChildFrame(LinearLayout.java:1829)
                                                                                                    	at android.widget.LinearLayout.layoutVertical(LinearLayout.java:1673)
                                                                                                    	at android.widget.LinearLayout.onLayout(LinearLayout.java:1582)
                                                                                                    	at android.view.View.layout(View.java:24475)
                                                                                                    	at android.view.ViewGroup.layout(ViewGroup.java:7383)
                                                                                                    	at android.widget.FrameLayout.layoutChildren(FrameLayout.java:332)
                                                                                                    	at android.widget.FrameLayout.onLayout(FrameLayout.java:270)
                                                                                                    	at android.view.View.layout(View.java:24475)
                                                                                                    	at android.view.ViewGroup.layout(ViewGroup.java:7383)
                                                                                                    	at android.widget.LinearLayout.setChildFrame(LinearLayout.java:1829)
                                                                                                    	at android.widget.LinearLayout.layoutVertical(LinearLayout.java:1673)
                                                                                                    	at android.widget.LinearLayout.onLayout(LinearLayout.java:1582)
                                                                                                    	at android.view.View.layout(View.java:24475)
                                                                                                    	at android.view.ViewGroup.layout(ViewGroup.java:7383)
                                                                                                    	at android.widget.FrameLayout.layoutChildren(FrameLayout.java:332)
                                                                                                    	at android.widget.FrameLayout.onLayout(FrameLayout.java:270)
                                                                                                    	at com.android.internal.policy.DecorView.onLayout(DecorView.java:1225)
                                                                                                    	at android.view.View.layout(View.java:24475)
                                                                                                    	at android.view.ViewGroup.layout(ViewGroup.java:7383)
                                                                                                    	at android.view.ViewRootImpl.performLayout(ViewRootImpl.java:4260)
                                                                                                    	at android.view.ViewRootImpl.performTraversals(ViewRootImpl.java:3695)
                                                                                                    	at android.view.ViewRootImpl.doTraversal(ViewRootImpl.java:2618)
2022-11-27 22:34:03.200 27039-27039 AndroidRuntime          com.example.appmedicamentos          E  	at android.view.ViewRootImpl$TraversalRunnable.run(ViewRootImpl.java:9971)
                                                                                                    	at android.view.Choreographer$CallbackRecord.run(Choreographer.java:1010)
                                                                                                    	at android.view.Choreographer.doCallbacks(Choreographer.java:809)
                                                                                                    	at android.view.Choreographer.doFrame(Choreographer.java:744)
                                                                                                    	at android.view.Choreographer$FrameDisplayEventReceiver.run(Choreographer.java:995)
                                                                                                    	at android.os.Handler.handleCallback(Handler.java:938)
                                                                                                    	at android.os.Handler.dispatchMessage(Handler.java:99)
                                                                                                    	at android.os.Looper.loop(Looper.java:246)
                                                                                                    	at android.app.ActivityThread.main(ActivityThread.java:8645)
                                                                                                    	at java.lang.reflect.Method.invoke(Native Method)
                                                                                                    	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:602)
                                                                                                    	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1130)
 */