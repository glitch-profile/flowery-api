package com.glitch.floweryapi.di

import com.glitch.floweryapi.data.datasource.PersonsDataSource
import com.glitch.floweryapi.data.datasourceimpl.users.PersonsDataSourceImpl
import org.koin.dsl.module

val dataSourceModule = module {
    single<PersonsDataSource> {
        PersonsDataSourceImpl(db = get())
    }
}