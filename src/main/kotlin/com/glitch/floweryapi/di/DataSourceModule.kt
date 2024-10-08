package com.glitch.floweryapi.di

import com.glitch.floweryapi.data.datasource.users.ClientsDataSource
import com.glitch.floweryapi.data.datasource.users.EmployeesDataSource
import com.glitch.floweryapi.data.datasource.users.PersonsDataSource
import com.glitch.floweryapi.data.datasourceimpl.users.ClientsDataSourceImpl
import com.glitch.floweryapi.data.datasourceimpl.users.EmployeesDataSourceImpl
import com.glitch.floweryapi.data.datasourceimpl.users.PersonsDataSourceImpl
import org.koin.dsl.module

val dataSourceModule = module {
    single<PersonsDataSource> {
        PersonsDataSourceImpl(db = get())
    }
    single<ClientsDataSource> {
        ClientsDataSourceImpl(db = get())
    }
    single<EmployeesDataSource> {
        EmployeesDataSourceImpl(db = get(), persons = get())
    }
}