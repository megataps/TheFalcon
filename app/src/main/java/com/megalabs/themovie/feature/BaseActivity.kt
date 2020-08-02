package com.megalabs.themovie.feature

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable

open class BaseActivity: AppCompatActivity() {

    protected val disposables by lazy { CompositeDisposable() }

}