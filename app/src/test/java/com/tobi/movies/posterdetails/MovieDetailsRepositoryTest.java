package com.tobi.movies.posterdetails;

import android.support.annotation.NonNull;

import com.tobi.movies.Converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MovieDetailsRepositoryTest {

    private MovieDetailsApiDatasource apiDatasource = mock(MovieDetailsApiDatasource.class);
    private Converter<ApiMovieDetails, MovieDetails> conveter = mock(ApiMovieDetailsConverter.class);

    private final MovieDetailsRepository repository = new MovieDetailsRepository(
            apiDatasource,
            conveter
    );

    @Before
    public void setUp() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ApiMovieDetails apiMovieDetails = ((ApiMovieDetails) invocation.getArguments()[0]);

                return MovieDetails.builder().
                        movieId(apiMovieDetails.movieId).
                        originalTitle(apiMovieDetails.originalTitle).
                        posterPath(apiMovieDetails.posterPath).
                        overview(apiMovieDetails.overview).
                        releaseDate(apiMovieDetails.releaseDate).build();
            }
        }).when(conveter).convert(any(ApiMovieDetails.class));
    }

    @Test
    public void shouldReturnMovieDetailsForId() throws Exception {
        long movieId = 1L;
        String imagePath = "path";
        String overview = "overview";
        String title = "title";
        String releaseDate = "date";
        ApiMovieDetails apiMovieDetails = createApiMovieDetails(movieId, imagePath, overview, title, releaseDate);
        when(apiDatasource.getMovieDetails(movieId)).thenReturn(Observable.just(apiMovieDetails));

        Observable<MovieDetails> detailsObservable = repository.getMovieDetails(movieId);

        TestSubscriber<MovieDetails> testSubscriber = new TestSubscriber<>();
        detailsObservable.subscribe(testSubscriber);
        MovieDetails expectedMovieDetails = MovieDetails.builder().
                movieId(movieId).
                posterPath(imagePath).
                originalTitle(title).
                releaseDate(releaseDate).
                overview(overview).
                build();
        testSubscriber.assertValue(expectedMovieDetails);
    }

    @NonNull
    private ApiMovieDetails createApiMovieDetails(long movieId, String imagePath, String overview, String title, String releaseDate) {
        ApiMovieDetails apiMovieDetails = new ApiMovieDetails();
        apiMovieDetails.movieId = movieId;
        apiMovieDetails.posterPath = imagePath;
        apiMovieDetails.overview = overview;
        apiMovieDetails.originalTitle = title;
        apiMovieDetails.releaseDate = releaseDate;
        return apiMovieDetails;
    }
}
