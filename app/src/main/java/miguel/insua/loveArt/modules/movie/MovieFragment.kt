package miguel.insua.loveArt.modules.movie


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.little_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import miguel.insua.loveArt.R
import miguel.insua.loveArt.api.TMDbApiManager
import miguel.insua.loveArt.api.TMDbService
import miguel.insua.loveArt.databinding.FragmentMovieBinding
import miguel.insua.loveArt.model.*
import miguel.insua.loveArt.modules.base.BaseFragment
import miguel.insua.loveArt.modules.home.HomeFragment
import miguel.insua.loveArt.modules.movie.movieImages.MovieImagesFragment
import retrofit2.Response


class MovieFragment: BaseFragment<MovieViewModel, FragmentMovieBinding>(
    MovieViewModel::class.java,
){

    lateinit var media: Media

    private lateinit var adapterGenre: MovieGenreAdapter

    private lateinit var adapterLanguages: MovieLanguagesAdapter

    var movieImages: MovieImages? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            media = requireArguments().getParcelable<Media>("media")!!
            getMovieById(media.id.toString())
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_movie
    }

    override fun viewCreated(view: View?) {
        mBinding.viewModel = viewModel
        viewModel.back = ::back
        viewModel.request = :: request
        initMovieGenreAdapter()
        initMovieLanguagesAdapter()
    }

    private fun back() {
        navigator.navigate(HomeFragment(), false, HomeFragment().LOG_TAG, container = R.id.fragmentContainerHome)
    }

    private fun request() {
        getMovieImages(viewModel.movie!!.id.toString())
        val bundle: Bundle = Bundle()
        bundle.putParcelable("media", media)
        val fragment: MovieImagesFragment = MovieImagesFragment()
        fragment.arguments = bundle
        navigator.navigate(fragment, false, fragment.LOG_TAG, container = R.id.fragmentContainerHome)
    }

    private fun initMovieGenreAdapter() {
        val layoutManager = GridLayoutManager(context, 3)
        recycler_view_movie_genres.layoutManager = layoutManager
        val appContext = requireContext().applicationContext
        adapterGenre = MovieGenreAdapter(appContext)
        recycler_view_movie_genres.adapter = adapterGenre
    }

    private fun initMovieLanguagesAdapter() {
        val layoutManager = GridLayoutManager(context, 4)
        recycler_view_movie_languages.layoutManager = layoutManager
        val appContext = requireContext().applicationContext
        adapterLanguages = MovieLanguagesAdapter(appContext)
        recycler_view_movie_languages.adapter = adapterLanguages
    }


    private fun getMovieById(query: String) {
        val completeQuery: String = "$query?api_key=5451f06f86322e090841b4c2ebab2b7d"
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<Movie> =
                TMDbApiManager().getRetrofit().create(TMDbService::class.java).getMovie("$completeQuery")
            val movieResponse: Movie? = call.body()
            activity?.runOnUiThread(Runnable {
                if (call.isSuccessful) {
                    // Show data
                    viewModel.movie = movieResponse
                    if (viewModel.movie != null) {
                        movie_tittle.text = viewModel.movie!!.tittle
                        movie_release_date.text = viewModel.movie!!.releaseDate

                        Glide.with(requireContext().applicationContext)
                            .load("https://image.tmdb.org/t/p/w500" + viewModel.movie!!.imageUrl)
                            .into(movie_fragment_image)

                        movie_tagline.text = viewModel.movie!!.tagline
                        movie_status.text = viewModel.movie!!.status
                        movie_vote_count.text = viewModel.movie!!.voteCount.toString()

                        movie_rating.rating = viewModel.movie!!.rating!!.toFloat()!!/2

                        movie_overview.text = viewModel.movie!!.overview

                        refreshGenres(viewModel.movie!!.genres)
                        refreshLanguages(viewModel.movie!!.spokenLanguages)
                    }

                } else {
                    showToast(R.string.error_loading_popular.toString())
                }
            })
        }
    }

    private fun getMovieImages(id: String) {
        val completeQuery: String = "$id/images?api_key=5451f06f86322e090841b4c2ebab2b7d"
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<MovieImages> = TMDbApiManager().getRetrofit().create(TMDbService::class.java)
                .getMovieImages("$completeQuery")
            val movieImagesResponse: MovieImages? = call.body()
            activity?.runOnUiThread(Runnable {
                if (call.isSuccessful) {
                    if (movieImagesResponse != null) {
                        movieImages = movieImagesResponse
                        showToast("OK")
                    }
                }
            })
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(activity?.applicationContext, text, Toast.LENGTH_LONG).show()
    }

    private fun refreshGenres(genres: List<MovieGenre>?) {
        if (genres != null) {
            val genresList = mutableListOf<MovieGenre>()
            genresList.addAll(genres)
            adapterGenre.setListData(genresList)
            adapterGenre.notifyDataSetChanged()
        }
    }

    private fun refreshLanguages(languages: List<MovieLanguages>?) {
        if (languages != null) {
            val languagesList = mutableListOf<MovieLanguages>()
            languagesList.addAll(languages)
            adapterLanguages.setListData(languagesList)
            adapterLanguages.notifyDataSetChanged()
        }
    }
}
