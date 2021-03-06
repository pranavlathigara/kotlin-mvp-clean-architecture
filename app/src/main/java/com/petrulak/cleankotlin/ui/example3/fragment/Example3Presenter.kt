package com.petrulak.cleankotlin.ui.example3.fragment

import android.util.Log
import com.petrulak.cleankotlin.di.scope.ViewScope
import com.petrulak.cleankotlin.domain.interactor.WeatherRemoteUseCase
import com.petrulak.cleankotlin.domain.model.Weather
import com.petrulak.cleankotlin.platform.bus.data.DataBus
import com.petrulak.cleankotlin.platform.bus.event.EventBus
import com.petrulak.cleankotlin.platform.bus.event.events.BaseEvent
import com.petrulak.cleankotlin.platform.bus.event.events.FragmentSyncEvent
import com.petrulak.cleankotlin.ui.example3.fragment.Example3Contract.View
import dagger.internal.Preconditions.checkNotNull
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

@ViewScope
class Example3Presenter
@Inject
constructor(private val weatherRemote: WeatherRemoteUseCase, private val dataBus: DataBus, private val eventBus: EventBus) : Example3Contract.Presenter {

    private var view: View? = null

    private val disposables = CompositeDisposable()
    private val dataDisposable = CompositeDisposable()

    override fun attachView(view: View) {
        this.view = checkNotNull(view)
    }

    override fun start() {
        weatherRemote.execute({ onSuccess(it) }, { onError(it) }, "London,uk")
        subscribeToData()
        subscribeToFragmentSyncEvents()
        //  subscribeToDummyEvents()
    }

    private fun subscribeToData() {
        val disposable = dataBus.weatherDataBus.flowable.subscribe({ view?.showWeather(it) })
        dataDisposable.add(disposable)
    }

    private fun subscribeToFragmentSyncEvents() {
        val disposable = eventBus.fragmentSyncEvent.flowable.subscribe({ processEvent(it) })
        disposables.add(disposable)
    }

    /**
     * You can consume data/payload  which is included in the event.
     */
    private fun subscribeToDummyEvents() {
        val disposable = eventBus.weatherDummyEvent.flowable.subscribe({ processDummyEvent(it) })
        disposables.add(disposable)
    }

    private fun processDummyEvent(event: BaseEvent<Weather>) {
        //payload can be null, we have to perform null check
        event.payload?.let {
            val payload = it
            Log.e("Hello", "This is payload: $payload")
        }
    }

    private fun processEvent(event: BaseEvent<Any>) {
        when (event.eventType) {
            FragmentSyncEvent.ACTION_SYNC_OFF -> dataDisposable.clear()
            FragmentSyncEvent.ACTION_SYNC_ON  -> subscribeToData()
        }
    }

    override fun stop() {
        weatherRemote.dispose()
        dataDisposable.clear()
        disposables.clear()
    }

    override fun refresh() {
        weatherRemote.execute({ onSuccess(it) }, { onError(it) }, "London,uk")
    }

    private fun onSuccess(weather: Weather) {
        //Changing visibility value manually to see changes in the UI
        val modified = Weather(weather.id, weather.name, Random().nextInt(80 - 65) + 65)
        dataBus.weatherDataBus.emmit(modified)
        view?.showWeather(modified)
    }

    private fun onError(t: Throwable) {

    }

}
