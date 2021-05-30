package miguel.insua.loveArt.modules.main


import android.os.Bundle
import miguel.insua.loveArt.R
import miguel.insua.loveArt.databinding.ActivityMainBinding
import miguel.insua.loveArt.modules.base.BaseActivity
import miguel.insua.loveArt.modules.start.StartFragment


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(
    MainViewModel::class.java
) {
    override fun initViewModel(viewModel: MainViewModel) {
        binding.viewModel = viewModel
    }

    override fun getLayoutRes() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator.addFragment(StartFragment(), container = R.id.fragmentContainerMain)
    }

}
