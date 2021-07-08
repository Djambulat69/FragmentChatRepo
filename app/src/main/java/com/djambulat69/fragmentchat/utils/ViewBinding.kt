package com.djambulat69.fragmentchat.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewBindingDelegate<F : Fragment, VB : ViewBinding>(fragment: F, private val inflateViewBinding: () -> VB) :
    ReadOnlyProperty<F, VB>, LifecycleObserver {

    private var binding: VB? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { owner: LifecycleOwner? ->
            owner?.let {
                owner.lifecycle.addObserver(this)
            }
        }
    }

    override fun getValue(thisRef: F, property: KProperty<*>): VB {
        binding = binding ?: inflateViewBinding()
        return binding!!
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyBinding() {
        binding = null
    }

}

fun <F : Fragment, VB : ViewBinding> F.viewBinding(inflateViewBinding: () -> VB): ViewBindingDelegate<F, VB> {
    return ViewBindingDelegate(this, inflateViewBinding)
}
