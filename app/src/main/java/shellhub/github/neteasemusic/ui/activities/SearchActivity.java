package shellhub.github.neteasemusic.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import shellhub.github.neteasemusic.BaseApp;
import shellhub.github.neteasemusic.R;
import shellhub.github.neteasemusic.model.entities.SearchHistory;
import shellhub.github.neteasemusic.networking.NetEaseMusicService;
import shellhub.github.neteasemusic.presenter.SearchPresenter;
import shellhub.github.neteasemusic.presenter.impl.SearchPresenterImpl;
import shellhub.github.neteasemusic.response.search.SearchResponse;
import shellhub.github.neteasemusic.response.search.artist.ArtistResponse;
import shellhub.github.neteasemusic.response.search.hot.Hot;
import shellhub.github.neteasemusic.response.search.hot.HotResponse;
import shellhub.github.neteasemusic.response.search.video.VideoResponse;
import shellhub.github.neteasemusic.ui.fragments.HotFragment;
import shellhub.github.neteasemusic.ui.fragments.SearchFragment;
import shellhub.github.neteasemusic.util.TagUtils;

public class SearchActivity extends BaseApp implements shellhub.github.neteasemusic.view.SearchView {

    private String TAG = TagUtils.getTag(this.getClass());
    private SearchView searchView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.pb_searching)
    ProgressBar pbSearching;

    @Inject
    NetEaseMusicService mNetEaseMusicService;

    private SearchPresenter mSearchPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDeps().inject(this);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initToolbar();
        setUpMVP();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtils.d(TAG, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LogUtils.d(TAG, newText);
                return true;
            }
        });



        return true;
    }

    @Override
    public void showProgress() {
        pbSearching.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbSearching.setVisibility(View.GONE);
    }

    @Override
    public void showHots(HotResponse hotResponse) {
        LogUtils.d(TAG, hotResponse);
        EventBus.getDefault().post(hotResponse);
    }

    @Override
    public void showHistory(SearchHistory searchHistory) {

    }

    @Override
    public void showSearchResult(SearchResponse searchResponse) {
        LogUtils.d(TAG, searchResponse);
        EventBus.getDefault().post(searchResponse);
    }

    @Override
    public void showVideos(VideoResponse videoResponse) {
        LogUtils.d(TAG, videoResponse);
        EventBus.getDefault().post(videoResponse);
    }

    @Override
    public void showArtist(ArtistResponse artistResponse) {
        LogUtils.d(TAG, artistResponse);
        new Handler().postDelayed(() -> EventBus.getDefault().post(artistResponse), 1000);
    }

    @Override
    public void setUpMVP() {
        getSupportFragmentManager().beginTransaction().replace(R.id.search_container, new HotFragment()).commit();
        mSearchPresenter = new SearchPresenterImpl(this, mNetEaseMusicService);
        mSearchPresenter.searchHot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHotClickEvent(Hot hot) {
        LogUtils.d(TAG, hot);
//        searchView.setQuery(hot.getFirst(), false);
        getSupportFragmentManager().beginTransaction().replace(R.id.search_container, new SearchFragment()).commitAllowingStateLoss();
        mSearchPresenter.search(hot.getFirst());
        mSearchPresenter.searchVideo(hot.getFirst());

        new Handler().postDelayed(() -> mSearchPresenter.searchArtist(hot.getFirst()), 1000);
    }
}