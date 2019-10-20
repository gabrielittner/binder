package com.gabrielittner.renderer.connect

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.gabrielittner.renderer.Renderer
import com.gabrielittner.renderer.ViewRenderer
import com.gabrielittner.renderer.create

fun <State, Action> Fragment.connect(
    rendererFactory: ViewRenderer.Factory<State, Action>,
    model: LiveDataViewModel<State, Action>
) {
    val renderer = rendererFactory.create(this)
    return connect(renderer, model)
}

fun <State, Action> FragmentActivity.connect(
    rendererFactory: ViewRenderer.Factory<State, Action>,
    model: LiveDataViewModel<State, Action>
) {
    val renderer = rendererFactory.create(this)
    return connect(renderer, model)
}

fun <State, Action> LifecycleOwner.connect(
    renderer: Renderer<State, Action>,
    model: LiveDataViewModel<State, Action>
) {
    return lifecycle.connect(renderer, model)
}

fun <State, Action> Lifecycle.connect(
    renderer: Renderer<State, Action>,
    model: LiveDataViewModel<State, Action>
) {
    val observer = LiveDataWhileStartedObserver(renderer, model)
    addObserver(observer)
}

private class LiveDataWhileStartedObserver<State, Action>(
    private val renderer: Renderer<State, Action>,
    private val model: LiveDataViewModel<State, Action>
) : WhileStartedObserver() {

    override fun onStart(owner: LifecycleOwner) {
        disposable = renderer.actions.subscribe(
            { model.handleAction(it) },
            { crashApp(exceptionMessage(it, owner), it) }
        )
        model.observe().observe(owner, Observer { renderer.render(it) })
    }

    private fun exceptionMessage(throwable: Throwable, owner: LifecycleOwner): String {
        return "Received ${throwable::class.java.simpleName} from " +
                "${renderer::class.java.simpleName} sending it to ${model::class.java.simpleName} " +
                "while lifecycle state is ${owner.lifecycle.currentState}"
    }
}