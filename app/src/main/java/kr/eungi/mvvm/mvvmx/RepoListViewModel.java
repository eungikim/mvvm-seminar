package kr.eungi.mvvm.mvvmx;

import android.app.Application;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kr.eungi.mvvm.GitHubService;
import kr.eungi.mvvm.SeminarApplication;
import kr.eungi.mvvm.SingleLiveEvent;

public class RepoListViewModel extends AndroidViewModel {

    SingleLiveEvent<Boolean> mProgressEvent = new SingleLiveEvent<>();
    SingleLiveEvent<String> mSnackbarEvent = new SingleLiveEvent<>();
    MutableLiveData<List<GitHubService.RepositoryItem>> mRepositoriesData = new MutableLiveData<>();

    public RepoListViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 지난 1주일간 만들어진 라이브러리의 인기순으로 가져온다
     *
     * @param language 가져올 프로그래밍 언어
     */
    public void loadRepositories(String language) {
        mProgressEvent.postValue(true);
        // 일주일전 날짜의 문자열 지금이 2016-10-27이면 2016-10-20 이라는 문자열을 얻는다
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        String text = DateFormat.format("yyyy-MM-dd", calendar).toString();

        // Retrofit를 이용해 서버에 액세스한다
        final SeminarApplication application = (SeminarApplication) getApplication();
        // 지난 일주일간 만들어지고 언어가 language인 것을 요청으로 전달한다
        Observable<GitHubService.Repositories> observable = application.getGitHubService().listRepos("language:" + language + " " + "created:>" + text);

        // 입출력(IO)용 스레드로 통신하고, 메인스레드에서 결과를 수신하게 한다
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                (repositories) -> {
                    // 로딩이 끝났으므로 진행바를 표시하지 않는다
                    mProgressEvent.postValue(false);
                    // 가져온 아이템을 표시하고자 RecyclerView에 아이템을 설정하고 갱신한다
                    mRepositoriesData.postValue(repositories.items);
                },
                (e) -> {
                    // 통신 실패 시에 호출된다
                    // 여기서는 스낵바를 표시한다(아래에 표시되는 바)
                    mProgressEvent.postValue(false);
                    mSnackbarEvent.postValue("읽어올 수 없습니다.");
                }
        );
    }


    public LiveData<List<GitHubService.RepositoryItem>> getRepositoriesData() {
        return mRepositoriesData;
    }

    public SingleLiveEvent<Boolean> getProgressEvent() {
        return mProgressEvent;
    }

    public SingleLiveEvent<String> getSnackbarEvent() {
        return mSnackbarEvent;
    }
}
