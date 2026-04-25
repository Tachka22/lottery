package org.lottery.config;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.lottery.repository.*;
import org.lottery.service.*;

import javax.sql.DataSource;

public class LotteryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DataSource.class).toInstance(DatabaseConfig.getDataSource());

        //services
        bind(DrawService.class).to(DrawServiceImpl.class).in(Scopes.SINGLETON);
        bind(AuthService.class).to(AuthServiceImpl.class).in(Scopes.SINGLETON);
        bind(TicketService.class).to(TicketServiceImpl.class).in(Scopes.SINGLETON);
        bind(ReportService.class).to(ReportServiceImpl.class).in(Scopes.SINGLETON);
        bind(LotteryTypeService.class).to(LotteryTypeServiceImpl.class).in(Scopes.SINGLETON);

        //repositories
        bind(DrawRepository.class).to(DrawRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(LotteryTypeRepository.class).to(LotteryTypeRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(UserRepository.class).to(UserRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(TicketRepository.class).to(TicketRepositoryImpl.class).in(Scopes.SINGLETON);
    }
}
