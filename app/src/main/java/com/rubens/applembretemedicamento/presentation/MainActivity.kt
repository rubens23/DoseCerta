package com.rubens.applembretemedicamento.presentation

import android.app.PendingIntent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.ActivityMainBinding
import com.rubens.applembretemedicamento.framework.ApplicationContextProvider
import com.rubens.applembretemedicamento.framework.ApplicationContextProvider.setApplicationContext
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.broadcastreceivers.OnRestartDeviceReceiver
import com.rubens.applembretemedicamento.framework.data.datastore.DataStoreTheme
import com.rubens.applembretemedicamento.framework.data.datastore.interfaces.ThemeDataStoreInterface
import com.rubens.applembretemedicamento.framework.singletons.AlarmReceiverSingleton
import com.rubens.applembretemedicamento.framework.viewModels.ActivityHostAndFragmentConfikguracoesSharedViewModel
import com.rubens.applembretemedicamento.framework.viewModels.ActivityHostAndFragmentListaMedicamentosSharedViewModel
import com.rubens.applembretemedicamento.framework.viewModels.MainActivityViewModel
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentListaMedicamentosInterface
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity(), MainActivityInterface{



    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val pendingIntentsList = ArrayList<PendingIntent>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var sharedViewModel: ActivityHostAndFragmentListaMedicamentosSharedViewModel
    private lateinit var configuracoesSharedViewModel: ActivityHostAndFragmentConfikguracoesSharedViewModel
    private var temaAzulReferenceId: Int = R.style.CustomThemeAzul
    private var temaVermelhoReferenceId: Int = R.style.Theme_AppLembreteMedicamento
    private var tema = ""

    private lateinit var fragmentListaMedicamentosInterface: FragmentListaMedicamentosInterface




    private lateinit var themeDataStore: ThemeDataStoreInterface

    private var theme: Int = R.style.Theme_AppLembreteMedicamento


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        launchScopeToLoadTheme()


        setContentView(binding.root)

        Log.d("callobserver", "metodo que vai esconder a toolbar foi chamado")

        hideToolbarTitle()
        hideToolbar()
        configNavigation()
        initViewModel()
        onClickListeners()
        setApplicationContextt()




    }


    private fun setApplicationContextt() {
        ApplicationContextProvider.setApplicationContext(this)
    }


    private fun injetarInstanciaDaInterfaceDaMainActivity(fragmentLista: FragmentListaMedicamentos) {

        fragmentLista.setMainActivityInterface(this)




    }




    private fun launchScopeToLoadTheme(){
        lifecycleScope.launch {
            themeDataStore = DataStoreTheme(applicationContext)
            val intDataStore = intPreferencesKey("theme_key")
            val theme = themeDataStore.getThemeChosenByUser(intDataStore)
            withContext(Dispatchers.Main){
                initConfiguracoesSharedViewModel()
                setTheme(theme)
                configBottomNavigationTheme(theme)



            }

        }
    }

    private fun initConfiguracoesSharedViewModel() {
        configuracoesSharedViewModel = ViewModelProvider(this)[ActivityHostAndFragmentConfikguracoesSharedViewModel::class.java]
        initConfiguracoesObserver()

    }

    private fun initConfiguracoesObserver() {
        configuracoesSharedViewModel.getMudouTema.observe(this){
                mudou->
            mostrarToolbar(mudou)
        }
    }

    private fun configBottomNavigationTheme(theme: Int) {
        if(theme == temaAzulReferenceId){
            //tema azul
            tema = theme.toString()
            configuracoesSharedViewModel.atualizarTema("Azul")
            if(this::fragmentListaMedicamentosInterface.isInitialized){
                changeFloatingActionButtonColor(R.color.blue)

            }

            changeBottomNavigationToBlueTheme()
        }
        if(theme == temaVermelhoReferenceId){
            //tema vermelho
            tema = theme.toString()
            configuracoesSharedViewModel.atualizarTema("Vermelho")
            if(this::fragmentListaMedicamentosInterface.isInitialized){
                changeFloatingActionButtonColor(R.color.rosa_salmao)
            }

            changeBottomNavigationToRedTheme()

        }
    }

    private fun changeFloatingActionButtonColor(color: Int) {
        fragmentListaMedicamentosInterface.changeFloatingActionButtonColor(color)
    }

    private fun changeFloatingActionButtonTheme(){
        if(tema == temaAzulReferenceId.toString()){
            //tema azul
            tema = theme.toString()

            changeFloatingActionButtonColor(R.color.blue)
        }
        if(tema == temaVermelhoReferenceId.toString()){
            //tema vermelho
            Log.d("instanciafragmento", "eu to aqui no tema vermelho")

            changeFloatingActionButtonColor(R.color.rosa_salmao)

        }

    }


    private fun changeBottomNavigationTheme(){
        if(tema == temaAzulReferenceId.toString()){
            //tema azul
            changeBottomNavigationToBlueTheme()
        }
        if(tema == temaVermelhoReferenceId.toString()){
            //tema vermelho
            changeBottomNavigationToRedTheme()

        }
    }

    private fun changeBottomNavigationToRedTheme() {
        changeBottomNavigationIconTintToRed()
        changeBottomNavigationTextColorToRed()
    }

    private fun changeBottomNavigationToBlueTheme() {
        changeBottomNavigationIconTintToBlue()
        changeBottomNavigationTextColorToBlue()
    }


    private fun configNavigation() {
        navHostFragment = (supportFragmentManager.findFragmentById(binding.fragmentContainerView.id)) as MainNavHostFragment
        navController = navHostFragment.navController

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

                    changeBottomNavigationTheme()
                    if(isMedicamentosFragment){
                        if(navController.currentDestination?.id != R.id.medicamentosFragment){
                            navController.navigate(R.id.medicamentosFragment)

                        }
                    }else{
                        navController.popBackStack()
                    }
                }
                R.id.historicoFragment -> {
                    hideToolbarTitle()
                    changeBottomNavigationTheme()
                    if(navController.currentDestination?.id != R.id.historicoFragment){
                        navController.navigate(R.id.historicoFragment)

                    }
                }
            }

            true
        }

        initFragmentListaMedicamentosInterface(navController)






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

    private fun initFragmentListaMedicamentosInterface(navController: NavController) {
        verSeDestinoAtualEIgualAFragmentListaMedicamentos(navController)
    }

    private fun verSeDestinoAtualEIgualAFragmentListaMedicamentos(nc: NavController) {
        if (nc.currentDestination?.id == R.id.medicamentosFragment){
            val medicamentosFragment = verSeMedicamentosFragmentEstaCertoParaPegarInstancia()
            if (medicamentosFragment != null) {
                fragmentListaMedicamentosInterface = medicamentosFragment
                changeFloatingActionButtonTheme()
            }


        }

    }

    private fun verSeMedicamentosFragmentEstaCertoParaPegarInstancia(): FragmentListaMedicamentos? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val fragmentMedicamentos = navHostFragment.childFragmentManager.fragments.first() as? FragmentListaMedicamentos

        if (fragmentMedicamentos != null){
            return fragmentMedicamentos
        }
        return null
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



    private fun initViewModel() {
        sharedViewModel = ViewModelProvider(this)[ActivityHostAndFragmentListaMedicamentosSharedViewModel::class.java]
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        initCollectors()
    }



    private fun mostrarToolbar(mudou: Boolean) {
        Log.d("callobserver", "observer que vai chamar o metodo que vai mostrar a toolbar foi chamado $mudou")
        if(mudou){
            showToolbar()
            hideToolbarTitle()
            hideBtnDeleteMedicamento()
        }
    }

    private fun initCollectors() {
        lifecycleScope.launchWhenStarted {
            sharedViewModel.getFragmentListaInstance.collectLatest {
            fragmentLista->
                injetarInstanciaDaInterfaceDaMainActivity(fragmentLista)
            }
        }

    }

    override fun onResume() {
        super.onResume()



    }

    override fun hideToolbar() {
        Log.d("callobserver", "encondi a toolbar")
        binding.toolbar.visibility = View.GONE
    }

    override fun hideBtnDeleteMedicamento() {
        binding.btnDeleteMedicamento.visibility = View.GONE
    }

    override fun changeThemeToBlueTheme() {
        lifecycleScope.launch {
            themeDataStore = DataStoreTheme(applicationContext)
            val intDataStore = intPreferencesKey("theme_key")
            themeDataStore.passThemeToUserChosenTheme(intDataStore, R.style.CustomThemeAzul)
            withContext(Dispatchers.Main){
                recreate()




            }

        }

    }

    private fun changeBottomNavigationTextColorToBlue() {
        binding.bottomNavigationView.itemTextColor = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
            intArrayOf(ContextCompat.getColor(this@MainActivity, R.color.blue), ContextCompat.getColor(this@MainActivity, R.color.light_gray)))


    }

    private fun changeBottomNavigationIconTintToBlue() {
        binding.bottomNavigationView.itemIconTintList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
            intArrayOf(ContextCompat.getColor(this@MainActivity, R.color.blue), ContextCompat.getColor(this@MainActivity, R.color.light_gray)))
    }

    private fun changeBottomNavigationTextColorToRed() {
        binding.bottomNavigationView.itemTextColor = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
            intArrayOf(ContextCompat.getColor(this@MainActivity, R.color.rosa_salmao), ContextCompat.getColor(this@MainActivity, R.color.light_gray)))


    }

    private fun changeBottomNavigationIconTintToRed() {
        binding.bottomNavigationView.itemIconTintList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
            intArrayOf(ContextCompat.getColor(this@MainActivity, R.color.rosa_salmao), ContextCompat.getColor(this@MainActivity, R.color.light_gray)))

    }

    override fun changeThemeToRedTheme() {
        lifecycleScope.launch {
            themeDataStore = DataStoreTheme(applicationContext)
            val intDataStore = intPreferencesKey("theme_key")
            themeDataStore.passThemeToUserChosenTheme(intDataStore, R.style.Theme_AppLembreteMedicamento)
            withContext(Dispatchers.Main){
                recreate()






            }

        }
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