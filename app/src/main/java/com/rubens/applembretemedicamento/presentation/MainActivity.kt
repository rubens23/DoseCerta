package com.rubens.applembretemedicamento.presentation

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rubens.applembretemedicamento.databinding.ActivityMainBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.MyDataStore
import com.rubens.applembretemedicamento.framework.services.ClosingAppServiceService
import com.rubens.applembretemedicamento.framework.viewModels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var binding: ActivityMainBinding
        val pendingIntentsList = ArrayList<PendingIntent>()


    }

    private lateinit var mIntent: Intent
    private lateinit var myDataStore: MyDataStore
    private lateinit var viewModel: MainActivityViewModel

    private val alarmReceiver = AlarmReceiver()

    private var myService: ClosingAppServiceService? = null
    private var isBound = false

    private val connection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ClosingAppServiceService.MyBinder
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }

    }

    inner class MyBinder(private val activity: MainActivity): Binder(){
        fun getActivity():MainActivity{
            return this@MainActivity
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        myDataStore = MyDataStore(applicationContext)
        setContentView(binding.root)

        val navHostFragment = (supportFragmentManager.findFragmentById(binding.fragmentContainerView.id)) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.visibility = View.GONE
//        binding.toolbar.setNavigationOnClickListener {
//            Toast.makeText(this, "eu cliquei pra voltar", Toast.LENGTH_SHORT).show()
//        }

        initViewModel()

        mIntent = Intent(this, ClosingAppServiceService::class.java)
        bindService(mIntent, connection, Context.BIND_AUTO_CREATE)




    }

    fun codigoASerExecutadoAoFecharOApp(){

        lifecycleScope.launch{
            myDataStore.markToastAsNotShown(booleanPreferencesKey(FragmentDetalhesMedicamentos.medicamento.stringDataStore))

        }

        Log.d("testeonclose", "esse metodo foi chamado quando o user fechou o aplicativo")

        //todo testar o onTaskRemoved



        viewModel.desativarOAlarmeDeTodosMedicamentos()
        Log.d("testedeletepi", "to aqui no onDestroy")

        alarmReceiver.cancelAllAlarms(this.applicationContext)

    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        binding.toolbar.visibility = View.GONE


    }



    override fun onStop() {
        super.onStop()
        myService?.startService(mIntent)
        /*

        lifecycleScope.launch{
            myDataStore.markToastAsNotShown(booleanPreferencesKey(FragmentDetalhesMedicamentos.medicamento.stringDataStore))

        }


        viewModel.desativarOAlarmeDeTodosMedicamentos()
        Log.d("testedeletepi", "to aqui no onDestroy")

        alarmReceiver.cancelAllAlarms(this.applicationContext)

         */




    }

    override fun onDestroy() {
        super.onDestroy()


        lifecycleScope.launch{
            myDataStore.markToastAsNotShown(booleanPreferencesKey(FragmentDetalhesMedicamentos.medicamento.stringDataStore))

        }


        viewModel.desativarOAlarmeDeTodosMedicamentos()
        Log.d("testedeletepi", "to aqui no onDestroy")

        alarmReceiver.cancelAllAlarms(this.applicationContext)

    }
}