package app.views.mvvm

import app.ext.BaseViewModel
import app.views.dagger.LaunchActivityScope
import javax.inject.Inject

@LaunchActivityScope
internal class LaunchActivityModel @Inject constructor() : BaseViewModel()