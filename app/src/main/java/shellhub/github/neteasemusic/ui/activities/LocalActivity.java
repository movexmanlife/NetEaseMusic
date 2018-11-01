package shellhub.github.neteasemusic.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import shellhub.github.neteasemusic.R;
import shellhub.github.neteasemusic.adapter.LocalCategoryPagerAdapter;
import shellhub.github.neteasemusic.model.entities.Artist;
import shellhub.github.neteasemusic.model.entities.ArtistEvent;
import shellhub.github.neteasemusic.model.entities.Single;
import shellhub.github.neteasemusic.model.entities.SingleEvent;
import shellhub.github.neteasemusic.presenter.LocalPresenter;
import shellhub.github.neteasemusic.presenter.impl.LocalPresenterImpl;
import shellhub.github.neteasemusic.util.TagUtils;
import shellhub.github.neteasemusic.view.LocalView;

public class LocalActivity extends AppCompatActivity implements LocalView {

    private String TAG = TagUtils.getTag(this.getClass());
    @BindView(R.id.tl_local_category)
    TabLayout tlLocalCategory;

    @BindView(R.id.vp_local_files)
    ViewPager vpLocalFiles;

    private LocalPresenter mLocalPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        ButterKnife.bind(this);
        vpLocalFiles.setAdapter(new LocalCategoryPagerAdapter(getSupportFragmentManager(), this));
        LogUtils.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        tlLocalCategory.setupWithViewPager(vpLocalFiles);
        setUpMVP();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_local_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getResources().getString(R.string.searching_local_file));

        searchView.setOnCloseListener(null);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtils.d(TAG, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LogUtils.d(TAG, newText);
                mLocalPresenter.query(newText);
                return false;
            }
        });

        return true;

    }

    @Override
    public void setUpMVP() {
        mLocalPresenter = new LocalPresenterImpl(this);
        mLocalPresenter.load();
    }

    @Override
    public void loadSingle(List<Single> singles) {
        LogUtils.d(TAG, singles);
        EventBus.getDefault().post(new SingleEvent(singles));
    }

    @Override
    public void loadArtist(List<Artist> artists) {
        LogUtils.d(TAG, artists);
        EventBus.getDefault().post(new ArtistEvent(artists));
    }
}