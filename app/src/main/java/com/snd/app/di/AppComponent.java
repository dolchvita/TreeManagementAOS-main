package com.snd.app.di;


import com.snd.app.domain.tree.vo.TreeIntegratedVO;
import com.snd.app.repository.company.CompanyUseCase;
import com.snd.app.repository.config.AosConfigUseCase;
import com.snd.app.repository.login.LoginUseCase;
import com.snd.app.repository.project.ProjectUseCase;
import com.snd.app.repository.tree.TreeUseCase;
import com.snd.app.repository.tree.treeHashtag.TreeHashtagUseCase;
import com.snd.app.repository.tree.treeManagement.TreeManagementUseCase;
import com.snd.app.repository.user.UserUseCase;
import com.snd.app.repository.tree.treeDataList.TreeDataListUseCase;
import com.snd.app.ui.home.HomeViewModel;
import com.snd.app.repository.tree.treeImage.TreeImageUseCase;

import javax.inject.Singleton;
import dagger.Component;

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {

    HomeViewModel homeViewModel();      //의존성 주입 성공

    TreeIntegratedVO treeIntegratedVO();

    LoginUseCase loginUseCase();

    UserUseCase userUseCase();

    TreeImageUseCase treeImageUseCase();

    TreeDataListUseCase treeDataListUseCase();

    TreeUseCase treeUseCase();

    CompanyUseCase companyUseCase();

    TreeManagementUseCase treeManagementUseCase();

    ProjectUseCase projectUseCase();

    TreeHashtagUseCase treeHashtagUseCase();

    AosConfigUseCase aosConfigUseCase();

    @Component.Builder
    interface Builder {
        Builder appModule(AppModule appModule);
        AppComponent build();
    }



}