package com.asazonov.infinitemovielist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements IViewer {

    public static final int SPACE = 40;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.recycler_bottom)
    RecyclerView mPreviewRecycler;

    private AwesomeLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private PreviewAdapter mPreviewAdapter;

    private Presenter mPresenter;
    boolean minus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new Presenter();
        mPresenter.bind(this);

        mLayoutManager = new AwesomeLayoutManager();
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MoviesAdapter(new ArrayList<MovieViewModel>(), onClickListener, new ICenterChecker() {
            @Override
            public boolean isCenterView(int pos) {
                return mLayoutManager.mCenterPos == pos;
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mLayoutManager.scrollToCenter(mRecyclerView);
                    updateSelectedMovie();
                }
            }
        });

        LinearLayoutPagerManager linearLayoutManager = new LinearLayoutPagerManager(this, LinearLayoutManager.HORIZONTAL, false, 3, SPACE);
        mPreviewRecycler.setHasFixedSize(true);
        mPreviewRecycler.addItemDecoration(new SpaceItemDecoration(SPACE));
        mPreviewAdapter = new PreviewAdapter(new ArrayList<PreviewViewModel>());
        mPreviewRecycler.setAdapter(mPreviewAdapter);
        mPreviewRecycler.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.loadPage(0);
    }

    private void updateSelectedMovie() {
        mPresenter.onMovieSelected(mLayoutManager.mCenterPos);
    }

    @Override
    public void publish(ArrayList<MovieViewModel> items) {
        int curSize = mAdapter.moviesList.size();
        if (minus) {
            mAdapter.moviesList.addAll(0, items);
            mAdapter.notifyItemRangeInserted(0, items.size() - 1);
        } else {
            mAdapter.moviesList.addAll(items);
            mAdapter.notifyItemRangeInserted(curSize, mAdapter.moviesList.size() - 1);
        }

        mLayoutManager.scrollToCenter(mRecyclerView);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                updateSelectedMovie();
            }
        });

    }

    @Override
    public void publishPreviews(ArrayList<PreviewViewModel> items) {
        mPreviewAdapter.previewList.clear();
        mPreviewAdapter.previewList.addAll(items);
        mPreviewAdapter.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            title = ButterKnife.findById(view, R.id.title);
            imageView = ButterKnife.findById(view, R.id.image);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewHolder holder = (ViewHolder) v.getTag();
            mRecyclerView.smoothScrollToPosition(holder.getAdapterPosition());
        }
    };

    interface ICenterChecker {
        boolean isCenterView(int pos);
    }

    public static class MoviesAdapter extends RecyclerView.Adapter<ViewHolder> {
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(6)
                .oval(false)
                .build();

        private final View.OnClickListener listener;
        private final ICenterChecker centerChecker;
        private List<MovieViewModel> moviesList;

        public MoviesAdapter(List<MovieViewModel> moviesList, View.OnClickListener onClickListener, ICenterChecker iCenterChecker) {
            this.moviesList = moviesList;
            this.listener = onClickListener;
            this.centerChecker = iCenterChecker;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            itemView.setTag(viewHolder);
            itemView.setOnClickListener(listener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            MovieViewModel movie = moviesList.get(position);
            holder.title.setText(movie.getTitle());

            Picasso.with(holder.imageView.getContext())
                    .load(movie.getImage())
                    .transform(transformation)
                    .into(holder.imageView);

            if (centerChecker.isCenterView(position)) {
                holder.itemView.setBackgroundResource(R.drawable.selected);
            } else {
                holder.itemView.setBackground(null);
            }
        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }
    }

    public static class PreviewAdapter extends RecyclerView.Adapter<ViewHolder> {
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(20)
                .oval(false)
                .build();

        private List<PreviewViewModel> previewList;

        public PreviewAdapter(List<PreviewViewModel> previewList) {
            this.previewList = previewList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.preview_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            itemView.setTag(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            PreviewViewModel movie = previewList.get(position);
            holder.title.setText(movie.getTitle());

            Picasso.with(holder.imageView.getContext())
                    .load(movie.getImage())
                    .transform(transformation)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return previewList.size();
        }
    }
}
