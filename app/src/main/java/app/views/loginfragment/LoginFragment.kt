package app.views.loginfragment

import app.ext.BaseFragment
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentLoginBinding

@LoginFragmentScope
internal class LoginFragment : BaseFragment<LoginFragmentViewModel, FragmentLoginBinding>() {
    override val viewModel: LoginFragmentViewModel
        get() = provideViewModel()
    override val layoutRes: Int
        get() = R.layout.fragment_login

}