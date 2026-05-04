package org.lottery.config;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import org.lottery.model.Event;
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
        bind(LotteryTypeService.class).to(LotteryTypeServiceImpl.class).in(Scopes.SINGLETON);
        bind(UserActionsService.class).to(UserActionsServiceImpl.class).in(Scopes.SINGLETON);
        bind(LotteryGeneratorService.class).asEagerSingleton();

        //repositories
        bind(DrawRepository.class).to(DrawRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(LotteryTypeRepository.class).to(LotteryTypeRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(UserRepository.class).to(UserRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(TicketRepository.class).to(TicketRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(UserActionRepository.class).to(UserActionRepositoryImpl.class).in(Scopes.SINGLETON);

        //Audit
        bind(AuditService.class).asEagerSingleton();
        Multibinder<Consumer<Event>> binder = Multibinder.newSetBinder(binder(), new TypeLiteral<Consumer<Event>>(){});

        binder.addBinding().to(UserActionsConsumer.class);
    }
}
