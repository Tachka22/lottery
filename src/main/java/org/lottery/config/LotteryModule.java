package org.lottery.config;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import org.lottery.model.UserActionEvent;
import org.lottery.repository.*;
import org.lottery.service.*;

import javax.sql.DataSource;
import java.util.function.Consumer;

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

        //Audit
        bind(AuditService.class).asEagerSingleton();
        Multibinder<Consumer<UserActionEvent>> binder = Multibinder.newSetBinder(binder(), new TypeLiteral<Consumer<UserActionEvent>>(){});

        binder.addBinding().to(DatabaseAuditListener.class);
    }
}
