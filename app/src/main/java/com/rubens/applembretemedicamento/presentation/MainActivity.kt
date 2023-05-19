package com.rubens.applembretemedicamento.presentation

import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.ActivityMainBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.MyDataStore
import com.rubens.applembretemedicamento.framework.singletons.AlarmReceiverSingleton
import com.rubens.applembretemedicamento.framework.viewModels.MainActivityViewModel
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityInterface {


    private val pendingIntentsList = ArrayList<PendingIntent>()


    private lateinit var binding: ActivityMainBinding
    private lateinit var myDataStore: MyDataStore
    private lateinit var viewModel: MainActivityViewModel

    private val alarmReceiver = AlarmReceiverSingleton.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        myDataStore = MyDataStore(applicationContext)
        setContentView(binding.root)
        hideToolbarTitle()
        hideToolbar()
        configNavigation()
        initViewModel()
        onClickListeners()
    }


    private fun configNavigation() {
        val navHostFragment = (supportFragmentManager.findFragmentById(binding.fragmentContainerView.id)) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination
            val isMedicamentosFragment = currentDestination?.id == R.id.medicamentosFragment
            for (i in 0 until binding.bottomNavigationView.menu.size()) {
                val menuItem = binding.bottomNavigationView.menu.getItem(i)
                menuItem.isChecked = false
            }

            item.isChecked = true

            when (item.itemId) {
                R.id.medicamentosFragment -> {
                    hideToolbarTitle()
                    if(isMedicamentosFragment){
                        navController.navigate(R.id.medicamentosFragment)
                    }else{
                        navController.popBackStack()
                    }
                }
                R.id.historicoFragment -> {
                    hideToolbarTitle()
                    navController.navigate(R.id.historicoFragment)
                }
            }

            true
        }
    }



    private fun onClickListeners() {
        binding.btnDeleteMedicamento.setOnClickListener {
            checarSeFragmentoDetalhesEstaAberto()
        }
    }

    private fun checarSeFragmentoDetalhesEstaAberto() {
        val nc = findNavController(R.id.fragmentContainerView)
        verSeDestinoAtualEIgualAFragmentDetalhesMedicamentos(nc)

    }



    private fun verSeDestinoAtualEIgualAFragmentDetalhesMedicamentos(nc: NavController) {
        if (nc.currentDestination?.id == R.id.fragmentDetalhesMedicamentos){
            val fragmentDetalhes = verSeFragmentoEstaCertoParaPegarInstancia()
            fragmentDetalhes?.onDeleteMedicamento()


        }

    }

    private fun verSeFragmentoEstaCertoParaPegarInstancia(): FragmentDetalhesMedicamentos? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val fragmentDetalhes = navHostFragment.childFragmentManager.fragments.first() as? FragmentDetalhesMedicamentos

        if (fragmentDetalhes != null){
            return fragmentDetalhes
        }
        return null
    }

    fun codigoASerExecutadoAoFecharOApp(){


        viewModel.desativarOAlarmeDeTodosMedicamentos()
        Log.d("testedeletepi", "to aqui no onDestroy")

        alarmReceiver.cancelAllAlarms(this.applicationContext)
        Log.d("testealarme", "metodo da main activity de cancelar todos os alarmes ao fechar, foi chamado")


    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()


    }

    override fun hideToolbar() {
        binding.toolbar.visibility = View.GONE
    }

    override fun hideBtnDeleteMedicamento() {
        binding.btnDeleteMedicamento.visibility = View.GONE
    }


    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()

        codigoASerExecutadoAoFecharOApp()

    }

    override fun showToolbar() {
        binding.toolbar.visibility = View.VISIBLE

    }

    override fun hideToolbarTitle() {
        binding.toolbar.title = ""
    }

    override fun showBtnDeleteMedicamento() {
        binding.btnDeleteMedicamento.visibility = View.VISIBLE

    }

    override fun getPendingIntentsList(): ArrayList<PendingIntent> {
        return pendingIntentsList
    }

    override fun clearPendingIntentsList() {
        pendingIntentsList.clear()
    }

    override fun addPendingIntentToPendingIntentsList(pi: PendingIntent) {
        pendingIntentsList.add(pi)
    }


}