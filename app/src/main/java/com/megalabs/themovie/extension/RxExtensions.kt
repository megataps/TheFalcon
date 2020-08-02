package com.megalabs.themovie.extension

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.with(): Single<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun Completable.with(): Completable = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())