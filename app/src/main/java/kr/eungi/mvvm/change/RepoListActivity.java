package kr.eungi.mvvm.change;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kr.eungi.mvvm.Constant;
import kr.eungi.mvvm.GitHubService;
import kr.eungi.mvvm.R;
import kr.eungi.mvvm.RepositoryAdapter;
import kr.eungi.mvvm.SeminarApplication;

/**
 * 리포지토리 목록을 표시하는 Activity
 */
public class RepoListActivity extends AppCompatActivity implements RepositoryAdapter.OnRepositoryItemClickListener {
    private static final String TAG = Constant.TAG + RepoListActivity.class.getSimpleName();
    private static final String[] SEARCH_LANGUAGE = new String[]
            {"java", "objective-c", "swift", "groovy", "python", "ruby", "c"};

    private Spinner languageSpinner;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    private RepoListViewModel mViewModel;
    private RepositoryAdapter repositoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_list);
        Log.d(TAG, "onCreate() called");

        mViewModel = new ViewModelProvider(this).get(RepoListViewModel.class);

        // View를 설정한다
        setupViews();
        setupObserver();
    }

    private void setupObserver() {
        mViewModel.getmProgressBarEvent().observe(this, this::showProgressbar);
        mViewModel.getmSnackbarEvent().observe(this, this::showSnackbar);
        mViewModel.getmRepositoryesData().observe(this,
                repositories -> repositoryAdapter.setItemsAndRefresh(repositories));
    }

    private void showProgressbar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showSnackbar(String msg) {
        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * 목록 등 화면 요소를 만든다
     */
    private void setupViews() {
        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Recycler View
        RecyclerView recyclerView = findViewById(R.id.recycler_repos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repositoryAdapter = new RepositoryAdapter(this);
        recyclerView.setAdapter(repositoryAdapter);

        // ProgressBar
        progressBar = findViewById(R.id.progress_bar);

        // SnackBar 표시에 이용한다
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        // Spinner
        languageSpinner = findViewById(R.id.language_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.addAll(SEARCH_LANGUAGE);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 선택시 뿐만 아니라 처음에도 호출된다
                Log.d(TAG, "languageSpinner selected: " + position);
                String language = (String) languageSpinner.getItemAtPosition(position);
                mViewModel.loadRepositories(language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /**
     * 상세화면을 표시한다
     *
     * @see RepositoryAdapter.OnRepositoryItemClickListener#onRepositoryItemClick
     */
    @Override
    public void onRepositoryItemClick(GitHubService.RepositoryItem item) {
        Toast.makeText(this, item.name + " 의 Detail 화면 표시", Toast.LENGTH_SHORT).show();
//        DetailActivity.start(this, item.full_name);
    }
}

