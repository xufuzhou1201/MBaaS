package com.angkorteam.mbaas.server.bean;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.GroovyTable;
import com.angkorteam.mbaas.model.entity.tables.PageRoleTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.GroovyPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.server.Session;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by socheat on 11/3/16.
 */
public class AuthorizationStrategy implements IAuthorizationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationStrategy.class);

    private final DSLContext context;

    private final System system;

    public AuthorizationStrategy(DSLContext context, System system) {
        this.context = context;
        this.system = system;
    }

    @Override
    public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {
        Roles roles = Session.get().getRoles();
        if (roles != null && !roles.isEmpty()) {
            Configuration configuration = system.getConfiguration();
            if (roles.hasRole(configuration.getString(Configuration.ROLE_ADMINISTRATOR))) {
                return true;
            }
        }
        if (roles == null) {
            roles = new Roles();
        }
        PageTable pageTable = Tables.PAGE.as("pageTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");
        GroovyPojo groovy = context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.JAVA_CLASS.eq(componentClass.getName())).fetchOneInto(GroovyPojo.class);
        PagePojo page = null;
        if (groovy != null) {
            page = context.select(pageTable.fields()).from(pageTable).where(pageTable.GROOVY_ID.eq(groovy.getGroovyId())).fetchOneInto(PagePojo.class);
        }
        if (page != null) {
            PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
            RoleTable roleTable = Tables.ROLE.as("roleTable");
            List<String> pageRoles = context.select(roleTable.NAME).from(roleTable).innerJoin(pageRoleTable).on(roleTable.ROLE_ID.eq(pageRoleTable.ROLE_ID)).and(pageRoleTable.PAGE_ID.eq(page.getPageId())).fetchInto(String.class);
            if (pageRoles != null && !pageRoles.isEmpty()) {
                Roles r = new Roles();
                r.addAll(pageRoles);
                return roles.hasAnyRole(r);
            }
        }
        return true;
    }

    @Override
    public boolean isActionAuthorized(Component component, Action action) {
        return true;
    }

    @Override
    public boolean isResourceAuthorized(IResource resource, PageParameters parameters) {
        return true;
    }

}
