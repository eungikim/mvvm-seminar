package kr.eungi.mvvm.mvvmx;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kr.eungi.mvvm.GitHubService;
import kr.eungi.mvvm.R;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepoViewHolder> {
    private final OnRepositoryItemClickListener onRepositoryItemClickListener;
    private Context context;
    private List<GitHubService.RepositoryItem> items;

    /**
     * 뷰를 저장해 둘 클래스
     */
    static class RepoViewHolder extends RecyclerView.ViewHolder {
        private final TextView repoName;
        private final TextView repoDetail;
        private final ImageView repoImage;
        private final TextView starCount;

        public RepoViewHolder(View itemView) {
            super(itemView);
            repoName = itemView.findViewById(R.id.repo_name);
            repoDetail = itemView.findViewById(R.id.repo_detail);
            repoImage = itemView.findViewById(R.id.repo_image);
            starCount = itemView.findViewById(R.id.repo_star);
        }

        private void bindViewHolder(GitHubService.RepositoryItem item, View.OnClickListener listener) {
            // 뷰가 클릭되면 클릭된 아이템을 Listener에게 알린다
            itemView.setOnClickListener(listener);
            repoName.setText(item.name);
            repoDetail.setText(item.description);
            starCount.setText(item.stargazers_count);
            // 이미지는 Glide라는 라이브러리로 데이터를 설정한다
            Glide.with(itemView.getContext())
                    .asBitmap()
                    .load(item.owner.avatar_url)
                    .centerCrop().into(new BitmapImageViewTarget(repoImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    // 이미지를 동그랗게 만든다
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(itemView.getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    repoImage.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }

    public RepositoryAdapter(OnRepositoryItemClickListener onRepositoryItemClickListener) {
        this.onRepositoryItemClickListener = onRepositoryItemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.context = recyclerView.getContext();
    }

    /**
     * 리포지토리의 데이터를 설정해서 갱신한다
     */
    public void setItemsAndRefresh(List<GitHubService.RepositoryItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public GitHubService.RepositoryItem getItemAt(int position) {
        return items.get(position);
    }

    /**
     * RecyclerView의 아이템 뷰 생성과 뷰를 유지할 ViewHolder를 생성
     */
    @NotNull
    @Override
    public RepoViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.repo_item, parent, false);
        return new RepoViewHolder(view);
    }

    /**
     * onCreateViewHolder로 만든 ViewHolder의 뷰에
     * setItemsAndRefresh(items)으로 설정된 데이터를 넣는다
     */
    @Override
    public void onBindViewHolder(final RepoViewHolder holder, final int position) {
        final GitHubService.RepositoryItem item = getItemAt(position);
        holder.bindViewHolder(item, v -> onRepositoryItemClickListener.onRepositoryItemClick(item));
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    interface OnRepositoryItemClickListener {
        /**
         * 리포지토리의 아이템이 탭되면 호출된다
         */
        void onRepositoryItemClick(GitHubService.RepositoryItem item);
    }

}
