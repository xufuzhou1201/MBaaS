package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/13/16.
 */
public class MobileProvider extends JooqProvider {

    private MobileTable mobileTable = Tables.MOBILE.as("mobileTable");

    private UserTable userTable = Tables.USER.as("userTable");

    private TableLike<?> from;

    public MobileProvider() {
        this.from = mobileTable.join(userTable).on(mobileTable.USER_ID.eq(userTable.USER_ID));
        setSort("dateSeen", SortOrder.DESCENDING);
    }

    public Field<String> getLogin() {
        return this.userTable.LOGIN;
    }

    public Field<String> getMobileId() {
        return this.mobileTable.MOBILE_ID;
    }

    public Field<String> getClientIp() {
        return this.mobileTable.CLIENT_IP;
    }

    public Field<String> getPushToken() {
        return this.mobileTable.PUSH_TOKEN;
    }

    public Field<String> getUserAgent() {
        return this.mobileTable.USER_AGENT;
    }

    public Field<Date> getDateCreated() {
        return this.mobileTable.DATE_CREATED;
    }

    public Field<Date> getDateSeen() {
        return this.mobileTable.DATE_SEEN;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        return null;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}