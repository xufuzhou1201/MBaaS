package com.angkorteam.mbaas.server.template;

import com.angkorteam.framework.extension.wicket.markup.html.form.TimeTextField;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Date;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class TimePanel extends Panel {

    private final String name;

    private final Map<String, Object> fields;

    public TimePanel(String id, String name, Map<String, Object> modelObject) {
        super(id);
        this.name = name;
        this.fields = modelObject;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label label = new Label("label", this.name);
        this.add(label);
        TimeTextField field = new TimeTextField("field", new PropertyModel<>(this.fields, this.name));
        field.setType(Date.class);
        field.setLabel(Model.of(name));
        TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
        this.add(field);
        this.add(feedback);
    }
}
