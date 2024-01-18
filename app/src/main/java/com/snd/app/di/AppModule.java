package com.snd.app.di;


import com.snd.app.domain.tree.vo.TreeIntegratedVO;
import com.snd.app.repository.company.CompanyRepository;
import com.snd.app.repository.company.CompanyUseCase;
import com.snd.app.repository.config.AosConfigRepository;
import com.snd.app.repository.config.AosConfigUseCase;
import com.snd.app.repository.login.LoginRepository;
import com.snd.app.repository.login.LoginUseCase;
import com.snd.app.repository.project.ProjectRepository;
import com.snd.app.repository.project.ProjectUseCase;
import com.snd.app.repository.tree.TreeRepository;
import com.snd.app.repository.tree.TreeUseCase;
import com.snd.app.repository.tree.treeHashtag.TreeHashtagRepository;
import com.snd.app.repository.tree.treeHashtag.TreeHashtagUseCase;
import com.snd.app.repository.tree.treeManagement.TreeManagementRepository;
import com.snd.app.repository.tree.treeManagement.TreeManagementUseCase;
import com.snd.app.repository.user.UserRepository;
import com.snd.app.repository.user.UserUseCase;
import com.snd.app.repository.tree.treeDataList.TreeDataListRepository;
import com.snd.app.repository.tree.treeDataList.TreeDataListUseCase;
import com.snd.app.ui.home.HomeViewModel;
import com.snd.app.repository.tree.treeImage.TreeImageRepository;
import com.snd.app.repository.tree.treeImage.TreeImageUseCase;
import com.snd.app.ui.write.tree.RegistTreeBasicInfoViewModel;


import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    //HomeFragment 와 연결되는 뷰모델 관리
    @Provides
    HomeViewModel provideHomeViewModel(){
        return new HomeViewModel();
    };

    @Provides
    TreeIntegratedVO provideTreeIntegratedVO(){
        return new TreeIntegratedVO();
    }

    @Provides
    RegistTreeBasicInfoViewModel provideRegistTreeBasicInfoViewModel() {
        return new RegistTreeBasicInfoViewModel();
    };

    @Provides
    LoginUseCase provideLoginUseCase(){
        return new LoginUseCase(new LoginRepository());
    }

    @Provides
    UserUseCase provideUserUseCase(){
        return new UserUseCase(new UserRepository());
    };

    @Provides
    TreeImageUseCase provideImageUseCase(){
        return new TreeImageUseCase(new TreeImageRepository());
    }

    @Provides
    TreeDataListUseCase provideTreeDataListUseCase(){
        return new TreeDataListUseCase(new TreeDataListRepository());
    }

    @Provides
    TreeUseCase provideTreeUseCase(){
        return new TreeUseCase(new TreeRepository());
    }

    @Provides
    CompanyUseCase provideCompanyUseCase(){
        return new CompanyUseCase(new CompanyRepository());
    }


    @Provides
    TreeManagementUseCase provideTreeManagementUseCase(){
        return new TreeManagementUseCase(new TreeManagementRepository());
    }

    @Provides
    ProjectUseCase provideProjectUseCase(){
        return new ProjectUseCase(new ProjectRepository());
    }


    @Provides
    TreeHashtagUseCase provideTreeHashtagUseCase(){
        return new TreeHashtagUseCase(new TreeHashtagRepository());
    }

    @Provides
    AosConfigUseCase provideAosConfigUseCase(){
        return new AosConfigUseCase(new AosConfigRepository());
    }

}