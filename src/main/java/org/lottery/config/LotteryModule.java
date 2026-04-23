package org.lottery.config;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.lottery.repository.*;
import org.lottery.service.AuthService;
import org.lottery.service.AuthServiceImpl;
import org.lottery.service.DrawService;
import org.lottery.service.DrawServiceImpl;
import org.lottery.service.TicketService;
import org.lottery.service.TicketServiceImpl;
import org.lottery.service.ReportService;
import org.lottery.service.ReportServiceImpl;

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

        //repositories
        bind(DrawRepository.class).to(DrawRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(LotteryTypeRepository.class).to(LotteryTypeRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(UserRepository.class).to(UserRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(TicketRepository.class).to(TicketRepositoryImpl.class).in(Scopes.SINGLETON);
    }
}
