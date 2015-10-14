package eu.kanade.mangafeed.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.kanade.mangafeed.R;
import eu.kanade.mangafeed.presenter.CatalogueListPresenter;
import eu.kanade.mangafeed.view.CatalogueListView;
import eu.kanade.mangafeed.widget.EndlessScrollListener;
import uk.co.ribot.easyadapter.EasyAdapter;

public class CatalogueListActivity extends BaseActivity implements CatalogueListView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.gridView)
    GridView manga_list;

    @Bind(R.id.progress)
    ProgressBar progress;

    @Bind(R.id.progress_grid)
    ProgressBar progress_grid;

    private CatalogueListPresenter presenter;

    private EndlessScrollListener scrollListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue_list);
        ButterKnife.bind(this);

        setupToolbar(toolbar);

        presenter = new CatalogueListPresenter(this);
        presenter.initialize();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroySubscriptions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalogue_list, menu);
        initializeSearch(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initializeSearch(Menu menu) {
        final SearchView sv = (SearchView) menu.findItem(R.id.action_search).getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onQueryTextChange(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onQueryTextChange(newText);
                return true;
            }
        });
    }

    // CatalogueListView

    @Override
    public void setSourceTitle(String title) {
        setToolbarTitle(title);
    }

    @Override
    public void setAdapter(EasyAdapter adapter) {
        manga_list.setAdapter(adapter);
    }

    @Override
    public void setScrollListener() {
        scrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                presenter.loadMoreMangas(page);
                return true;
            }
        };

        manga_list.setOnScrollListener(scrollListener);
    }

    @Override
    public void resetScrollListener() {
        scrollListener.resetScroll();
    }

    @Override
    public void showProgressBar() {
        progress.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void showGridProgressBar() {
        progress_grid.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progress.setVisibility(ProgressBar.GONE);
        progress_grid.setVisibility(ProgressBar.GONE);
    }

    @Override
    public ImageView getImageView(int position) {
        View v = manga_list.getChildAt(position -
                manga_list.getFirstVisiblePosition());

        if(v == null)
            return null;

        return (ImageView) v.findViewById(R.id.catalogue_thumbnail);
    }
}
