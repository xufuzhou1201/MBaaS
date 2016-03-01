package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.jooq.IDSLContext;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.WicketTable;
import com.angkorteam.mbaas.server.Scope;
import com.angkorteam.mbaas.server.page.HomePage;
import com.angkorteam.mbaas.server.page.LoginPage;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.jooq.DSLContext;
import org.reflections.Reflections;

import java.util.*;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 */
public class Application extends AuthenticatedWebApplication implements IDSLContext {

    private transient Map<String, Session> sessions;

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();
        this.sessions = new WeakHashMap<>();
        getRequestCycleSettings().setBufferResponse(true);
        getRequestCycleSettings().setGatherExtendedBrowserInfo(true);
        initPageMount();
    }

    protected void initPageMount() {
        Reflections reflections = new Reflections(Scope.class.getPackage().getName());
        Set<Class<?>> pages = reflections.getTypesAnnotatedWith(Mount.class);
        Map<String, String> mounted = new HashMap<>();
        if (pages != null && !pages.isEmpty()) {
            for (Class<?> page : pages) {
                if (WebPage.class.isAssignableFrom(page)) {
                    Mount mount = page.getAnnotation(Mount.class);
                    if (mount.value() != null && !"".equals(mount.value())) {
                        String url = mount.value().startsWith("/") ? mount.value() : "/" + mount.value();
                        if (mounted.containsKey(url)) {
                            throw new WicketRuntimeException(url + " is ambiguous between " + page.getName() + " and " + mounted.get(url));
                        } else {
                            mounted.put(url, page.getName());
                            mountPage(mount.value(), (Class<WebPage>) page);
                        }
                    }
                }
            }
        }
    }

    @Override
    public final DSLContext getDSLContext() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getDSLContext();
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return Session.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    public void trackSession(String sessionId, Session session) {
        this.sessions.put(sessionId, session);
    }

    public void invalidate(String sessionId) {
        DSLContext context = getDSLContext();
        WicketTable wicketTable = Tables.WICKET.as("wicketTable");
        context.delete(wicketTable).where(wicketTable.SESSION_ID.eq(sessionId)).execute();
        Session session = this.sessions.remove(sessionId);
        if (session != null) {
            session.invalidateNow();
        }
    }
}