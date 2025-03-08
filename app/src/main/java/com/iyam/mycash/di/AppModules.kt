package com.iyam.mycash.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.iyam.mycash.data.local.datastore.UserPreferenceDataSource
import com.iyam.mycash.data.local.datastore.UserPreferenceDataSourceImpl
import com.iyam.mycash.data.local.datastore.appDataStore
import com.iyam.mycash.data.local.db.AppDatabase
import com.iyam.mycash.data.local.db.datasource.DatabaseDataSource
import com.iyam.mycash.data.local.db.datasource.DatabaseDataSourceImpl
import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.datasource.ApiDataSourceImpl
import com.iyam.mycash.data.network.api.service.AuthInterceptor
import com.iyam.mycash.data.network.api.service.MyCashApiService
import com.iyam.mycash.data.repository.AuthRepository
import com.iyam.mycash.data.repository.AuthRepositoryImpl
import com.iyam.mycash.data.repository.CartRepository
import com.iyam.mycash.data.repository.CartRepositoryImpl
import com.iyam.mycash.data.repository.CategoryRepository
import com.iyam.mycash.data.repository.CategoryRepositoryImpl
import com.iyam.mycash.data.repository.OutletRepository
import com.iyam.mycash.data.repository.OutletRepositoryImpl
import com.iyam.mycash.data.repository.ProductRepository
import com.iyam.mycash.data.repository.ProductRepositoryImpl
import com.iyam.mycash.data.repository.SessionRepository
import com.iyam.mycash.data.repository.SessionRepositoryImpl
import com.iyam.mycash.data.repository.TransactionRepository
import com.iyam.mycash.data.repository.TransactionRepositoryImpl
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.manage.ManageViewModel
import com.iyam.mycash.ui.outlet.OutletViewModel
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.ui.resetpassword.ResetPasswordViewModel
import com.iyam.mycash.ui.settings.SettingsViewModel
import com.iyam.mycash.ui.signin.SignInViewModel
import com.iyam.mycash.ui.signup.SignUpViewModel
import com.iyam.mycash.utils.PreferenceDataStoreHelper
import com.iyam.mycash.utils.PreferenceDataStoreHelperImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModules {

    private val networkModule = module {
        single { AuthInterceptor(get()) }
        single { AppDatabase.getInstance(androidContext()) }
        single { get<AppDatabase>().cartDao() }
        single { ChuckerInterceptor(androidContext()) }
        single { MyCashApiService.invoke(get(), get()) }
    }

    private val localModule = module {
        single { androidContext().appDataStore }
        single<PreferenceDataStoreHelper> { PreferenceDataStoreHelperImpl(get()) }
    }

    private val dataSourceModule = module {
        single<ApiDataSource> { ApiDataSourceImpl(get()) }
        single<DatabaseDataSource> { DatabaseDataSourceImpl(get()) }
        single<UserPreferenceDataSource> { UserPreferenceDataSourceImpl(get()) }
    }

    private val repositoryModule = module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<OutletRepository> { OutletRepositoryImpl(get()) }
        single<CategoryRepository> { CategoryRepositoryImpl(get()) }
        single<ProductRepository> { ProductRepositoryImpl(get()) }
        single<SessionRepository> { SessionRepositoryImpl(get()) }
        single<TransactionRepository> { TransactionRepositoryImpl(get()) }
        single<CartRepository> { CartRepositoryImpl(get(), get()) }
    }

    private val utilsModule = module {
    }

    private val viewModelModule = module {
        viewModelOf(::SignUpViewModel)
        viewModelOf(::MainViewModel)
        viewModelOf(::SignInViewModel)
        viewModelOf(::ResetPasswordViewModel)
        viewModelOf(::OutletViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::ManageViewModel)
        viewModel { PointOfSaleViewModel(get(), get(), get()) }
    }

    val modules: List<Module> = listOf(
        networkModule,
        localModule,
        dataSourceModule,
        repositoryModule,
        viewModelModule,
        utilsModule
    )
}
