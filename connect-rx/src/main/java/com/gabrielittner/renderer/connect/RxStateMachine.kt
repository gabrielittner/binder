package com.gabrielittner.renderer.connect

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.gabrielittner.renderer.Renderer
import io.reactivex.Observable

@Deprecated("Use RxLiveDataStateMachine instead")
typealias RxViewModel<State, Action> = RxStateMachine<State, Action>

@Deprecated("Use RxLiveDataStateMachine instead")
interface RxStateMachine<State, Action> {
    fun observe(events: Observable<Action>): Observable<State>
}

@Deprecated("Use RxLiveDataViewModel instead")
fun <State : Any, Action : Any> Fragment.connect(
    renderer: Renderer<State, Action>,
    model: RxStateMachine<State, Action>
) {
    viewLifecycleOwner.connect(renderer, model)
}

@Deprecated("Use RxLiveDataViewModel instead")
fun <State : Any, Action : Any> LifecycleOwner.connect(
    renderer: Renderer<State, Action>,
    model: RxStateMachine<State, Action>
) {
    lifecycle.addObserver(RxWhileStartedObserver(renderer, model))
}
