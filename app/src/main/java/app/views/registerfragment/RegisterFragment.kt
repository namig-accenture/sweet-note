package app.views.registerfragment

import app.ext.BaseFragment
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentRegisterBinding

@RegisterFragmentScope
internal class RegisterFragment : BaseFragment<RegisterFragmentViewModel, FragmentRegisterBinding>() {
    override val viewModel: RegisterFragmentViewModel
        get() = provideViewModel()
    override val layoutRes: Int
        get() = R.layout.fragment_register
}