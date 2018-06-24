package app.vineshbuilds.githubpullviewer.presenter

import app.vineshbuilds.githubpullviewer.model.PR
import app.vineshbuilds.githubpullviewer.rest.RestClient
import app.vineshbuilds.githubpullviewer.view.MainViewStub
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class PresenterImpl(val view: MainViewStub) : PresenterStub {

    companion object {
        /**
         *Verifies if the given path is in the valid format
         * @return true if valid path else false
         */
        private val regex = Regex("[^\\s]+/[^\\s]+")

        fun verify(path: String?) = regex.matches(path ?: "")
        //private fun verify(path: String?) = path?.isNotBlank() ?: false
    }

    /**
     * grabs the pull requests from github
     */
    override fun getPulls(path: String) {
        if (verify(path)) {
            view.hideError()
            view.showLoading()
            RestClient.apis.getOpenPulls(path)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<PR>>() {
                        override fun onSuccess(pullRequests: List<PR>) {
                            view.hideLoading()
                            if (pullRequests.isNotEmpty()) view.onResult(pullRequests) else view.showError("Empty PRs!")
                        }

                        override fun onError(e: Throwable) {
                            view.hideLoading()
                            view.showError(e.message ?: "Something went wrong :(")
                        }

                    })
        } else {
            view.showError("Incorrect Format")
        }
    }
}