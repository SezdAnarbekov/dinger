package domain.interactor

import domain.Domain
import domain.exec.PostExecutionSchedulerProvider
import io.reactivex.Completable
import io.reactivex.observers.DisposableCompletableObserver

abstract class CompletableUseCase(
        private val postExecutionSchedulerProvider: PostExecutionSchedulerProvider)
    : DisposableUseCase(), UseCase<Completable> {
    fun execute(subscriber: DisposableCompletableObserver) {
        assembledSubscriber = buildUseCase()
                .subscribeOn(Domain.useCaseScheduler)
                .observeOn(postExecutionSchedulerProvider.provideScheduler())
                .subscribeWith(subscriber)
    }
}
