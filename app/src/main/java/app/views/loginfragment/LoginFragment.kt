package app.views.loginfragment

import app.ext.BaseFragment
import com.example.namigtahmazli.sweetnote.BR
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentLoginBinding
import org.koin.android.architecture.ext.viewModel

internal class LoginFragment : BaseFragment<LoginFragmentViewModel, FragmentLoginBinding>() {
    private val loginViewModel by viewModel<LoginFragmentViewModel>()
    override val layoutRes: Int
        get() = R.layout.fragment_login
    override val viewModel: LoginFragmentViewModel
        get() = loginViewModel
    override val viewModelId: Int
        get() = BR.viewModel
}