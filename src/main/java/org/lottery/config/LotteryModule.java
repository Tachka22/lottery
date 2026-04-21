package org.lottery.config;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.lottery.repository.DrawRepository;
import org.lottery.repository.DrawRepositoryImpl;
import org.lottery.repository.LotteryTypeRepository;
import org.lottery.repository.LotteryTypeRepositoryImpl;
import org.lottery.service.DrawService;
import org.lottery.service.DrawServiceImpl;

import javax.sql.DataSource;

public class LotteryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DataSource.class).toInstance(DatabaseConfig.getDataSource());

        //services
        bind(DrawService.class).to(DrawServiceImpl.class).in(Scopes.SINGLETON);

        //repositories
        bind(DrawRepository.class).to(DrawRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(LotteryTypeRepository.class).to(LotteryTypeRepositoryImpl.class).in(Scopes.SINGLETON);
    }
}
